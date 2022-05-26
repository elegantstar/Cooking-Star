package toy.cookingstar.querytest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import toy.cookingstar.entity.*;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.repository.PostCommentLikerRepository;
import toy.cookingstar.repository.PostCommentRepository;
import toy.cookingstar.repository.PostRepository;
import toy.cookingstar.service.post.StatusType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class DeletePostTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostCommentLikerRepository postCommentLikerRepository;

    @Autowired
    PostCommentRepository postCommentRepository;

    @Test
    void deleteTest() throws Exception {
        //given
        Member member = Member.builder()
                .userId("member")
                .salt("salt")
                .email("email")
                .name("name")
                .password("password")
                .nickname("nickname")
                .build();
        Member savedMember = memberRepository.save(member);
        PostImage postImage = PostImage.createPostImage("123", 1);

        Post post = Post.createPost(savedMember, "post", StatusType.POSTING, Arrays.asList(postImage));
        Post savedPost = postRepository.save(post);

        PostComment comment1 = PostComment.createComment(savedMember, post, null, "comment");
        PostComment savedComment1 = postCommentRepository.save(comment1);

        PostCommentLiker commentLiker1a = PostCommentLiker.createCommentLiker(savedMember, savedComment1);
        PostCommentLiker commentLiker1b = PostCommentLiker.createCommentLiker(savedMember, savedComment1);
        PostCommentLiker commentLiker1c = PostCommentLiker.createCommentLiker(savedMember, savedComment1);
        postCommentLikerRepository.save(commentLiker1a);
        postCommentLikerRepository.save(commentLiker1b);
        postCommentLikerRepository.save(commentLiker1c);

        PostComment comment2 = PostComment.createComment(savedMember, post, null, "comment");
        PostComment savedComment2 = postCommentRepository.save(comment2);

        PostCommentLiker commentLiker2a = PostCommentLiker.createCommentLiker(savedMember, savedComment1);
        PostCommentLiker commentLiker2b = PostCommentLiker.createCommentLiker(savedMember, savedComment1);
        PostCommentLiker commentLiker2c = PostCommentLiker.createCommentLiker(savedMember, savedComment1);
        postCommentLikerRepository.save(commentLiker2a);
        postCommentLikerRepository.save(commentLiker2b);
        postCommentLikerRepository.save(commentLiker2c);

        PostComment comment3 = PostComment.createComment(savedMember, post, null, "comment");
        PostComment savedComment3 = postCommentRepository.save(comment3);

        PostCommentLiker commentLiker3a = PostCommentLiker.createCommentLiker(savedMember, savedComment1);
        PostCommentLiker commentLiker3b = PostCommentLiker.createCommentLiker(savedMember, savedComment1);
        PostCommentLiker commentLiker3c = PostCommentLiker.createCommentLiker(savedMember, savedComment1);
        postCommentLikerRepository.save(commentLiker3a);
        postCommentLikerRepository.save(commentLiker3b);
        postCommentLikerRepository.save(commentLiker3c);

        Pageable pageable = PageRequest.of(0, 50);

        List<Long> postCommentIdList = postCommentRepository.findByPost(savedPost, pageable).stream()
                .map(PostComment::getId)
                .collect(Collectors.toList());

        //when
        postCommentLikerRepository.deleteInCommentIds(postCommentIdList);

        //then

    }
}
