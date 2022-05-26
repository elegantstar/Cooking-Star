package toy.cookingstar.service.following;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import toy.cookingstar.entity.Following;
import toy.cookingstar.entity.Member;
import toy.cookingstar.repository.FollowingRepository;
import toy.cookingstar.repository.MemberRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FollowingServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    FollowingRepository followingRepository;

    @InjectMocks
    FollowingService followingService;

    @Nested
    @DisplayName("Following 관계 생성 테스트")
    class CreateFollowingTest {

        @Test
        @DisplayName("성공")
        void createFollowingSuccessTest() throws Exception {
            //given
            Member loginUser = mock(Member.class);
            given(loginUser.getId()).willReturn(1L);
            given(memberRepository.findById(1L)).willReturn(Optional.of(loginUser));

            Member followedMember = mock(Member.class);
            given(followedMember.getUserId()).willReturn("testId");
            given(memberRepository.findByUserId("testId")).willReturn(followedMember);

            ArgumentCaptor<Following> captor = ArgumentCaptor.forClass(Following.class);

            //when
            followingService.create(1L, "testId");

            //then
            then(memberRepository).should(times(1)).findById(anyLong());
            then(memberRepository).should(times(1)).findByUserId(anyString());
            then(followingRepository).should(times(1)).save(captor.capture());

            assertEquals(loginUser.getId(), captor.getValue().getFollower().getId());
            assertEquals(followedMember.getUserId(), captor.getValue().getFollowedMember().getUserId());
        }

        @Test
        @DisplayName("실패_존재하지 않는 로그인 유저")
        void createFollowingFailureTest_LoginUserDoesNotExist() throws Exception {
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> followingService.create(1L, "testId"));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(memberRepository).should(never()).findByUserId(anyString());
            then(followingRepository).should(never()).save(any(Following.class));
        }

        @Test
        @DisplayName("실패_존재하지 않는 followedMember")
        void createFollowingFailureTest_FollowedMemberDoesNotExist() throws Exception {
            //given
            Member loginUser = mock(Member.class);
            given(memberRepository.findById(1L)).willReturn(Optional.of(loginUser));
            given(memberRepository.findByUserId(anyString())).willReturn(null);

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> followingService.create(1L, "testId"));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(memberRepository).should(times(1)).findByUserId(anyString());
            then(followingRepository).should(never()).save(any(Following.class));
        }
    }

    @Nested
    @DisplayName("전체 팔로워 수 조회 테스트")
    class CountFollowersTest {

        @Test
        @DisplayName("성공")
        void countFollowersSuccessTest() throws Exception {
            //given
            Member member = mock(Member.class);
            given(member.getId()).willReturn(1L);
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(followingRepository.countFollowers(member)).willReturn(13);

            ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

            //when
            int count = followingService.countFollowers(1L);

            //then
            then(memberRepository).should(times(1)).findById(anyLong());
            then(followingRepository).should(times(1)).countFollowers(captor.capture());
            assertEquals(member.getId(), captor.getValue().getId());
            assertEquals(13, count);
        }

        @Test
        @DisplayName("실패_존재하지 않는 memberId")
        void countFollowersFailureTest_MemberIdDoesNotExist() throws Exception {
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> followingService.countFollowers(1L));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(followingRepository).should(never()).countFollowers(any(Member.class));
        }
    }

    @Nested
    @DisplayName("전체 팔로잉 수 조회 테스트")
    class CountFollowingsTest {

        @Test
        @DisplayName("성공")
        void countFollowingsSuccessTest() throws Exception {
            //given
            Member member = mock(Member.class);
            given(member.getId()).willReturn(1L);
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(followingRepository.countFollowings(member)).willReturn(7);

            ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

            //when
            int count = followingService.countFollowings(1L);

            //then
            then(memberRepository).should(times(1)).findById(anyLong());
            then(followingRepository).should(times(1)).countFollowings(captor.capture());
            assertEquals(member.getId(), captor.getValue().getId());
            assertEquals(7, count);
        }

        @Test
        @DisplayName("실패_존재하지 않는 memberId")
        void countFollowingsFailureTest_MemberIdDoesNotExist() throws Exception {
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> followingService.countFollowings(1L));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(followingRepository).should(never()).countFollowings(any(Member.class));
        }
    }

    @Nested
    @DisplayName("팔로워 Silice 조회")
    class GetFollowersTest {

        @Test
        @DisplayName("성공")
        void getFollowersSuccessTest() throws Exception {
            //given
            Member targetUser = Member.builder().userId("targetUser").build();
            given(memberRepository.findByUserId("targetUser")).willReturn(targetUser);

            Member follower1 = Member.builder().userId("follower1").build();
            Member follower2 = Member.builder().userId("follower2").build();
            Member follower3 = Member.builder().userId("follower3").build();

            Following following1 = Following.createFollowing(follower1, targetUser);
            Following following2 = Following.createFollowing(follower2, targetUser);
            Following following3 = Following.createFollowing(follower3, targetUser);

            SliceImpl<Following> slice = new SliceImpl<>(Arrays.asList(following1, following2, following3));
            given(followingRepository.findFollowersByFollowedMember(eq(targetUser), any(Pageable.class))).willReturn(slice);

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

            //when
            Slice<Member> result = followingService.getFollowers("targetUser", 0, 6);

            //then
            then(memberRepository).should(times(1)).findByUserId(anyString());
            then(followingRepository).should(times(1))
                    .findFollowersByFollowedMember(any(Member.class), captor.capture());
            assertEquals(0, captor.getValue().getOffset());
            assertEquals(6, captor.getValue().getPageSize());

            assertEquals(3, result.getNumberOfElements());
            assertEquals(follower1.getUserId(), result.getContent().get(0).getUserId());
            assertEquals(follower2.getUserId(), result.getContent().get(1).getUserId());
            assertEquals(follower3.getUserId(), result.getContent().get(2).getUserId());
        }

        @Test
        @DisplayName("실패_존재하지 않는 userId")
        void getFollowersFailureTest_UserIdDoesNotExist() throws Exception {
            //given
            given(memberRepository.findByUserId(anyString())).willReturn(null);

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> followingService.getFollowers("testId", 0, 6));
            then(memberRepository).should(times(1)).findByUserId(anyString());
            then(followingRepository).should(never()).findFollowersByFollowedMember(any(Member.class), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("팔로잉 Silice 조회")
    class GetFollowingsTest {

        @Test
        @DisplayName("성공")
        void getFollowingsSuccessTest() throws Exception {
            //given
            Member targetUser = Member.builder().userId("targetUser").build();
            given(memberRepository.findByUserId("targetUser")).willReturn(targetUser);

            Member followedMember1 = Member.builder().userId("followedMember1").build();
            Member followedMember2 = Member.builder().userId("followedMember2").build();
            Member followedMember3 = Member.builder().userId("followedMember3").build();

            Following following1 = Following.createFollowing(targetUser, followedMember1);
            Following following2 = Following.createFollowing(targetUser, followedMember2);
            Following following3 = Following.createFollowing(targetUser, followedMember3);

            SliceImpl<Following> slice = new SliceImpl<>(Arrays.asList(following1, following2, following3));
            given(followingRepository.findFollowingsByFollower(eq(targetUser), any(Pageable.class))).willReturn(slice);

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

            //when
            Slice<Member> result = followingService.getFollowings("targetUser", 0, 6);

            //then
            then(memberRepository).should(times(1)).findByUserId(anyString());
            then(followingRepository).should(times(1))
                    .findFollowingsByFollower(any(Member.class), captor.capture());
            assertEquals(0, captor.getValue().getOffset());
            assertEquals(6, captor.getValue().getPageSize());

            assertEquals(3, result.getNumberOfElements());
            assertEquals(followedMember1.getUserId(), result.getContent().get(0).getUserId());
            assertEquals(followedMember2.getUserId(), result.getContent().get(1).getUserId());
            assertEquals(followedMember3.getUserId(), result.getContent().get(2).getUserId());
        }

        @Test
        @DisplayName("실패_존재하지 않는 userId")
        void getFollowingsFailureTest_UserIdDoesNotExist() throws Exception {
            //given
            given(memberRepository.findByUserId(anyString())).willReturn(null);

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> followingService.getFollowings("testId", 0, 6));
            then(memberRepository).should(times(1)).findByUserId(anyString());
            then(followingRepository).should(never()).findFollowingsByFollower(any(Member.class), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("로그인 유저의 특정 유저 팔로잉 여부 확인 테스트")
    class CheckForFollowingTest {

        @Test
        @DisplayName("성공")
        void checkForFollowingSuccessTest() throws Exception {
            //given
            Member loginMember = mock(Member.class);
            given(memberRepository.findById(1L)).willReturn(Optional.of(loginMember));

            Member followedMember = mock(Member.class);
            given(memberRepository.findByUserId("test_user")).willReturn(followedMember);

            given(followingRepository.existsByFollowerAndFollowedMember(loginMember, followedMember)).willReturn(true);

            //when
            boolean following = followingService.checkForFollowing(1L, "test_user");

            //then
            then(memberRepository).should(times(1)).findById(anyLong());
            then(memberRepository).should(times(1)).findByUserId(anyString());
            then(followingRepository).should(times(1))
                    .existsByFollowerAndFollowedMember(any(Member.class), any(Member.class));
            assertTrue(following);
        }

        @Test
        @DisplayName("실패_존재하지 않는 memberId")
        void checkForFollowingFailureTest_MemberIdDoesNotExist() throws Exception {
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> followingService.checkForFollowing(1L, "test_user"));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(memberRepository).should(never()).findByUserId(anyString());
            then(followingRepository).should(never())
                    .existsByFollowerAndFollowedMember(any(Member.class), any(Member.class));
        }

        @Test
        @DisplayName("실패_존재하지 않는 userId")
        void checkForFollowingFailureTest_UserIdDoesNotExist() throws Exception {
            //given
            Member loginMember = mock(Member.class);
            given(memberRepository.findById(1L)).willReturn(Optional.of(loginMember));
            given(memberRepository.findByUserId(anyString())).willReturn(null);

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> followingService.checkForFollowing(1L, "test_user"));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(memberRepository).should(times(1)).findByUserId(anyString());
            then(followingRepository).should(never())
                    .existsByFollowerAndFollowedMember(any(Member.class), any(Member.class));
        }
    }

    @Nested
    @DisplayName("특정 유저의 로그인 유저 팔로잉 여부 확인 테스트")
    class CheckForFollowedTest {

        @Test
        @DisplayName("성공")
        void checkForFollowedSuccessTest() throws Exception {
            //given
            Member loginMember = mock(Member.class);
            given(memberRepository.findById(1L)).willReturn(Optional.of(loginMember));

            Member follower = mock(Member.class);
            given(memberRepository.findByUserId("test_user")).willReturn(follower);

            given(followingRepository.existsByFollowerAndFollowedMember(follower, loginMember)).willReturn(true);

            //when
            boolean following = followingService.checkForFollowed(1L, "test_user");

            //then
            then(memberRepository).should(times(1)).findById(anyLong());
            then(memberRepository).should(times(1)).findByUserId(anyString());
            then(followingRepository).should(times(1))
                    .existsByFollowerAndFollowedMember(any(Member.class), any(Member.class));
            assertTrue(following);
        }

        @Test
        @DisplayName("실패_존재하지 않는 memberId")
        void checkForFollowedFailureTest_MemberIdDoesNotExist() throws Exception {
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> followingService.checkForFollowed(1L, "test_user"));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(memberRepository).should(never()).findByUserId(anyString());
            then(followingRepository).should(never())
                    .existsByFollowerAndFollowedMember(any(Member.class), any(Member.class));
        }

        @Test
        @DisplayName("실패_존재하지 않는 userId")
        void checkForFollowedFailureTest_UserIdDoesNotExist() throws Exception {
            //given
            Member loginMember = mock(Member.class);
            given(memberRepository.findById(1L)).willReturn(Optional.of(loginMember));
            given(memberRepository.findByUserId(anyString())).willReturn(null);

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> followingService.checkForFollowed(1L, "test_user"));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(memberRepository).should(times(1)).findByUserId(anyString());
            then(followingRepository).should(never())
                    .existsByFollowerAndFollowedMember(any(Member.class), any(Member.class));
        }
    }

    @Nested
    @DisplayName("팔로잉 삭제(언팔로잉) 테스트")
    class DeleteFollowingTest {

        @Test
        @DisplayName("성공")
        void deleteFollowingSuccessTest() throws Exception {
            //given
            Member member = mock(Member.class);
            given(memberRepository.findByUserId("followingMember")).willReturn(member);

            Member followedMember = mock(Member.class);
            given(memberRepository.findByUserId("followedMember")).willReturn(followedMember);

            given(followingRepository.findIdByFollowerAndFollowedMember(member, followedMember)).willReturn(1L);

            ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

            //when
            followingService.deleteFollowing("followingMember", "followedMember");

            //then
            then(memberRepository).should(times(2)).findByUserId(anyString());
            then(followingRepository).should(times(1))
                    .findIdByFollowerAndFollowedMember(any(Member.class), any(Member.class));
            then(followingRepository).should(times(1)).deleteById(captor.capture());

            assertEquals(1L, captor.getValue());
        }

        @Test
        @DisplayName("실패_존재하지 않는 memberId")
        void deleteFollowingFailureTest_MemberIdDoesNotExist() throws Exception {
            //given
            given(memberRepository.findByUserId(anyString())).willReturn(null);

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> followingService.deleteFollowing("followingMember", "followedMember"));
            then(memberRepository).should(times(1)).findByUserId(anyString());
            then(followingRepository).should(never()).findIdByFollowerAndFollowedMember(any(Member.class), any(Member.class));
            then(followingRepository).should(never()).delete(any(Following.class));
        }

        @Test
        @DisplayName("실패_존재하지 않는 userId")
        void deleteFollowingFailureTest_UserIdDoesNotExist() throws Exception {
            //given
            Member member = mock(Member.class);
            given(memberRepository.findByUserId("followingMember")).willReturn(member);
            given(memberRepository.findByUserId("followedMember")).willReturn(null);

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> followingService.deleteFollowing("followingMember", "followedMember"));
            then(memberRepository).should(times(2)).findByUserId(anyString());
            then(followingRepository).should(never()).findIdByFollowerAndFollowedMember(any(Member.class), any(Member.class));
            then(followingRepository).should(never()).delete(any(Following.class));
        }

        @Test
        @DisplayName("실패_존재하지 않는 Following")
        void deleteFollowingFailureTest_FollowingDoesNotExist() throws Exception {
            //given
            Member member = mock(Member.class);
            given(memberRepository.findByUserId("followingMember")).willReturn(member);

            Member followedMember = mock(Member.class);
            given(memberRepository.findByUserId("followedMember")).willReturn(followedMember);

            given(followingRepository.findIdByFollowerAndFollowedMember(member, followedMember)).willReturn(null);

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> followingService.deleteFollowing("followingMember", "followedMember"));
            then(memberRepository).should(times(2)).findByUserId(anyString());
            then(followingRepository).should(times(1)).findIdByFollowerAndFollowedMember(any(Member.class), any(Member.class));
            then(followingRepository).should(never()).delete(any(Following.class));
        }
    }

}