package toy.cookingstar.web.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toy.cookingstar.entity.Member;
import toy.cookingstar.service.comment.PostCommentService;
import toy.cookingstar.service.comment.dto.PostCommentDto;
import toy.cookingstar.web.argumentresolver.Login;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment/post")
public class PostCommentRestController {

    private final PostCommentService postCommentService;

    @PostMapping
    public ResponseEntity<?> createPostComment(@Login Member loginUser,
                                               @RequestParam Long postId,
                                               @RequestParam Long parentCommentId,
                                               @RequestParam String content) throws Exception {
        if (parentCommentId == null) {
            postCommentService.create(loginUser.getId(), postId, 0L, content);
            return ResponseEntity.ok().build();
        }

        postCommentService.create(loginUser.getId(), postId, parentCommentId, content);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Slice<PostCommentDto>> getCommentsByPostId(@RequestParam Long postId, @RequestParam int page,
                                                 @RequestParam int size) {
        Long parentCommentId = 0L;

        Slice<PostCommentDto> commentDtoPage = postCommentService.getByPostId(postId, parentCommentId, page, size)
                .map(PostCommentDto::of);

        return ResponseEntity.ok().body(commentDtoPage);
    }

    @GetMapping("/{postId}/count")
    public ResponseEntity<Integer> countCommentsByPostId(@PathVariable("postId") Long postId) {
        int count = postCommentService.countComments(postId);
        return ResponseEntity.ok().body(count);
    }

    @GetMapping("/nested")
    public ResponseEntity<Slice<PostCommentDto>> getNestedCommentsById(@RequestParam Long postId,
                                                   @RequestParam("postCommentId") Long parentCommentId,
                                                   @RequestParam int page, @RequestParam int size) {

        Slice<PostCommentDto> nestedCommentDtoPage = postCommentService.getByPostId(postId, parentCommentId, page, size)
                .map(PostCommentDto::of);

        return ResponseEntity.ok().body(nestedCommentDtoPage);
    }

    @PostMapping("/deletion/{commentId}")
    public ResponseEntity<?> deleteComment(@Login Member loginUser, @PathVariable Long commentId) throws Exception {
        postCommentService.deleteComment(loginUser.getId(), commentId);

        return ResponseEntity.ok().build();
    }

}
