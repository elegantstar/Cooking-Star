package toy.cookingstar.service.liker;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import toy.cookingstar.entity.*;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.repository.PostCommentLikerRepository;
import toy.cookingstar.repository.PostCommentRepository;

@Service
@RequiredArgsConstructor
public class PostCommentLikerServiceImpl implements LikerService {

    private final MemberRepository memberRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostCommentLikerRepository postCommentLikerRepository;

    public void create(Long loginMemberId, Long postCommentId) {
        Member loginUser = memberRepository.findById(loginMemberId).orElseThrow(IllegalArgumentException::new);
        PostComment comment = postCommentRepository.findById(postCommentId).orElseThrow(IllegalArgumentException::new);

        //PostLiker 생성
        PostCommentLiker commentLiker = PostCommentLiker.createCommentLiker(loginUser, comment);

        postCommentLikerRepository.save(commentLiker);
    }

    @Override
    public int countLikers(Long postCommentId) {
        PostComment comment = postCommentRepository.findById(postCommentId).orElseThrow(IllegalArgumentException::new);
        return postCommentLikerRepository.countByPostComment(comment);
    }

    @Override
    public Slice<Member> getLikers(Long postCommentId, int page, int size) {
        PostComment comment = postCommentRepository.findById(postCommentId).orElseThrow(IllegalArgumentException::new);
        Pageable pageable = PageRequest.of(page, size);
        return postCommentLikerRepository.findLikersByPostComment(comment, pageable).map(PostCommentLiker::getMember);
    }

    @Override
    public boolean checkForLikes(Long loginMemberId, Long postCommentId) {
        Member loginUser = memberRepository.findById(loginMemberId).orElseThrow(IllegalArgumentException::new);
        PostComment comment = postCommentRepository.findById(postCommentId).orElseThrow(IllegalArgumentException::new);
        return postCommentLikerRepository.existsByMemberAndPostComment(loginUser, comment);
    }

    @Override
    public void deleteLiker(Long loginMemberId, Long postId) {
        Member loginUser = memberRepository.findById(loginMemberId).orElseThrow(IllegalArgumentException::new);
        PostComment comment = postCommentRepository.findById(postId).orElseThrow(IllegalArgumentException::new);
        PostCommentLiker postCommentLiker = postCommentLikerRepository.findByMemberAndPostComment(loginUser, comment);

        postCommentLikerRepository.delete(postCommentLiker);
    }
}
