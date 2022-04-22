package toy.cookingstar.service.following;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.cookingstar.entity.Following;
import toy.cookingstar.entity.Member;
import toy.cookingstar.repository.FollowingRepository;
import toy.cookingstar.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowingService {

    private final MemberRepository memberRepository;
    private final FollowingRepository followingRepository;

    @Transactional
    public void create(Long loginUserId, String followedUserId) throws Exception {
        Member loginUser = memberRepository.findById(loginUserId).orElseThrow(IllegalArgumentException::new);
        Member followedMember = memberRepository.findByUserId(followedUserId);

        if (followedMember == null) {
            throw new IllegalArgumentException();
        }

        //Following 객체 생성
        Following following = Following.createFollowing(loginUser, followedMember);

        followingRepository.save(following);
    }

    public int countFollowers(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
        return followingRepository.countFollowers(member);
    }

    public int countFollowings(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
        return followingRepository.countFollowings(member);
    }

    public Slice<Member> getFollowers(String userId, int page, int size) throws Exception {
        Member member = memberRepository.findByUserId(userId);
        if (member == null) {
            throw new IllegalArgumentException();
        }
        Pageable pageable = PageRequest.of(page, size);
        Slice<Following> followerSlice = followingRepository.findFollowersByFollowedMember(member, pageable);
        followerSlice.forEach(following -> following.getFollower().getUserId());
        return followerSlice.map(Following::getFollower);
    }

    public Slice<Member> getFollowings(String userId, int page, int size) throws Exception {
        Member member = memberRepository.findByUserId(userId);
        if (member == null) {
            throw new IllegalArgumentException();
        }
        Pageable pageable = PageRequest.of(page, size);
        Slice<Following> followingSlice = followingRepository.findFollowingsByFollower(member, pageable);
        followingSlice.forEach(following -> following.getFollowedMember().getUserId());
        return followingSlice.map(Following::getFollowedMember);
    }

    public boolean checkForFollowing(Long memberId, String userId) throws Exception {
        Member loginMember = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
        Member searchedUser = memberRepository.findByUserId(userId);
        if (searchedUser == null) {
            throw new IllegalArgumentException();
        }
        return followingRepository.existsByFollowerAndFollowedMember(loginMember, searchedUser);
    }

    public boolean checkForFollowed(Long memberId, String userId) {
        Member loginMember = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
        Member searchedUser = memberRepository.findByUserId(userId);
        if (searchedUser == null) {
            throw new IllegalArgumentException();
        }
        return followingRepository.existsByFollowerAndFollowedMember(searchedUser, loginMember);
    }

    @Transactional
    public void deleteFollowing(Long memberId, String userId) throws Exception {
        Member loginMember = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
        Member searchedUser = memberRepository.findByUserId(userId);
        if (searchedUser == null) {
            throw new IllegalArgumentException();
        }

        Following following = followingRepository.findByFollowerAndFollowedMember(loginMember, searchedUser);
        if (following == null) {
            throw new IllegalArgumentException();
        }

        followingRepository.delete(following);
    }
}