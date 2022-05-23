package toy.cookingstar.service.post;

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
import org.springframework.data.domain.Sort;
import toy.cookingstar.entity.Member;
import toy.cookingstar.entity.Post;
import toy.cookingstar.entity.PostImage;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.repository.PostImageRepository;
import toy.cookingstar.repository.PostRepository;
import toy.cookingstar.service.post.dto.PostCreateDto;
import toy.cookingstar.service.post.dto.PostImageUrlDto;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    PostImageRepository postImageRepository;

    @InjectMocks
    PostService postService;

    @Nested
    @DisplayName("게시물 생성 테스트")
    class CreatedTest {

        /**
         * userId 검증
         * 게시물 이미지 생성
         * 게시물 생성
         * 게시물 저장
         */
        @Test
        @DisplayName("성공")
        void createSuccessTest() throws Exception {
            //given
            PostCreateDto dto = new PostCreateDto(1L, "userId","content in post",
                    Arrays.asList("pasta", "steak", "salad"), StatusType.POSTING);
            Member user = mock(Member.class);
            given(memberRepository.findById(dto.getMemberId())).willReturn(Optional.of(user));

            ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
            Post post = mock(Post.class);
            given(post.getId()).willReturn(1L);
            given(postRepository.save(any(Post.class))).willReturn(post);

            //when
            Long postId = postService.create(dto);

            //then
            then(memberRepository).should(times(1)).findById(1L);
            then(postRepository).should(times(1)).save(captor.capture());

            Post capturedPost = captor.getValue();
            assertEquals(user, capturedPost.getMember());
            assertEquals(dto.getContent(), capturedPost.getContent());
            assertEquals(dto.getStatus(), capturedPost.getStatus());
            assertEquals(3, capturedPost.getPostImages().size());
            assertEquals("pasta", capturedPost.getPostImages().get(0).getUrl());
            assertEquals(1, capturedPost.getPostImages().get(0).getPriority());
            assertEquals("steak", capturedPost.getPostImages().get(1).getUrl());
            assertEquals(2, capturedPost.getPostImages().get(1).getPriority());
            assertEquals("salad", capturedPost.getPostImages().get(2).getUrl());
            assertEquals(3, capturedPost.getPostImages().get(2).getPriority());
        }

        @Test
        @DisplayName("실패")
        void createdFailureTest() throws Exception {
            //given
            PostCreateDto dto = mock(PostCreateDto.class);
            given(dto.getMemberId()).willReturn(1L);
            given(memberRepository.findById(1L)).willReturn(Optional.empty());

            //when & then
            assertThrows(IllegalArgumentException.class, () -> postService.create(dto));
            then(memberRepository).should(times(1)).findById(1L);
            then(postRepository).should(never()).save(any(Post.class));
        }
    }

    @Nested
    @DisplayName("Id로 게시물 단건 조회 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("성공")
        void findByIdSuccessTest() throws Exception {
            //given
            Member member = mock(Member.class);
            Post post = mock(Post.class);
            PostImage postImage = mock(PostImage.class);
            given(post.getMember()).willReturn(member);
            given(post.getPostImages()).willReturn(Arrays.asList(postImage, postImage, postImage));

            Optional<Post> optionalPost = Optional.of(post);
            given(postRepository.findById(anyLong())).willReturn(optionalPost);

            //when
            Post foundPost = postService.findById(1L);

            //then
            then(postRepository).should(times(1)).findById(anyLong());
            assertNotNull(foundPost);
            assertEquals(3, foundPost.getPostImages().size());
        }

        @Test
        @DisplayName("실패_존재하지 않는 postId")
        void findByIdFailureTest() throws Exception {
            //given
            given(postRepository.findById(1L)).willReturn(Optional.empty());

            //then
            assertThrows(IllegalArgumentException.class,
                    () -> postService.findById(1L));
        }

        @Test
        @DisplayName("실패_게시물 이미지 없음")
        void findByIdFailureTest_postImagesIsEmpty() throws Exception {
            //given
            Post post = mock(Post.class);
            Optional<Post> optionalPost = Optional.of(post);
            given(postRepository.findById(anyLong())).willReturn(optionalPost);

            //when
            Post foundPost = postService.findById(1L);

            //then
            assertNull(foundPost);
        }
    }

    @Nested
    @DisplayName("유저 페이지 게시물 리스트 조회 테스트")
    class GetUserPagePostImagesTest {

        /**
         * userId 검증
         * 게시물 Id 리스트 생성
         * priority = 1인 게시물 이미지 Url 리스트 생성
         * postImageUrlDto 생성
         */
        @Test
        @DisplayName("성공")
        void getUserPagePostImagesSuccessTest() throws Exception {
            //given
            Member user = mock(Member.class);
            given(user.getId()).willReturn(1L);

            Post post = mock(Post.class);
            PostImage postImage = mock(PostImage.class);

            given(post.getId()).willReturn(1L).willReturn(2L).willReturn(3L);
            given(post.getPostImages()).willReturn(Arrays.asList(postImage, postImage, postImage));
            given(postImage.getPriority()).willReturn(1).willReturn(2).willReturn(1).willReturn(2).willReturn(1).willReturn(2);
            given(postImage.getUrl()).willReturn("pasta1").willReturn("steak1").willReturn("salad1");

            Slice<Post> pages = mock(Slice.class);
            given(pages.getContent()).willReturn(Arrays.asList(post, post, post));

            given(memberRepository.findByUserId(anyString())).willReturn(user);
            given(postRepository.findPosts(eq(user.getId()), eq(StatusType.POSTING), any(Pageable.class))).willReturn(pages);

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

            //when
            PostImageUrlDto dto = postService.getUserPagePostImages("test_user", 0, 5, StatusType.POSTING);

            //then
            then(memberRepository).should(times(1)).findByUserId(anyString());

            assertNotNull(dto);
            assertEquals(1L, dto.getPostIds().get(0));
            assertEquals(2L, dto.getPostIds().get(1));
            assertEquals(3L, dto.getPostIds().get(2));
            assertEquals("pasta1", dto.getImageUrls().get(0));
            assertEquals("steak1", dto.getImageUrls().get(1));
            assertEquals("salad1", dto.getImageUrls().get(2));

            then(postRepository).should(times(1))
                    .findPosts(eq(user.getId()), eq(StatusType.POSTING), captor.capture());
            Pageable pageable = captor.getValue();
            assertEquals(0, pageable.getPageNumber());
            assertEquals(5, pageable.getPageSize());
            assertEquals(Sort.by(Sort.Direction.DESC, "id"), pageable.getSort());
        }

        @Test
        @DisplayName("실패_존재하지 않는 userId")
        void getUserPagePostImageFailureTest() throws Exception {
            //given
            given(memberRepository.findByUserId("test_user")).willReturn(null);
            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postService.getUserPagePostImages("test_user", 0, 5, StatusType.POSTING));
        }
    }

    @Test
    @DisplayName("유저 총 게시물 수 조회")
    void countPostsTest() throws Exception {
        //given
        given(postRepository.countByMemberId(1L)).willReturn(150);
        //when
        int count = postService.countPosts(1L);
        //then
        then(postRepository).should(times(1)).countByMemberId(anyLong());
        assertEquals(150, count);
    }

    @Nested
    @DisplayName("게시물 삭제 테스트")
    class DeletePostTest {

        @Test
        @DisplayName("성공")
        void deletePostSuccessTest() throws Exception {
            //given
            Post post = mock(Post.class);
            given(post.getId()).willReturn(1L);
            Optional<Post> optionalPost = Optional.of(post);
            given(postRepository.findById(1L)).willReturn(optionalPost);

            ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);

            //when
            postService.deletePost("test_user", 1L);

            //then
            then(postRepository).should(times(1)).findById(1L);

            then(postImageRepository).should(times(1)).deleteAllByPost(captor.capture());
            assertEquals(1L, captor.getValue().getId());

            then(postRepository).should(times(1)).delete(captor.capture());
            assertEquals(1L, captor.getValue().getId());
        }

        @Test
        @DisplayName("실패")
        void deletePostFailureTest() throws Exception {
            //given
            given(postRepository.findById(anyLong())).willReturn(Optional.empty());

            //then
            assertThrows(IllegalArgumentException.class,
                    () -> postService.deletePost("test_user", 1L));
        }
    }

    @Nested
    @DisplayName("게시물 수정 테스트")
    class UpdatePostTest {

        @Test
        @DisplayName("성공")
        void updatePostSuccessTest() throws Exception {
            //given
            Post post = mock(Post.class);
            Optional<Post> optionalPost = Optional.of(post);
            given(postRepository.findById(1L)).willReturn(optionalPost);

            ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<StatusType> statusCaptor = ArgumentCaptor.forClass(StatusType.class);

            //when
            postService.updatePost("test_user", 1L, "updated content in post", StatusType.POSTING);

            //then
            then(postRepository).should(times(1)).findById(1L);
            then(optionalPost.orElse(null)).should().updatePost(stringCaptor.capture(), statusCaptor.capture());
            assertEquals("updated content in post", stringCaptor.getValue());
            assertEquals(StatusType.POSTING, statusCaptor.getValue());
        }

        @Test
        @DisplayName("실패")
        void updatePostFailureTest() throws Exception {
            //given
            given(postRepository.findById(anyLong())).willReturn(Optional.empty());
            //then
            assertThrows(IllegalArgumentException.class,
                    () -> postService.updatePost("test_user", 1L, "update content", StatusType.POSTING));
        }
    }

    @Nested
    @DisplayName("임시 보관 게시물 조회")
    class GetTemporaryStorageTest {

        @Test
        @DisplayName("성공")
        void getTemporaryStorageSuccessTest() throws Exception {
            //given
            Member user = mock(Member.class);
            given(user.getId()).willReturn(1L);
            Optional<Member> optionalMember = Optional.of(user);
            given(memberRepository.findById(1L)).willReturn(optionalMember);

            Post post = mock(Post.class);
            PostImage postImage = mock(PostImage.class);

            given(post.getId()).willReturn(1L).willReturn(2L);
            given(post.getPostImages()).willReturn(Arrays.asList(postImage, postImage));
            given(postImage.getPriority()).willReturn(1).willReturn(2).willReturn(1).willReturn(2);
            given(postImage.getUrl()).willReturn("pasta1").willReturn("pasta2").willReturn("steak1").willReturn("steak2");

            Slice<Post> pages = mock(Slice.class);
            given(pages.getContent()).willReturn(Arrays.asList(post, post, post));

            given(postRepository.findPosts(eq(user.getId()), eq(StatusType.TEMPORARY_STORAGE), any(Pageable.class))).willReturn(pages);

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

            //when
            List<Post> temporaryStorage = postService.getTemporaryStorage(1L, StatusType.TEMPORARY_STORAGE, 0, 5);

            //then
            then(memberRepository).should(times(1)).findById(1L);

            assertNotNull(temporaryStorage);
            assertEquals(1L, temporaryStorage.get(0).getId());
            assertEquals(2L, temporaryStorage.get(1).getId());
            assertEquals("pasta1", temporaryStorage.get(0).getPostImages().get(0).getUrl());
            assertEquals("pasta2", temporaryStorage.get(0).getPostImages().get(1).getUrl());
            assertEquals("steak1", temporaryStorage.get(1).getPostImages().get(0).getUrl());
            assertEquals("steak2", temporaryStorage.get(1).getPostImages().get(1).getUrl());
            assertEquals(1, temporaryStorage.get(0).getPostImages().get(0).getPriority());
            assertEquals(2, temporaryStorage.get(0).getPostImages().get(1).getPriority());
            assertEquals(1, temporaryStorage.get(1).getPostImages().get(0).getPriority());
            assertEquals(2, temporaryStorage.get(1).getPostImages().get(1).getPriority());


            then(postRepository).should(times(1))
                    .findPosts(eq(user.getId()), eq(StatusType.TEMPORARY_STORAGE), captor.capture());
            Pageable pageable = captor.getValue();

            assertEquals(0, pageable.getPageNumber());
            assertEquals(5, pageable.getPageSize());
            assertEquals(Sort.by(Sort.Direction.DESC, "id"), pageable.getSort());
        }

        @Test
        @DisplayName("실패_존재하지 않는 memberId")
        void getTemporaryStorageFailureTest() throws Exception {
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

            //then
            assertThrows(IllegalArgumentException.class,
                    () -> postService.getTemporaryStorage(1L, StatusType.TEMPORARY_STORAGE, 0, 5));
        }
    }

    @Nested
    @DisplayName("유저 페이지 게시물 Slice 조회 테스트")
    class GetUserPagePostImageSliceTest {

        @Test
        @DisplayName("성공")
        void getUserPagePostImageSliceSuccessTest() throws Exception {
            //given
            Member user = mock(Member.class);
            given(user.getId()).willReturn(1L);

            Post post = mock(Post.class);
            PostImage postImage = mock(PostImage.class);

            given(post.getPostImages()).willReturn(Arrays.asList(postImage, postImage, postImage));
            Slice<Post> pages = new SliceImpl<>(Arrays.asList(post, post, post));

            given(memberRepository.findByUserId(anyString())).willReturn(user);
            given(postRepository.findPostsByLastReadPostId(eq(user.getId()), eq(1L), any(Pageable.class), eq(StatusType.POSTING)))
                    .willReturn(pages);

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

            //when
            Slice<Post> slice = postService.getUserPagePostImageSlice("test_user", 1L, 3, StatusType.POSTING);

            //then
            then(memberRepository).should(times(1)).findByUserId(anyString());
            then(postRepository).should(times(1))
                    .findPostsByLastReadPostId(anyLong(), anyLong(), captor.capture(), any(StatusType.class));
            Pageable capturedPageable = captor.getValue();
            assertEquals(0, capturedPageable.getOffset());
            assertEquals(3, capturedPageable.getPageSize());
            assertEquals(Sort.by(Sort.Direction.DESC, "createdDate"), capturedPageable.getSort());

            assertEquals(3, slice.getNumberOfElements());
        }

        @Test
        @DisplayName("실패_존재하지 않는 userId")
        void getUserPagePostImageSliceFailureTest_UserIdDoesNotExist() throws Exception {
            //given
            given(memberRepository.findByUserId(anyString())).willReturn(null);
            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> postService.getUserPagePostImageSlice("test_user", 1L,6, StatusType.POSTING));
            then(memberRepository).should(times(1)).findByUserId(anyString());
            then(postRepository).should(never()).findPostsByLastReadPostId(anyLong(), anyLong(), any(Pageable.class), any(StatusType.class));
        }
    }


}