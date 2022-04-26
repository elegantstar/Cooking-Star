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
import toy.cookingstar.entity.*;
import toy.cookingstar.service.post.StatusType;

import javax.persistence.EntityManager;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class PostCommentLikerRepositoryTest {

    @Autowired
    PostCommentLikerRepository postCommentLikerRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostCommentRepository postCommentRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("댓글 좋아요 저장 테스트")
    void savePostCommentLikerTest() throws Exception {
        //given
        Member member = Member.builder().build();
        Post post = Post.createPost(member, "content", StatusType.POSTING, Arrays.asList());
        PostComment comment = PostComment.createComment(member, post, null, "comment");
        PostCommentLiker commentLiker = PostCommentLiker.createCommentLiker(member, comment);
        //when
        PostCommentLiker savedCommentLiker = postCommentLikerRepository.save(commentLiker);

        //then
        assertSame(commentLiker.getMember(), savedCommentLiker.getMember());
        assertEquals(commentLiker.getPostComment().getContent(), savedCommentLiker.getPostComment().getContent());
        assertNotNull(savedCommentLiker.getId());
    }

    @Test
    @DisplayName("postComment로 댓글 좋아요 수 조회 테스트")
    void countByPostCommentTest() throws Exception {
        //given
        Member member1 = Member.builder().build();
        Member member2 = Member.builder().build();
        Member member3 = Member.builder().build();
        Member member4 = Member.builder().build();

        Member savedMember1 = memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        Post post = Post.createPost(member1, "content", StatusType.POSTING, Arrays.asList());
        Post savedPost = postRepository.save(post);

        PostComment comment = PostComment.createComment(savedMember1, savedPost, null, "content");
        PostComment savedComment = postCommentRepository.save(comment);

        PostCommentLiker commentLiker1 = PostCommentLiker.createCommentLiker(member2, comment);
        PostCommentLiker commentLiker2 = PostCommentLiker.createCommentLiker(member3, comment);
        PostCommentLiker commentLiker3 = PostCommentLiker.createCommentLiker(member4, comment);

        postCommentLikerRepository.save(commentLiker1);
        postCommentLikerRepository.save(commentLiker2);
        postCommentLikerRepository.save(commentLiker3);

        //when
        int count = postCommentLikerRepository.countByPostComment(savedComment);

        //then
        assertEquals(3, count);
    }

    @Test
    @DisplayName("PostComment로 '좋아요' 누른 유저 조회 테스트")
    void findLikersByPostCommentSuccessTest() throws Exception {
        //given
        Member member1 = Member.builder().build();
        Member member2 = Member.builder().build();
        Member member3 = Member.builder().build();
        Member member4 = Member.builder().build();

        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);
        Member savedMember3 = memberRepository.save(member3);
        Member savedMember4 = memberRepository.save(member4);

        Post post = Post.createPost(savedMember1, "content", StatusType.POSTING, Arrays.asList());
        Post savedPost = postRepository.save(post);

        PostComment comment = PostComment.createComment(savedMember1, savedPost, null, "content");
        PostComment savedComment = postCommentRepository.save(comment);

        PostCommentLiker commentLiker1 = PostCommentLiker.createCommentLiker(member2, comment);
        PostCommentLiker commentLiker2 = PostCommentLiker.createCommentLiker(member3, comment);
        PostCommentLiker commentLiker3 = PostCommentLiker.createCommentLiker(member4, comment);

        postCommentLikerRepository.save(commentLiker1);
        postCommentLikerRepository.save(commentLiker2);
        postCommentLikerRepository.save(commentLiker3);

        Pageable pageable = PageRequest.of(0, 10);

        //when
        Slice<PostCommentLiker> slice = postCommentLikerRepository.findLikersByPostComment(savedComment, pageable);

        //then
        assertEquals(3, slice.getNumberOfElements());
        assertEquals(savedMember2.getId(), slice.getContent().get(0).getMember().getId());
        assertEquals(savedMember3.getId(), slice.getContent().get(1).getMember().getId());
        assertEquals(savedMember4.getId(), slice.getContent().get(2).getMember().getId());
    }

    @Nested
    @DisplayName("member와 postComment로 댓글 '좋아요' 여부 확인 테스트")
    class ExistsByMemberAndPostCommentTest {

        @Test
        @DisplayName("'좋아요' 누른 경우")
        void existsByMemberAndPostTest_Exist() throws Exception {
            //given
            Member member = Member.builder().build();
            Member savedMember = memberRepository.save(member);

            Post post = Post.createPost(member, "content", StatusType.POSTING, Arrays.asList());
            Post savedPost = postRepository.save(post);

            PostComment comment = PostComment.createComment(savedMember, savedPost, null, "content");
            PostComment savedComment = postCommentRepository.save(comment);

            PostCommentLiker postCommentLiker = PostCommentLiker.createCommentLiker(savedMember, savedComment);
            postCommentLikerRepository.save(postCommentLiker);

            //when
            boolean LikeOrNot = postCommentLikerRepository.existsByMemberAndPostComment(savedMember, savedComment);

            //then
            assertTrue(LikeOrNot);
        }

        @Test
        @DisplayName("'좋아요' 누르지 않은 경우")
        void existsByMemberAndPostTest_DoesNotExist() throws Exception {
            //given
            Member member = Member.builder().build();
            Member savedMember = memberRepository.save(member);

            Post post = Post.createPost(member, "content", StatusType.POSTING, Arrays.asList());
            Post savedPost = postRepository.save(post);

            PostComment comment = PostComment.createComment(savedMember, savedPost, null, "content");
            PostComment savedComment = postCommentRepository.save(comment);

            //when
            boolean LikeOrNot = postCommentLikerRepository.existsByMemberAndPostComment(savedMember, savedComment);

            //then
            assertFalse(LikeOrNot);
        }
    }

    @Test
    @DisplayName("member 및 postComment로 PostCommentLiker 조회 테스트")
    void findByMemberAndPostTest() throws Exception {
        //given
        Member member = Member.builder().build();
        Member savedMember = memberRepository.save(member);

        Post post = Post.createPost(member, "content", StatusType.POSTING, Arrays.asList());
        Post savedPost = postRepository.save(post);

        PostComment comment = PostComment.createComment(savedMember, savedPost, null, "content");
        PostComment savedComment = postCommentRepository.save(comment);

        PostCommentLiker postCommentLiker = PostCommentLiker.createCommentLiker(savedMember, savedComment);
        PostCommentLiker savedCommentLiker = postCommentLikerRepository.save(postCommentLiker);

        em.flush();
        em.clear();

        //when
        PostCommentLiker foundPostLiker = postCommentLikerRepository.findByMemberAndPostComment(savedMember, savedComment);

        //then
        assertEquals(savedCommentLiker.getId(), foundPostLiker.getId());
        assertEquals(savedMember.getId(), foundPostLiker.getMember().getId());
        assertEquals(savedPost.getId(), foundPostLiker.getPostComment().getId());
    }

    @Test
    @DisplayName("좋아요 기록 삭제")
    void deletePostCommentLikerTest() throws Exception {
        //given
        Member member = Member.builder().build();
        Member savedMember = memberRepository.save(member);

        Post post = Post.createPost(member, "content", StatusType.POSTING, Arrays.asList());
        Post savedPost = postRepository.save(post);

        PostComment comment = PostComment.createComment(savedMember, savedPost, null, "content");
        PostComment savedComment = postCommentRepository.save(comment);

        PostCommentLiker postCommentLiker = PostCommentLiker.createCommentLiker(savedMember, savedComment);
        PostCommentLiker savedCommentLiker = postCommentLikerRepository.save(postCommentLiker);

        em.flush();
        em.clear();

        PostCommentLiker foundCommentLiker = postCommentLikerRepository.findByMemberAndPostComment(savedMember, savedComment);

        //when
        postCommentLikerRepository.delete(foundCommentLiker);

        Optional<PostCommentLiker> deletePostCommentLiker = postCommentLikerRepository.findById(savedCommentLiker.getId());

        //then
        assertEquals(Optional.empty(), deletePostCommentLiker);
    }
}