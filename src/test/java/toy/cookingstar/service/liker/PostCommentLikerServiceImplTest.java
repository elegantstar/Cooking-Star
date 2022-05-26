package toy.cookingstar.service.liker;

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
import toy.cookingstar.entity.*;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.repository.PostCommentLikerRepository;
import toy.cookingstar.repository.PostCommentRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostCommentLikerServiceImplTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PostCommentRepository postCommentRepository;

    @Mock
    PostCommentLikerRepository postCommentLikerRepository;

    @InjectMocks
    PostCommentLikerServiceImpl postCommentLikerService;

    @Nested
    @DisplayName("댓글 좋아요 생성 테스트")
    class CreatePostCommentLikerTest {

        @Test
        @DisplayName("성공")
        void createPostCommentLikerSuccessTest() throws Exception {
            //given
            Member member = mock(Member.class);
            given(member.getId()).willReturn(1L);
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));

            PostComment comment = mock(PostComment.class);
            given(comment.getId()).willReturn(1L);
            given(postCommentRepository.findById(1L)).willReturn(Optional.of(comment));

            ArgumentCaptor<PostCommentLiker> captor = ArgumentCaptor.forClass(PostCommentLiker.class);

            //when
            postCommentLikerService.create(1L, 1L);

            //then
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(times(1)).findById(anyLong());
            then(postCommentLikerRepository).should(times(1)).save(captor.capture());

            assertEquals(member.getId(), captor.getValue().getMember().getId());
            assertEquals(comment.getId(), captor.getValue().getPostComment().getId());
        }

        @Test
        @DisplayName("실패_존재하지 않는 memberId")
        void createdPostLikerFailureTest_MemberIdDoesNotExist() throws Exception {
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postCommentLikerService.create(1L, 1L));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(never()).findById(anyLong());
            then(postCommentLikerRepository).should(never()).save(any(PostCommentLiker.class));
        }

        @Test
        @DisplayName("실패_존재하지 않는 postCommentId")
        void createdPostLikerFailureTest_PostCommentIdDoesNotExist() throws Exception {
            //given
            Member member = mock(Member.class);
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(postCommentRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postCommentLikerService.create(1L, 1L));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(times(1)).findById(anyLong());
            then(postCommentLikerRepository).should(never()).save(any(PostCommentLiker.class));
        }
    }

    @Nested
    @DisplayName("댓글 좋아요 수 조회 테스트")
    class CountLikersTest {

        @Test
        @DisplayName("성공")
        void countLikersSuccessTest() throws Exception {
            //given
            PostComment postComment = mock(PostComment.class);
            given(postComment.getId()).willReturn(1L);
            given(postCommentRepository.findById(1L)).willReturn(Optional.of(postComment));
            given(postCommentLikerRepository.countByPostComment(postComment)).willReturn(13);

            ArgumentCaptor<PostComment> captor = ArgumentCaptor.forClass(PostComment.class);

            //when
            int count = postCommentLikerService.countLikers(1L);

            //then
            then(postCommentRepository).should(times(1)).findById(anyLong());
            then(postCommentLikerRepository).should(times(1)).countByPostComment(captor.capture());
            assertEquals(1L, captor.getValue().getId());
            assertEquals(13, count);
        }

        @Test
        @DisplayName("실패_존재하지 않는 postCommentId")
        void countLikerFailureTest_PostCommentIdDoesNotExist() throws Exception {
            //given
            given(postCommentRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postCommentLikerService.countLikers(1L));
            then(postCommentRepository).should(times(1)).findById(anyLong());
            then(postCommentLikerRepository).should(never()).countByPostComment(any(PostComment.class));
        }
    }

    @Nested
    @DisplayName("'좋아요'한 회원 조회 테스트")
    class GetLikersTest {

        @Test
        @DisplayName("성공")
        void getLikersSuccessTest() throws Exception {
            //given
            Member member1 = Member.builder().userId("member1").build();
            Member member2 = Member.builder().userId("member1").build();
            Member member3 = Member.builder().userId("member1").build();

            PostComment postComment = mock(PostComment.class);
            given(postCommentRepository.findById(1L)).willReturn(Optional.of(postComment));

            PostCommentLiker postLiker1 = PostCommentLiker.createCommentLiker(member1, postComment);
            PostCommentLiker postLiker2 = PostCommentLiker.createCommentLiker(member2, postComment);
            PostCommentLiker postLiker3 = PostCommentLiker.createCommentLiker(member3, postComment);

            Slice<PostCommentLiker> slice = new SliceImpl<>(Arrays.asList(postLiker1, postLiker2, postLiker3));
            given(postCommentLikerRepository.findLikersByPostComment(eq(postComment), any(Pageable.class))).willReturn(slice);

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

            //when
            Slice<Member> result = postCommentLikerService.getLikers(1L, 0, 10);

            //then
            then(postCommentRepository).should(times(1)).findById(anyLong());
            then(postCommentLikerRepository).should(times(1))
                    .findLikersByPostComment(any(PostComment.class), captor.capture());
            assertEquals(0, captor.getValue().getOffset());
            assertEquals(10, captor.getValue().getPageSize());

            assertEquals(3, result.getNumberOfElements());
            assertEquals(member1.getUserId(), result.getContent().get(0).getUserId());
            assertEquals(member2.getUserId(), result.getContent().get(1).getUserId());
            assertEquals(member3.getUserId(), result.getContent().get(2).getUserId());
        }

        @Test
        @DisplayName("실패_존재하지 않는 postCommentId")
        void getLikersFailureTest_PostCommentIdDoesNotExist() throws Exception {
            //given
            given(postCommentRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postCommentLikerService.getLikers(1L, 0, 10));
            then(postCommentRepository).should(times(1)).findById(anyLong());
            then(postCommentLikerRepository).should(never()).findLikersByPostComment(any(PostComment.class), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("'좋아요' 클릭 여부 확인 테스트")
    class CheckForLikesTest {

        @Test
        @DisplayName("성공")
        void checkForLikesSuccessTest() throws Exception {
            //given
            Member member = mock(Member.class);
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            PostComment postComment = mock(PostComment.class);
            given(postCommentRepository.findById(1L)).willReturn(Optional.of(postComment));

            given(postCommentLikerRepository.existsByMemberAndPostComment(member, postComment)).willReturn(true);

            //when
            boolean likeOrNot = postCommentLikerService.checkForLikes(1L, 1L);

            //then
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(times(1)).findById(anyLong());
            then(postCommentLikerRepository).should(times(1))
                    .existsByMemberAndPostComment(any(Member.class), any(PostComment.class));
            assertTrue(likeOrNot);
        }

        @Test
        @DisplayName("실패_존재하지 않는 memberId")
        void checkForLikesFailureTest_MemberIdDoesNotExist() throws Exception {
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postCommentLikerService.checkForLikes(1L, 1L));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(never()).findById(anyLong());
            then(postCommentLikerRepository).should(never())
                    .existsByMemberAndPostComment(any(Member.class), any(PostComment.class));
        }

        @Test
        @DisplayName("실패_존재하지 않는 postCommentId")
        void checkForLikesFailureTest_PostCommentIdDoesNotExist() throws Exception {
            //given
            Member member = mock(Member.class);
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(postCommentRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postCommentLikerService.checkForLikes(1L, 1L));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(times(1)).findById(anyLong());
            then(postCommentLikerRepository).should(never())
                    .existsByMemberAndPostComment(any(Member.class), any(PostComment.class));
        }
    }

    @Nested
    @DisplayName("좋아요 기록 삭제 테스트")
    class DeleteLikerTest {

        @Test
        @DisplayName("성공")
        void deleteLikerSuccessTest() throws Exception {
            //given
            Member member = mock(Member.class);
            given(member.getId()).willReturn(1L);
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            PostComment postComment = mock(PostComment.class);
            given(postComment.getId()).willReturn(1L);
            given(postCommentRepository.findById(1L)).willReturn(Optional.of(postComment));
            PostCommentLiker postCommentLiker = mock(PostCommentLiker.class);
            given(postCommentLiker.getMember()).willReturn(member);
            given(postCommentLiker.getPostComment()).willReturn(postComment);
            given(postCommentLikerRepository.findByMemberAndPostComment(member, postComment)).willReturn(postCommentLiker);

            ArgumentCaptor<PostCommentLiker> captor = ArgumentCaptor.forClass(PostCommentLiker.class);

            //when
            postCommentLikerService.deleteLiker(1L, 1L);

            //then
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(times(1)).findById(anyLong());
            then(postCommentLikerRepository).should(times(1))
                    .findByMemberAndPostComment(any(Member.class), any(PostComment.class));
            then(postCommentLikerRepository).should(times(1)).delete(captor.capture());

            assertEquals(member.getId(), captor.getValue().getMember().getId());
            assertEquals(postComment.getId(), captor.getValue().getPostComment().getId());
        }

        @Test
        @DisplayName("실패_존재하지 않는 memberId")
        void deleteLikerFailureTest_MemberIdDoesNotExist() throws Exception {
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());
            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postCommentLikerService.deleteLiker(1L, 1L));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(never()).findById(anyLong());
            then(postCommentLikerRepository).should(never()).delete(any(PostCommentLiker.class));
        }

        @Test
        @DisplayName("실패_존재하지 않는 postCommentId")
        void deleteLikerFailureTest_PostCommentIdDoesNotExist() throws Exception {
            //given
            Member member = mock(Member.class);
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(postCommentRepository.findById(anyLong())).willReturn(Optional.empty());
            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postCommentLikerService.deleteLiker(1L, 1L));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(times(1)).findById(anyLong());
            then(postCommentLikerRepository).should(never()).delete(any(PostCommentLiker.class));
        }
    }
}