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
import toy.cookingstar.entity.Member;
import toy.cookingstar.entity.Post;
import toy.cookingstar.entity.PostLiker;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.repository.PostLikerRepository;
import toy.cookingstar.repository.PostRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostLikerServiceImplTest {

    @Mock
    PostLikerRepository postLikerRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    PostRepository postRepository;

    @InjectMocks
    PostLikerServiceImpl postLikerService;

    @Nested
    @DisplayName("게시글 좋아요 생성 테스트")
    class CreatePostLikerTest {

        @Test
        @DisplayName("성공")
        void createPostLikerSuccessTest() throws Exception {
            //given
            Member member = mock(Member.class);
            given(member.getId()).willReturn(1L);
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));

            Post post = mock(Post.class);
            given(post.getId()).willReturn(1L);
            given(postRepository.findById(1L)).willReturn(Optional.of(post));

            ArgumentCaptor<PostLiker> captor = ArgumentCaptor.forClass(PostLiker.class);

            //when
            postLikerService.create(1L, 1L);

            //then
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postRepository).should(times(1)).findById(anyLong());
            then(postLikerRepository).should(times(1)).save(captor.capture());

            assertEquals(member.getId(), captor.getValue().getMember().getId());
            assertEquals(post.getId(), captor.getValue().getPost().getId());
        }

        @Test
        @DisplayName("실패_존재하지 않는 memberId")
        void createdPostLikerFailureTest_MemberIdDoesNotExist() throws Exception {
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postLikerService.create(1L, 1L));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postRepository).should(never()).findById(anyLong());
            then(postLikerRepository).should(never()).save(any(PostLiker.class));
        }

        @Test
        @DisplayName("실패_존재하지 않는 postId")
        void createdPostLikerFailureTest_PostIdDoesNotExist() throws Exception {
            //given
            Member member = mock(Member.class);
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(postRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postLikerService.create(1L, 1L));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postRepository).should(times(1)).findById(anyLong());
            then(postLikerRepository).should(never()).save(any(PostLiker.class));
        }
    }

    @Nested
    @DisplayName("게시글 좋아요 수 조회 테스트")
    class CountLikersTest {

        @Test
        @DisplayName("성공")
        void countLikersSuccessTest() throws Exception {
            //given
            Post post = mock(Post.class);
            given(post.getId()).willReturn(1L);
            given(postRepository.findById(1L)).willReturn(Optional.of(post));
            given(postLikerRepository.countByPost(post)).willReturn(13);

            ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);

            //when
            int count = postLikerService.countLikers(1L);

            //then
            then(postRepository).should(times(1)).findById(anyLong());
            then(postLikerRepository).should(times(1)).countByPost(captor.capture());
            assertEquals(1L, captor.getValue().getId());
            assertEquals(13, count);
        }

        @Test
        @DisplayName("실패_존재하지 않는 postId")
        void countLikerFailureTest_PostIdDoesNotExist() throws Exception {
            //given
            given(postRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postLikerService.countLikers(1L));
            then(postRepository).should(times(1)).findById(anyLong());
            then(postLikerRepository).should(never()).countByPost(any(Post.class));
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

            Post post = mock(Post.class);
            given(postRepository.findById(1L)).willReturn(Optional.of(post));

            PostLiker postLiker1 = PostLiker.createPostLiker(member1, post);
            PostLiker postLiker2 = PostLiker.createPostLiker(member2, post);
            PostLiker postLiker3 = PostLiker.createPostLiker(member3, post);

            Slice<PostLiker> slice = new SliceImpl<>(Arrays.asList(postLiker1, postLiker2, postLiker3));
            given(postLikerRepository.findLikersByPost(eq(post), any(Pageable.class))).willReturn(slice);

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

            //when
            Slice<Member> result = postLikerService.getLikers(1L, 0, 10);

            //then
            then(postRepository).should(times(1)).findById(anyLong());
            then(postLikerRepository).should(times(1)).findLikersByPost(any(Post.class), captor.capture());
            assertEquals(0, captor.getValue().getOffset());
            assertEquals(10, captor.getValue().getPageSize());

            assertEquals(3, result.getNumberOfElements());
            assertEquals(member1.getUserId(), result.getContent().get(0).getUserId());
            assertEquals(member2.getUserId(), result.getContent().get(1).getUserId());
            assertEquals(member3.getUserId(), result.getContent().get(2).getUserId());
        }

        @Test
        @DisplayName("실패_존재하지 않는 postId")
        void getLikersFailureTest_PostIdDoesNotExist() throws Exception {
            //given
            given(postRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postLikerService.getLikers(1L, 0, 10));
            then(postRepository).should(times(1)).findById(anyLong());
            then(postLikerRepository).should(never()).findLikersByPost(any(Post.class), any(Pageable.class));
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
            Post post = mock(Post.class);
            given(postRepository.findById(1L)).willReturn(Optional.of(post));

            given(postLikerRepository.existsByMemberAndPost(member, post)).willReturn(true);

            //when
            boolean likeOrNot = postLikerService.checkForLikes(1L, 1L);

            //then
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postRepository).should(times(1)).findById(anyLong());
            then(postLikerRepository).should(times(1)).existsByMemberAndPost(any(Member.class), any(Post.class));
            assertTrue(likeOrNot);
        }

        @Test
        @DisplayName("실패_존재하지 않는 memberId")
        void checkForLikesFailureTest_MemberIdDoesNotExist() throws Exception {
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postLikerService.checkForLikes(1L, 1L));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postRepository).should(never()).findById(anyLong());
            then(postLikerRepository).should(never()).existsByMemberAndPost(any(Member.class), any(Post.class));
        }

        @Test
        @DisplayName("실패_존재하지 않는 postId")
        void checkForLikesFailureTest_PostIdDoesNotExist() throws Exception {
            //given
            Member member = mock(Member.class);
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(postRepository.findById(anyLong())).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postLikerService.checkForLikes(1L, 1L));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postRepository).should(times(1)).findById(anyLong());
            then(postLikerRepository).should(never()).existsByMemberAndPost(any(Member.class), any(Post.class));
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
            Post post = mock(Post.class);
            given(post.getId()).willReturn(1L);
            given(postRepository.findById(1L)).willReturn(Optional.of(post));
            PostLiker postLiker = mock(PostLiker.class);
            given(postLiker.getMember()).willReturn(member);
            given(postLiker.getPost()).willReturn(post);
            given(postLikerRepository.findByMemberAndPost(member, post)).willReturn(postLiker);

            ArgumentCaptor<PostLiker> captor = ArgumentCaptor.forClass(PostLiker.class);

            //when
            postLikerService.deleteLiker(1L, 1L);

            //then
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postRepository).should(times(1)).findById(anyLong());
            then(postLikerRepository).should(times(1)).findByMemberAndPost(any(Member.class), any(Post.class));
            then(postLikerRepository).should(times(1)).delete(captor.capture());

            assertEquals(member.getId(), captor.getValue().getMember().getId());
            assertEquals(post.getId(), captor.getValue().getPost().getId());
        }

        @Test
        @DisplayName("실패_존재하지 않는 memberId")
        void deleteLikerFailureTest_MemberIdDoesNotExist() throws Exception {
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());
            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postLikerService.deleteLiker(1L, 1L));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postRepository).should(never()).findById(anyLong());
            then(postLikerRepository).should(never()).delete(any(PostLiker.class));
        }

        @Test
        @DisplayName("실패_존재하지 않는 postId")
        void deleteLikerFailureTest_PostIdDoesNotExist() throws Exception {
            //given
            Member member = mock(Member.class);
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(postRepository.findById(anyLong())).willReturn(Optional.empty());
            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postLikerService.deleteLiker(1L, 1L));
            then(memberRepository).should(times(1)).findById(anyLong());
            then(postRepository).should(times(1)).findById(anyLong());
            then(postLikerRepository).should(never()).delete(any(PostLiker.class));
        }
    }
}