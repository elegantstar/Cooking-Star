package toy.cookingstar.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.cookingstar.entity.Member;
import toy.cookingstar.entity.Post;
import toy.cookingstar.entity.PostComment;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.repository.PostCommentRepository;
import toy.cookingstar.repository.PostRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void create(Long loginMemberId, Long postId, Long parentCommentId, String content) {

        Member loginUser = memberRepository.findById(loginMemberId).orElseThrow(IllegalArgumentException::new);
        Post post = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);
        PostComment parentComment = postCommentRepository.findById(parentCommentId).orElse(null);

        //PostComment 생성
        PostComment comment = PostComment.createComment(loginUser, post, parentComment, content);

        postCommentRepository.save(comment);
    }

    public Slice<PostComment> getCommentsByPostId(Long postId, int page, int size) {
        Post post = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdDate"));

        Slice<PostComment> slice = postCommentRepository.findComments(post.getId(), pageable);
        slice.getContent().forEach(postComment -> postComment.getMember().getUserId());

        return slice;
    }

    public Slice<PostComment> getNestedCommentsByPostId(Long postId, Long parentCommentId, int page, int size) {
        Post post = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdDate"));

        Slice<PostComment> slice = postCommentRepository.findNestedComments(post.getId(), parentCommentId, pageable);
        slice.getContent().forEach(postComment -> postComment.getMember().getUserId());

        return slice;
    }

    public int countComments(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);
        return postCommentRepository.countByPost(post);
    }

    @Transactional
    public void deleteComment(Long loginMemberId, Long commentId) throws Exception {
        Member loginUser = memberRepository.findById(loginMemberId).orElseThrow(IllegalArgumentException::new);
        PostComment postComment = postCommentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);

        if (!loginUser.getId().equals(postComment.getMember().getId())) {
            throw new IllegalArgumentException();
        }

        //삭제하려는 댓글에 대댓글이 있는 경우, 내용을 삭제하고 삭제일자 삽입.
        if (postCommentRepository.existsNestedCommentsByParentCommentId(commentId)) {
            postComment.deleteComment();
            return;
        }

        //대댓글이 없는 경우 db 테이블에서 삭제.
        postCommentRepository.delete(postComment);
    }
}
