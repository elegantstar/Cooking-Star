package toy.cookingstar.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import toy.cookingstar.entity.Member;
import toy.cookingstar.entity.Post;
import toy.cookingstar.entity.PostImage;
import toy.cookingstar.service.post.StatusType;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class PostImageRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostImageRepository postImageRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    void deleteAllByPost() throws Exception {
        //given
        Member member = Member.builder().build();
        memberRepository.save(member);

        PostImage postImage1 = PostImage.createPostImage("post image url1", 1);
        PostImage postImage2 = PostImage.createPostImage("post image url2", 2);

        Post post = Post.createPost(member, "content in post", StatusType.POSTING, Arrays.asList(postImage1, postImage2));
        Long savedPostId = postRepository.save(post).getId();

        em.flush();
        em.clear();

        Post foundPost = postRepository.findById(savedPostId).orElseThrow(IllegalArgumentException::new);
        Long foundImageId1 = foundPost.getPostImages().get(0).getId();
        Long foundImageId2 = foundPost.getPostImages().get(1).getId();

        //when
        postImageRepository.deleteAllByPost(foundPost);

        Optional<PostImage> deletedPostImage1 = postImageRepository.findById(foundImageId1);
        Optional<PostImage> deletedPostImage2 = postImageRepository.findById(foundImageId2);

        //then
        assertEquals(Optional.empty(), deletedPostImage1);
        assertEquals(Optional.empty(), deletedPostImage2);
    }
}