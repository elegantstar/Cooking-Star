package toy.cookingstar.service.comment;

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
import org.springframework.data.domain.Sort;
import toy.cookingstar.entity.Member;
import toy.cookingstar.entity.Post;
import toy.cookingstar.entity.PostComment;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.repository.PostCommentRepository;
import toy.cookingstar.repository.PostRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostCommentServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    PostCommentRepository postCommentRepository;

    @InjectMocks
    PostCommentService postCommentService;

    @Nested
    @DisplayName("댓글 생성 테스트")
    class CreateCommentTest {

        /**
         * loginUserId 검증
         * postId 검증
         * parentCommentId 조회
         * 댓글 생성
         * 댓글 저장
         *
         * @throws Exception
         */
        @Test
        @DisplayName("성공")
        void createSuccessTest() throws Exception {
            //given
            Member member = mock(Member.class);
            given(member.getUserId()).willReturn("test_user");
            Optional<Member> optionalMember = Optional.of(member);
            given(memberRepository.findById(1L)).willReturn(optionalMember);

            Post post = mock(Post.class);
            given(post.getContent()).willReturn("content in post");
            Optional<Post> optionalPost = Optional.of(post);
            given(postRepository.findById(1L)).willReturn(optionalPost);

            given(postCommentRepository.findById(0L)).willReturn(Optional.empty());

            ArgumentCaptor<PostComment> captor = ArgumentCaptor.forClass(PostComment.class);

            //when
            postCommentService.create(1L, 1L, 0L, "content of comment");

            //then
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(times(1)).findById(anyLong());

            then(postCommentRepository).should(times(1)).save(captor.capture());
            PostComment capturedComment = captor.getValue();

            assertEquals("test_user", capturedComment.getMember().getUserId());
            assertEquals("content in post", capturedComment.getPost().getContent());
            assertEquals("content of comment", capturedComment.getContent());
            assertNull(capturedComment.getParentComment());
        }

        @Test
        @DisplayName("실패")
        void createFailureTest() throws Exception {
            //given
            Member member = mock(Member.class);
            Post post = mock(Post.class);

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postCommentService.create(1L, 1L, 0L, "content"));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postRepository).should(never()).findById(anyLong());
            then(postCommentRepository).should(never()).findById(anyLong());
            then(postCommentRepository).should(never()).save(any(PostComment.class));
        }
    }

    @Nested
    @DisplayName("postId로 댓글 조회 테스트")
    class GetByPostIdTest {

        @DisplayName("성공")
        @Test
        void getByPostIdSuccessTest() throws Exception {
            //given
            Member member1 = Member.builder().userId("test_user1").build();
            Member member2 = Member.builder().userId("test_user2").build();

            Post post = mock(Post.class);
            given(post.getId()).willReturn(1L);
            given(postRepository.findById(1L)).willReturn(Optional.of(post));

            PostComment comment1 = mock(PostComment.class);
            given(comment1.getMember()).willReturn(member1);
            given(comment1.getContent()).willReturn("comment1");

            PostComment comment2 = mock(PostComment.class);
            given(comment2.getMember()).willReturn(member2);
            given(comment2.getContent()).willReturn("comment2");

            Slice<PostComment> slice = mock(Slice.class);
            given(slice.getContent()).willReturn(Arrays.asList(comment1, comment2));
            given(slice.hasNext()).willReturn(false);
            given(slice.getContent().get(0).getMember()).willReturn(member1);
            given(slice.getContent().get(1).getMember()).willReturn(member2);

            given(postCommentRepository.findNestedComments(eq(1L), eq(0L), any(Pageable.class))).willReturn(slice);

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

            //when
            Slice<PostComment> commentSlice = postCommentService.getNestedCommentsByPostId(1L, 0L, 0, 5);

            //then
            then(postRepository).should(times(1)).findById(1L);
            then(postCommentRepository).should(times(1)).findNestedComments(anyLong(), anyLong(), captor.capture());
            assertEquals(0, captor.getValue().getOffset());
            assertEquals(5, captor.getValue().getPageSize());
            assertEquals(Sort.by(Sort.Direction.ASC, "createdDate"), captor.getValue().getSort());

            assertEquals(2, commentSlice.getContent().size());
            assertFalse(commentSlice.hasNext());
            assertEquals("comment1", commentSlice.getContent().get(0).getContent());
            assertEquals("comment2", commentSlice.getContent().get(1).getContent());
            assertEquals("test_user1", commentSlice.getContent().get(0).getMember().getUserId());
            assertEquals("test_user2", commentSlice.getContent().get(1).getMember().getUserId());
        }

        @Test
        @DisplayName("실패_존재하지 않는 postId")
        void getByPostIdFailureTest() throws Exception {
            //given
            given(postRepository.findById(anyLong())).willReturn(Optional.empty());
            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postCommentService.getNestedCommentsByPostId(1L, 0L, 0, 5));
            then(postCommentRepository).should(never()).findNestedComments(anyLong(), anyLong(), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("게시글 총 댓글 수 조회 테스트")
    class CountCommentsTest {

        @Test
        @DisplayName("성공")
        void countCommentsSuccessTest() throws Exception {
            //given
            Post post = mock(Post.class);
            given(post.getId()).willReturn(1L);
            given(postRepository.findById(1L)).willReturn(Optional.of(post));
            given(postCommentRepository.countByPost(post)).willReturn(10);

            ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);

            //when
            int count = postCommentService.countComments(1L);

            //then
            then(postRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(times(1)).countByPost(captor.capture());
            assertEquals(10, count);
            assertEquals(1L, captor.getValue().getId());
        }

        @Test
        @DisplayName("실패_존재하지 않는 postId")
        void countCommentsFailureTest() throws Exception {
            //given
            given(postRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postCommentService.countComments(1L));
            then(postCommentRepository).should(never()).countByPost(any(Post.class));
        }
    }

    @Nested
    @DisplayName("댓글 삭제 테스트")
    class DeleteCommentTest {

        @Test
        @DisplayName("성공_대댓글이 없는 경우")
        void deleteCommentSuccessTest_nestedCommentDoesNotExist() throws Exception {
            //given
            Member member = mock(Member.class);
            given(member.getId()).willReturn(1L);
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));

            PostComment postComment = mock(PostComment.class);
            given(postComment.getId()).willReturn(1L);
            given(postComment.getMember()).willReturn(member);
            given(postComment.getContent()).willReturn("This comment will be deleted.");
            given(postCommentRepository.findById(1L)).willReturn(Optional.of(postComment));

            given(postCommentRepository.existsNestedCommentsByParentCommentId(1L)).willReturn(false);

            ArgumentCaptor<PostComment> captor = ArgumentCaptor.forClass(PostComment.class);

            //when
            postCommentService.deleteComment(1L, 1L);

            //then
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(times(1)).existsNestedCommentsByParentCommentId(anyLong());

            then(postCommentRepository).should(times(1)).delete(captor.capture());
            PostComment capturedComment = captor.getValue();
            assertEquals(1L, capturedComment.getId());
            assertEquals("This comment will be deleted.", capturedComment.getContent());
            assertEquals(1L, capturedComment.getMember().getId());
        }

        @Test
        @DisplayName("성공_대댓글이 있는 경우")
        void deleteCommentSuccessTest_nestedCommentsExist() throws Exception {
            //given
            Member member = mock(Member.class);
            given(member.getId()).willReturn(1L);
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));

            PostComment postComment = mock(PostComment.class);
            given(postComment.getMember()).willReturn(member);
            given(postCommentRepository.findById(1L)).willReturn(Optional.of(postComment));

            given(postCommentRepository.existsNestedCommentsByParentCommentId(1L)).willReturn(true);

            //when
            postCommentService.deleteComment(1L, 1L);

            //then
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(times(1)).existsNestedCommentsByParentCommentId(anyLong());

            then(postCommentRepository).should(never()).delete(any(PostComment.class));
        }

        @Test
        @DisplayName("실패_존재하지 않는 memberId")
        void deleteCommentFailureTest_loginMemberIdDoesNotExist() throws Exception {
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postCommentService.deleteComment(1L, 1L));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(never()).findById(anyLong());
            then(postCommentRepository).should(never()).existsNestedCommentsByParentCommentId(anyLong());
            then(postCommentRepository).should(never()).delete(any(PostComment.class));
        }

        @Test
        @DisplayName("실패_존재하지 않는 commentId")
        void deleteCommentFailureTest_commentIdDoesNotExist() throws Exception {
            //given
            Member member = mock(Member.class);
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(postCommentRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postCommentService.deleteComment(1L, 1L));

            then(memberRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(never()).existsNestedCommentsByParentCommentId(anyLong());
            then(postCommentRepository).should(never()).delete(any(PostComment.class));
        }
        
        @Test
        @DisplayName("실패_로그인 유저와 댓글 작성 유저 불일치")
        void deleteCommentFailureTest_loginMemberIdDoesNotEqualMemberIdOfTheComment() throws Exception {
            //given
            Member loginMember = mock(Member.class);
            given(loginMember.getId()).willReturn(1L);

            Member otherMember = mock(Member.class);
            given(otherMember.getId()).willReturn(2L);

            given(memberRepository.findById(1L)).willReturn(Optional.of(loginMember));

            PostComment postComment = mock(PostComment.class);
            given(postComment.getMember()).willReturn(otherMember);
            given(postCommentRepository.findById(1L)).willReturn(Optional.of(postComment));

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postCommentService.deleteComment(1L, 1L));

            then(memberRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(times(1)).findById(anyLong());
            then(postCommentRepository).should(never()).existsNestedCommentsByParentCommentId(anyLong());
            then(postCommentRepository).should(never()).delete(any(PostComment.class));
        }
    }
}