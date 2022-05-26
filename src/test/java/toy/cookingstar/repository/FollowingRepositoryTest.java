package toy.cookingstar.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import toy.cookingstar.entity.Member;
import toy.cookingstar.entity.Following;

import javax.persistence.EntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class FollowingRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    FollowingRepository followingRepository;
    @Autowired
    EntityManager em;

    @Test
    @DisplayName("팔로잉 관계 저장 테스트")
    void saveFollowingTest() throws Exception {
        //given
        Member follower = Member.builder().userId("follower").build();
        memberRepository.save(follower);

        Member followedMember = Member.builder().userId("follower").build();
        memberRepository.save(followedMember);

        Following following = Following.createFollowing(follower, followedMember);

        //when
        Following savedFollowing = followingRepository.save(following);

        //then
        assertEquals(follower.getUserId(), savedFollowing.getFollower().getUserId());
        assertEquals(followedMember.getUserId(), savedFollowing.getFollowedMember().getUserId());
        assertNotNull(savedFollowing.getId());
    }

    @Test
    @DisplayName("전체 팔로워 수 조회 테스트")
    void countFollowersTest() throws Exception {
        //given
        Member follower1 = Member.builder().userId("follower1").build();
        Member follower2 = Member.builder().userId("follower2").build();
        memberRepository.save(follower1);
        memberRepository.save(follower2);

        Member targetMember = Member.builder().userId("targetMember").build();
        memberRepository.save(targetMember);

        Following following1 = Following.createFollowing(follower1, targetMember);
        Following following2 = Following.createFollowing(follower2, targetMember);

        followingRepository.save(following1);
        followingRepository.save(following2);

        //when
        int count = followingRepository.countFollowers(targetMember);

        //then
        assertEquals(2, count);
    }

    @Test
    @DisplayName("전체 팔로잉 수 조회 테스트")
    void countFollowingsTest() throws Exception {
        //given
        Member targetMember = Member.builder().userId("follower1").build();
        memberRepository.save(targetMember);

        Member followedMember1 = Member.builder().userId("followedMember1").build();
        Member followedMember2 = Member.builder().userId("followedMember2").build();
        memberRepository.save(followedMember1);
        memberRepository.save(followedMember2);

        Following following1 = Following.createFollowing(targetMember, followedMember1);
        Following following2 = Following.createFollowing(targetMember, followedMember2);
        followingRepository.save(following1);
        followingRepository.save(following2);

        //when
        int count = followingRepository.countFollowings(targetMember);

        //then
        assertEquals(2, count);
    }

    @Test
    @DisplayName("FollowedMember로 팔로워 조회 테스트")
    void findFollowersByFollowedMemberTest() throws Exception {
        //given
        Member follower1 = Member.builder().userId("follower1").build();
        Member follower2 = Member.builder().userId("follower2").build();
        Member savedFollower1 = memberRepository.save(follower1);
        Member savedFollower2 = memberRepository.save(follower2);

        Member targetMember = Member.builder().userId("targetMember").build();
        Member savedTargetMember = memberRepository.save(targetMember);

        Following following1 = Following.createFollowing(savedFollower1, savedTargetMember);
        Following following2 = Following.createFollowing(savedFollower2, savedTargetMember);
        followingRepository.save(following1);
        followingRepository.save(following2);

        em.flush();
        em.clear();

        Pageable pageable = PageRequest.of(0, 3);

        //when
        Slice<Following> followerSlice = followingRepository.findFollowersByFollowedMember(targetMember, pageable);

        //then
        assertEquals(2, followerSlice.getNumberOfElements());
        assertFalse(followerSlice.hasNext());
        assertEquals(savedFollower1.getUserId(), followerSlice.getContent().get(0).getFollower().getUserId());
        assertEquals(savedFollower2.getUserId(), followerSlice.getContent().get(1).getFollower().getUserId());
    }

    @Test
    @DisplayName("Follower로 팔로잉 조회 테스트")
    void findFollowingsByFollowerTest() throws Exception {
        //given
        Member targetMember = Member.builder().userId("targetMember").build();
        Member savedTargetMember = memberRepository.save(targetMember);

        Member followedMember1 = Member.builder().userId("followedMember1").build();
        Member followedMember2 = Member.builder().userId("followedMember2").build();
        Member savedFollowedMember1 = memberRepository.save(followedMember1);
        Member savedFollowedMember2 = memberRepository.save(followedMember2);

        Following following1 = Following.createFollowing(savedTargetMember, savedFollowedMember1);
        Following following2 = Following.createFollowing(savedTargetMember, savedFollowedMember2);
        followingRepository.save(following1);
        followingRepository.save(following2);

        em.flush();
        em.clear();

        Pageable pageable = PageRequest.of(0, 3);

        //when
        Slice<Following> followingSlice = followingRepository.findFollowingsByFollower(savedTargetMember, pageable);

        //then
        assertEquals(2, followingSlice.getNumberOfElements());
        assertFalse(followingSlice.hasNext());
        assertEquals(savedFollowedMember1.getUserId(), followingSlice.getContent().get(0).getFollowedMember().getUserId());
        assertEquals(savedFollowedMember2.getUserId(), followingSlice.getContent().get(1).getFollowedMember().getUserId());
    }

    @Nested
    @DisplayName("Follower 및 FollowedMember로 팔로우 관계 존재 확인 테스트")
    class ExistsByFollowerAndFollowedMemberTest {

        @Test
        @DisplayName("Following 존재")
        void existsByFollowerAndFollowedMemberTest_FollowingExists() throws Exception {
            //given
            Member follower = Member.builder().userId("follower").build();
            Member savedFollower = memberRepository.save(follower);

            Member followedMember = Member.builder().userId("followedMember").build();
            Member savedFollowedMember = memberRepository.save(followedMember);

            Following following = Following.createFollowing(savedFollower, savedFollowedMember);
            followingRepository.save(following);

            em.flush();
            em.clear();

            //when
            Boolean existenceCheck = followingRepository.existsByFollowerAndFollowedMember(savedFollower, savedFollowedMember);

            //then
            assertTrue(existenceCheck);
        }

        @Test
        @DisplayName("Following 존재하지 않음")
        void existsByFollowerAndFollowedMemberTest_FollowingDoesNotExist() throws Exception {
            //given
            Member member1 = Member.builder().userId("member1").build();
            Member savedMember1 = memberRepository.save(member1);

            Member member2 = Member.builder().userId("member2").build();
            Member savedMember2 = memberRepository.save(member2);

            em.flush();
            em.clear();

            //when
            Boolean existenceCheck = followingRepository.existsByFollowerAndFollowedMember(savedMember1, savedMember2);

            //then
            assertFalse(existenceCheck);
        }
    }

    @Test
    @DisplayName("Follower 및 FollowedMember로 Following 조회 테스트")
    void findByFollowerAndFollowedMemberTest() throws Exception {
        //given
        Member follower = Member.builder().userId("follower").build();
        Member savedFollower = memberRepository.save(follower);

        Member followedMember = Member.builder().userId("followedMember").build();
        Member savedFollowedMember = memberRepository.save(followedMember);

        Following following = Following.createFollowing(savedFollower, savedFollowedMember);
        Following savedFollowing = followingRepository.save(following);
        em.flush();
        em.clear();

        //when
        Long followingId = followingRepository.findIdByFollowerAndFollowedMember(savedFollower, savedFollowedMember);

        Following foundFollowing = followingRepository.findById(followingId).orElseThrow(IllegalArgumentException::new);

        //then
        assertEquals(savedFollowing.getId(), foundFollowing.getId());
        assertEquals(savedFollowing.getFollower().getUserId(), foundFollowing.getFollower().getUserId());
        assertEquals(savedFollowing.getFollowedMember().getUserId(), foundFollowing.getFollowedMember().getUserId());
    }

    @Test
    @DisplayName("언팔로우 테스트")
    void deleteFollowingTest() throws Exception {
        //given
        Member follower = Member.builder().userId("follower").build();
        Member savedFollower = memberRepository.save(follower);

        Member followedMember = Member.builder().userId("followedMember").build();
        Member savedFollowedMember = memberRepository.save(followedMember);

        Following following = Following.createFollowing(savedFollower, savedFollowedMember);
        Long savedFollowingId = followingRepository.save(following).getId();
        em.flush();
        em.clear();

        Long foundFollowingId = followingRepository.findIdByFollowerAndFollowedMember(savedFollower, savedFollowedMember);

        //when
        followingRepository.deleteById(foundFollowingId);
        Optional<Following> deletedFollowing = followingRepository.findById(savedFollowingId);

        //then
        assertEquals(Optional.empty(), deletedFollowing);
    }


}