package toy.cookingstar.service.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.cookingstar.entity.Member;
import toy.cookingstar.entity.Post;
import toy.cookingstar.entity.PostComment;
import toy.cookingstar.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostDeleteService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostLikerRepository postLikerRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostCommentLikerRepository postCommentLikerRepository;

    /**
     * Transaction 애너테이션 제거 -> 굳이 Rollback을 할 이유가 없음.
     */
    @Transactional
    @Async(value = "postAsyncThreadPool")
    public void deletePost(Long loginMemberId, Long postId) throws Exception {
        Member member = memberRepository.findById(loginMemberId).orElseThrow(IllegalArgumentException::new);
        Post post = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);

        //PostImage 삭제
        postImageRepository.deleteAllByPost(post);

        Pageable pageable = PageRequest.of(0, 1000);
        int numberOfComments = postCommentRepository.countByPost(post);

        //PostCommentLiker 삭제
        for (int i = 0; i < numberOfComments / 1000 + 1; i++) {
            List<Long> commentIdList = postCommentRepository.findByPost(post, pageable).stream()
                    .map(PostComment::getId)
                    .collect(Collectors.toList());
            postCommentLikerRepository.deleteInCommentIds(commentIdList);
        }

        /*
         * PostCommentLiker 삭제
         * hibernate.default_batch_fetch_size=1000 는 hibernate가 N+1 문제 상황에서 사용하는 것.
         * apache commons library를 사용하면 List partition을 손쉽게 사용할 수 있다!
         * 하지만 이렇게 구현하지 않고, paging을 통해서 조회 및 삭제를 진행할 예정.
         * 전체 데이터를 다 긁어 오는 것은 부하가 큰 작업이기 때문.
         */
//        List<Long> commentIdList = postCommentRepository.findByPost(post).stream()
//                .map(PostComment::getId)
//                .collect(Collectors.toList());

//        for (List<Long> partition : ListUtils.partition(commentIdList, 1000)) {
//            postCommentLikerRepository.deleteInCommentIds(partition);
//        }

        //PostComment 삭제
        postCommentRepository.deleteAllByPost(post);

        //PostLiker 삭제
        postLikerRepository.deleteAllByPost(post);

        //Post 삭제
        postRepository.delete(post);
        log.info("DELETE POST: userId=[{}], deletedPostId=[{}]", member.getUserId(), postId);
    }
}
