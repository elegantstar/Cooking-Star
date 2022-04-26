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

    /**
     * memberId로 전체 팔로워 수 조회
     */
    public int countFollowers(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
        return followingRepository.countFollowers(member);
    }

    /**
     * userId로 전체 팔로워 수 조회
     */
    public int countFollowersByUserId(String userId) throws Exception {
        Member member = memberRepository.findByUserId(userId);
        if (member == null) {
            throw new IllegalArgumentException();
        }
        return followingRepository.countFollowers(member);
    }

    /**
     * memberId로 전체 팔로잉 수 조회
     */
    public int countFollowings(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
        return followingRepository.countFollowings(member);
    }

    /**
     * userId로 전체 팔로잉 수 조회
     */
    public int countFollowingsByUserId(String userId) throws Exception {
        Member member = memberRepository.findByUserId(userId);
        if (member == null) {
            throw new IllegalArgumentException();
        }
        return followingRepository.countFollowings(member);
    }

    /**
     * 팔로워 리스트 조회
     */
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

    /**
     * 팔로잉 리스트 조회
     */
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

    /**
     * 로그인 멤버의 특정 유저 팔로잉 여부 확인
     */
    public boolean checkForFollowing(Long loginMemberId, String userId) throws Exception {
        Member loginMember = memberRepository.findById(loginMemberId).orElseThrow(IllegalArgumentException::new);
        Member searchedUser = memberRepository.findByUserId(userId);
        if (searchedUser == null) {
            throw new IllegalArgumentException();
        }
        return followingRepository.existsByFollowerAndFollowedMember(loginMember, searchedUser);
    }

    /**
     * 특정 유저의 로그인 멤버 팔로잉 여부 확인
     */
    public boolean checkForFollowed(Long loginMemberId, String userId) {
        Member loginMember = memberRepository.findById(loginMemberId).orElseThrow(IllegalArgumentException::new);
        Member searchedUser = memberRepository.findByUserId(userId);
        if (searchedUser == null) {
            throw new IllegalArgumentException();
        }
        return followingRepository.existsByFollowerAndFollowedMember(searchedUser, loginMember);
    }

    /**
     * 팔로잉 관계 삭제
     */
    @Transactional
    public void deleteFollowing(String followingUserId, String followedUserId) throws Exception {
        Member member = memberRepository.findByUserId(followingUserId);
        if (member == null) {
            throw new IllegalArgumentException();
        }

        Member followedUser = memberRepository.findByUserId(followedUserId);
        if (followedUser == null) {
            throw new IllegalArgumentException();
        }

        Following following = followingRepository.findByFollowerAndFollowedMember(member, followedUser);
        if (following == null) {
            throw new IllegalArgumentException();
        }

        followingRepository.delete(following);
    }
}