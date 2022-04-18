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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import toy.cookingstar.entity.Member;
import toy.cookingstar.entity.Post;
import toy.cookingstar.entity.PostComment;
import toy.cookingstar.service.post.StatusType;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class PostCommentRepositoryTest {

    @Autowired
    PostCommentRepository postCommentRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    EntityManager em;

    @Test
    @DisplayName("게시글 댓글 저장 테스트")
    void savePostCommentTest() throws Exception {
        //given
        Member member = Member.builder().build();
        Post post = Post.createPost(member, "content", StatusType.POSTING, Arrays.asList());
        PostComment comment = PostComment.createComment(member, post, null, "content of comment");

        //when
        PostComment savedComment = postCommentRepository.save(comment);

        //then
        assertSame(comment.getMember(), savedComment.getMember());
        assertEquals(comment.getPost().getContent(), savedComment.getPost().getContent());
        assertEquals(comment.getContent(), savedComment.getContent());
        assertNotNull(savedComment.getId());
    }

    @Nested
    @DisplayName("Id로 게시글 댓글 조회 테스트")
    class FindByIdSuccessTest {

        @Test
        @DisplayName("성공")
        void findByIdSuccessTest() throws Exception {
            //given
            Member member = Member.builder().build();
            memberRepository.save(member);

            Post post = Post.createPost(member, "content", StatusType.POSTING, Arrays.asList());
            postRepository.save(post);

            PostComment comment = PostComment.createComment(member, post, null, "content of comment");
            Long savedCommentId = postCommentRepository.save(comment).getId();
            em.flush();
            em.clear();

            //when
            Optional<PostComment> foundComment = postCommentRepository.findById(savedCommentId);

            //then
            foundComment.ifPresent(value -> assertEquals(savedCommentId, value.getId()));
        }

        @Test
        @DisplayName("실패")
        void findByIdFailureTest() throws Exception {
            //when
            Optional<PostComment> foundComment = postCommentRepository.findById(-1L);

            //then
            assertEquals(Optional.empty(), foundComment);
        }
    }

    @Nested
    @DisplayName("postId 및 parentCommentId로 댓글 조회 테스트")
    class FindCommentsTest {

        @Test
        @DisplayName("성공")
        void findCommentsSuccessTest() throws Exception {
            //given
            Member member = Member.builder().build();
            memberRepository.save(member);

            Post post = Post.createPost(member, "content", StatusType.POSTING, Arrays.asList());
            Post savedPost = postRepository.save(post);

            //H2 DB가 parentCommentId의 Default 값인 0L을 넣어 주지 않기 때문에 defaultComment의 Id값을 Default 값으로 간주하여 테스트.
            PostComment defaultComment = PostComment.createComment(member, post, null, "default");
            Long defaultValue = postCommentRepository.save(defaultComment).getId();

            PostComment comment1 = PostComment.createComment(member, post, defaultComment, "content of comment1");
            PostComment comment2 = PostComment.createComment(member, post, defaultComment, "content of comment2");
            postCommentRepository.save(comment1);
            PostComment savedComment2 = postCommentRepository.save(comment2);

            PostComment comment3 = PostComment.createComment(member, post, savedComment2, "content of comment3");
            postCommentRepository.save(comment3);

            em.flush();
            em.clear();

            Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "createdDate"));

            //when
            Slice<PostComment> commentSlice = postCommentRepository.findComments(savedPost.getId(), defaultValue, pageable);
            Slice<PostComment> nestedCommentSlice = postCommentRepository.findComments(savedPost.getId(), savedComment2.getId(), pageable);

            //then
            assertEquals(2, commentSlice.getNumberOfElements());
            assertFalse(commentSlice.hasNext());
            assertEquals(1, nestedCommentSlice.getNumberOfElements());
            assertFalse(nestedCommentSlice.hasNext());
            assertEquals(comment3.getContent(), nestedCommentSlice.getContent().get(0).getContent());
        }

        @Test
        @DisplayName("실패_존재하지 않는 postId")
        void findCommentsFailureTest() throws Exception {
            //given
            Pageable pageable = PageRequest.of(0, 5);
            //when
            Slice<PostComment> slice = postCommentRepository.findComments(-1L, 0L, pageable);
            //then
            assertEquals(0, slice.getNumberOfElements());
        }
    }

    @Test
    @DisplayName("ParentCommentId로 대댓글 존재 확인 테스트")
    void existsNestedCommentsByParentCommentIdTest() throws Exception {
        //given
        Member member = Member.builder().build();
        memberRepository.save(member);

        Post post = Post.createPost(member, "content", StatusType.POSTING, Arrays.asList());
        Post savedPost = postRepository.save(post);

        PostComment comment1 = PostComment.createComment(member, post, null, "content of comment1");
        PostComment savedComment1 = postCommentRepository.save(comment1);

        PostComment comment2 = PostComment.createComment(member, post, savedComment1, "content of comment2");
        PostComment comment3 = PostComment.createComment(member, post, savedComment1, "content of comment3");
        postCommentRepository.save(comment2);
        postCommentRepository.save(comment3);

        em.flush();
        em.clear();

        //when
        Boolean existenceCheck = postCommentRepository.existsNestedCommentsByParentCommentId(savedComment1.getId());

        //then
        assertEquals(true, existenceCheck);
    }

    @Test
    @DisplayName("post로 댓글 수 조회")
    void countCommentsTest() throws Exception {
        //given
        Member member1 = Member.builder().build();
        Member member2 = Member.builder().build();
        memberRepository.save(member1);
        memberRepository.save(member2);

        Post post = Post.createPost(member1, "content", StatusType.POSTING, Arrays.asList());
        postRepository.save(post);

        PostComment comment1 = PostComment.createComment(member2, post, null, "comment1");
        PostComment savedComment1 = postCommentRepository.save(comment1);

        PostComment comment2 = PostComment.createComment(member1, post, savedComment1, "comment2");
        PostComment comment3 = PostComment.createComment(member2, post, savedComment1, "comment3");
        postCommentRepository.save(comment2);
        postCommentRepository.save(comment3);


        //when
        int count = postCommentRepository.countByPost(post);

        //then
        assertEquals(3, count);
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void deletePostCommentTest() throws Exception {
        //given
        Member member = Member.builder().build();
        memberRepository.save(member);

        Post post = Post.createPost(member, "content", StatusType.POSTING, Arrays.asList());
        Post savedPost = postRepository.save(post);

        PostComment comment = PostComment.createComment(member, post, null, "content of comment");
        Long savedCommentId = postCommentRepository.save(comment).getId();

        em.flush();
        em.clear();

        PostComment foundComment = postCommentRepository.findById(savedCommentId).orElseThrow(IllegalArgumentException::new);

        //when
        postCommentRepository.delete(foundComment);

        Optional<PostComment> deletedComment = postCommentRepository.findById(savedCommentId);

        //then
        assertEquals(Optional.empty(), deletedComment);
    }

}