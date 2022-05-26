package toy.cookingstar.service.liker;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.cookingstar.entity.Member;
import toy.cookingstar.entity.Post;
import toy.cookingstar.entity.PostLiker;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.repository.PostLikerRepository;
import toy.cookingstar.repository.PostRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikerServiceImpl implements LikerService {

    private final PostLikerRepository postLikerRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public void create(Long loginMemberId, Long postId) {
        Member loginUser = memberRepository.findById(loginMemberId).orElseThrow(IllegalArgumentException::new);
        Post post = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);

        //PostLiker 생성
        PostLiker postLiker = PostLiker.createPostLiker(loginUser, post);

        postLikerRepository.save(postLiker);
    }

    public int countLikers(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);
        return postLikerRepository.countByPost(post);
    }

    public Slice<Member> getLikers(Long postId, int page, int size) {
        Post post = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);
        Pageable pageable = PageRequest.of(page, size);
        Slice<PostLiker> likerSlice = postLikerRepository.findLikersByPost(post, pageable);
        likerSlice.forEach(postLiker -> postLiker.getMember().getUserId());
        return likerSlice.map(PostLiker::getMember);
    }

    public boolean checkForLikes(Long loginMemberId, Long postId) {
        Member loginUser = memberRepository.findById(loginMemberId).orElseThrow(IllegalArgumentException::new);
        Post post = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);
        return postLikerRepository.existsByMemberAndPost(loginUser, post);
    }

    @Transactional
    public void deleteLiker(Long loginMemberId, Long postId) {
        Member loginUser = memberRepository.findById(loginMemberId).orElseThrow(IllegalArgumentException::new);
        Post post = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);
        PostLiker postLiker = postLikerRepository.findByMemberAndPost(loginUser, post);

        postLikerRepository.delete(postLiker);
    }
}
