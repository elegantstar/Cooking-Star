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
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import toy.cookingstar.entity.Member;
import toy.cookingstar.entity.Post;
import toy.cookingstar.entity.PostImage;
import toy.cookingstar.service.post.StatusType;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostImageRepository postImageRepository;
    @Autowired
    EntityManager em;

    @Test
    @DisplayName("게시물 저장 테스트")
    void savePostTest() throws Exception {
        //given
        Member member = Member.builder()
                .userId("test")
                .build();

        List<PostImage> postImages = new ArrayList<>();
        PostImage postImage1 = PostImage.createPostImage("image_url1", 1);
        PostImage postImage2 = PostImage.createPostImage("image_url2", 2);
        postImages.add(postImage1);
        postImages.add(postImage2);

        Post post = Post.createPost(member, "content in post", StatusType.POSTING, postImages);

        //when
        Post savedPost = postRepository.save(post);

        //then
        assertSame(post.getMember(), savedPost.getMember());
        assertEquals(post.getContent(), savedPost.getContent());
        assertEquals(post.getStatus(), savedPost.getStatus());
        assertEquals(post.getPostImages().get(0).getUrl(), savedPost.getPostImages().get(0).getUrl());
        assertNotNull(savedPost.getPostImages().get(0).getId());
    }

    @Nested
    @DisplayName("Id로 게시물 조회 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("성공")
        void findByIdSuccessTest() throws Exception {
            //given
            Member member = Member.builder()
                    .userId("test")
                    .build();
            memberRepository.save(member);

            Post post = Post.createPost(member, "content in post", StatusType.POSTING, new ArrayList<>());

            Long savedPostId = postRepository.save(post).getId();
            em.flush();
            em.clear();

            //when
            Optional<Post> foundPost = postRepository.findById(savedPostId);

            //then
            foundPost.ifPresent(value -> assertEquals(savedPostId, value.getId()));
        }

        @Test
        @DisplayName("실패")
        void findByIdFailureTest() throws Exception {
            //when
            Optional<Post> foundPost = postRepository.findById(-1L);
            //then
            assertEquals(Optional.empty(), foundPost);
        }
    }

    @Nested
    @DisplayName("memberId로 게시물 개수 조회 테스트")
    class CountByMemberIdTest {

        @Test
        @DisplayName("성공")
        void countByMemberIdSuccessTest() throws Exception {
            //given
            Member member = Member.builder()
                    .userId("test")
                    .build();

            Long savedMemberId = memberRepository.save(member).getId();

            Post postA = Post.createPost(member, "A", StatusType.POSTING, new ArrayList<>());
            Post postB = Post.createPost(member, "B", StatusType.POSTING, new ArrayList<>());
            Post postC = Post.createPost(member, "C", StatusType.PRIVATE, new ArrayList<>());
            Post postD = Post.createPost(member, "D", StatusType.TEMPORARY_STORAGE, new ArrayList<>());

            Post savedPostA = postRepository.save(postA);
            Post savedPostB = postRepository.save(postB);
            Post savedPostC = postRepository.save(postC);
            Post savedPostD = postRepository.save(postD);

            Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));

            //when
            int count = postRepository.countByMemberId(savedMemberId);

            //then
            assertEquals(4, count);
        }

        @Test
        @DisplayName("실패")
        void countByMemberIdFailureTest() throws Exception {
            //when
            int count = postRepository.countByMemberId(-1L);
            //then
            assertEquals(0, count);
        }
    }

    @Nested
    @DisplayName("게시물 페이징 조회 테스트")
    class FindPostsTest {

        @Test
        @DisplayName("성공")
        void findPostsSuccessTest() throws Exception {
            //given
            Member member = Member.builder()
                    .userId("test")
                    .build();

            Long savedMemberId = memberRepository.save(member).getId();

            PostImage imageA1 = PostImage.createPostImage("post_image_url_a1", 1);
            PostImage imageA2 = PostImage.createPostImage("post_image_url_a2", 2);
            PostImage imageB1 = PostImage.createPostImage("post_image_url_b1", 1);
            PostImage imageB2 = PostImage.createPostImage("post_image_url_b2", 2);

            List<PostImage> images1 = new ArrayList<>();
            images1.add(imageA1);
            images1.add(imageA2);

            List<PostImage> images2 = new ArrayList<>();
            images2.add(imageB1);
            images2.add(imageB2);

            Post postA = Post.createPost(member, "content in postA", StatusType.POSTING, images1);
            Post postB = Post.createPost(member, "content in postB", StatusType.POSTING, images2);

            Post savedPostA = postRepository.save(postA);
            Post savedPostB = postRepository.save(postB);

            Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));

            //when
            List<Post> foundPosts = postRepository.findPosts(savedMemberId, StatusType.POSTING, pageable).getContent();

            //then
            assertEquals(2, foundPosts.size());
            assertEquals(savedPostB.getContent(), foundPosts.get(0).getContent());
            assertEquals(savedPostA.getContent(), foundPosts.get(1).getContent());
            assertEquals(savedPostB.getPostImages().get(0).getUrl(), foundPosts.get(0).getPostImages().get(0).getUrl());
            assertEquals(savedPostA.getPostImages().get(1).getUrl(), foundPosts.get(1).getPostImages().get(1).getUrl());
        }

        @Test
        @DisplayName("실패")
        void findPostsFailureTest() throws Exception {
            //given
            Member member = Member.builder()
                    .userId("test")
                    .build();

            Long savedMemberId = memberRepository.save(member).getId();

            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));

            //when
            Slice<Post> foundPosts = postRepository.findPosts(savedMemberId, StatusType.POSTING, pageable);

            //then
            assertEquals(0, foundPosts.getNumberOfElements());
        }
    }

    @Test
    @DisplayName("게시물 수정 테스트")
    void updatePostSuccessTest() throws Exception {
        //given
        Member member = Member.builder().build();
        memberRepository.save(member);

        Post post1 = Post.createPost(member, "before update", StatusType.POSTING, new ArrayList<>());
        Post post2 = Post.createPost(member, "content", StatusType.POSTING, new ArrayList<>());

        Post savedPost1 = postRepository.save(post1);
        Post savedPost2 = postRepository.save(post2);

        //when
        savedPost1.updatePost("after update", StatusType.POSTING);
        savedPost2.updatePost("content", StatusType.PRIVATE);

        em.flush();
        em.clear();

        Optional<Post> foundPost1 = postRepository.findById(savedPost1.getId());
        Optional<Post> foundPost2 = postRepository.findById(savedPost2.getId());

        //then
        foundPost1.ifPresent(value -> assertEquals(savedPost1.getContent(), value.getContent()));
        foundPost2.ifPresent(value -> assertEquals(savedPost2.getStatus(), value.getStatus()));
    }

    @Test
    @DisplayName("게시물 삭제 테스트")
    void deletePostTest() throws Exception {
        //given
        Member member = Member.builder().build();
        memberRepository.save(member);

        Post post = Post.createPost(member, "content in post", StatusType.POSTING, Arrays.asList());
        Long savedPostId = postRepository.save(post).getId();

        em.flush();
        em.clear();

        Post foundPost = postRepository.findById(savedPostId).orElseThrow(IllegalArgumentException::new);

        //when
        postRepository.delete(foundPost);

        Optional<Post> deletedPost = postRepository.findById(savedPostId);

        //then
        assertEquals(Optional.empty(), deletedPost);
    }


}