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
import toy.cookingstar.entity.Post;
import toy.cookingstar.entity.PostLiker;
import toy.cookingstar.service.post.StatusType;

import javax.persistence.EntityManager;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class PostLikerRepositoryTest {

    @Autowired
    PostLikerRepository postLikerRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("게시글 좋아요 저장 테스트")
    void savePostLikerTest() throws Exception {
        //given
        Member member = Member.builder().build();
        Post post = Post.createPost(member, "content", StatusType.POSTING, Arrays.asList());
        PostLiker postLiker = PostLiker.createPostLiker(member, post);

        //when
        PostLiker savedPostLiker = postLikerRepository.save(postLiker);

        //then
        assertSame(postLiker.getMember(), savedPostLiker.getMember());
        assertEquals(postLiker.getPost().getContent(), savedPostLiker.getPost().getContent());
        assertNotNull(savedPostLiker.getId());
    }

    @Test
    @DisplayName("postId로 게시글 좋아요 수 조회 테스트")
    void countByPostTest() throws Exception {
        //given
        Member member1 = Member.builder().build();
        Member member2 = Member.builder().build();
        Member member3 = Member.builder().build();
        Member member4 = Member.builder().build();

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        Post post = Post.createPost(member1, "content", StatusType.POSTING, Arrays.asList());
        postRepository.save(post);

        PostLiker postLiker1 = PostLiker.createPostLiker(member2, post);
        PostLiker postLiker2 = PostLiker.createPostLiker(member3, post);
        PostLiker postLiker3 = PostLiker.createPostLiker(member4, post);

        postLikerRepository.save(postLiker1);
        postLikerRepository.save(postLiker2);
        postLikerRepository.save(postLiker3);

        //when
        int count = postLikerRepository.countByPost(post);

        //then
        assertEquals(3, count);
    }

    @Test
    @DisplayName("Post로 '좋아요' 누른 유저 조회 테스트")
    void findLikersByPostSuccessTest() throws Exception {
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

        PostLiker postLiker1 = PostLiker.createPostLiker(member2, savedPost);
        PostLiker postLiker2 = PostLiker.createPostLiker(member3, savedPost);
        PostLiker postLiker3 = PostLiker.createPostLiker(member4, savedPost);
        postLikerRepository.save(postLiker1);
        postLikerRepository.save(postLiker2);
        postLikerRepository.save(postLiker3);

        Pageable pageable = PageRequest.of(0, 10);

        //when
        Slice<PostLiker> slice = postLikerRepository.findLikersByPost(savedPost, pageable);

        //then
        assertEquals(3, slice.getNumberOfElements());
        assertEquals(savedMember2.getId(), slice.getContent().get(0).getMember().getId());
        assertEquals(savedMember3.getId(), slice.getContent().get(1).getMember().getId());
        assertEquals(savedMember4.getId(), slice.getContent().get(2).getMember().getId());
    }

    @Nested
    @DisplayName("member와 post로 게시글 '좋아요' 여부 확인 테스트")
    class ExistsByMemberAndPostTest {

        @Test
        @DisplayName("'좋아요' 누른 경우")
        void existsByMemberIdAndPostIdTest_Exist() throws Exception {
            //given
            Member member = Member.builder().build();
            Member savedMember = memberRepository.save(member);

            Post post = Post.createPost(member, "content", StatusType.POSTING, Arrays.asList());
            Post savedPost = postRepository.save(post);

            PostLiker postLiker = PostLiker.createPostLiker(savedMember, savedPost);
            postLikerRepository.save(postLiker);

            //when
            boolean LikeOrNot = postLikerRepository.existsByMemberAndPost(savedMember, savedPost);

            //then
            assertTrue(LikeOrNot);
        }

        @Test
        @DisplayName("'좋아요' 누르지 않은 경우")
        void existsByMemberIdAndPostIdTest_DoesNotExist() throws Exception {
            //given
            Member member = Member.builder().build();
            Member savedMember = memberRepository.save(member);

            Post post = Post.createPost(member, "content", StatusType.POSTING, Arrays.asList());
            Post savedPost = postRepository.save(post);

            //when
            boolean LikeOrNot = postLikerRepository.existsByMemberAndPost(savedMember, savedPost);

            //then
            assertFalse(LikeOrNot);
        }
    }

    @Test
    @DisplayName("member 및 post로 PostLiker 조회 테스트")
    void findByMemberAndPostTest() throws Exception {
        //given
        Member member = Member.builder().build();
        Member savedMember = memberRepository.save(member);

        Post post = Post.createPost(member, "content", StatusType.POSTING, Arrays.asList());
        Post savedPost = postRepository.save(post);

        PostLiker postLiker = PostLiker.createPostLiker(savedMember, savedPost);
        PostLiker savedPostLiker = postLikerRepository.save(postLiker);

        em.flush();
        em.clear();

        //when
        PostLiker foundPostLiker = postLikerRepository.findByMemberAndPost(savedMember, savedPost);

        //then
        assertEquals(savedPostLiker.getId(), foundPostLiker.getId());
        assertEquals(savedMember.getId(), foundPostLiker.getMember().getId());
        assertEquals(savedPost.getId(), foundPostLiker.getPost().getId());
    }

    @Test
    @DisplayName("좋아요 기록 삭제")
    void deletePostLikerTest() throws Exception {
        //given
        Member member = Member.builder().build();
        Member savedMember = memberRepository.save(member);

        Post post = Post.createPost(member, "content", StatusType.POSTING, Arrays.asList());
        Post savedPost = postRepository.save(post);

        PostLiker postLiker = PostLiker.createPostLiker(savedMember, savedPost);
        PostLiker savedPostLiker = postLikerRepository.save(postLiker);

        em.flush();
        em.clear();

        PostLiker foundPostLiker = postLikerRepository.findByMemberAndPost(savedMember, savedPost);

        //when
        postLikerRepository.delete(foundPostLiker);

        Optional<PostLiker> deletePostLiker = postLikerRepository.findById(savedPostLiker.getId());

        //then
        assertEquals(Optional.empty(), deletePostLiker);
    }
}