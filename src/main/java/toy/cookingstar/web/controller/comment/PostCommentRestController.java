package toy.cookingstar.web.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toy.cookingstar.entity.Member;
import toy.cookingstar.service.comment.PostCommentService;
import toy.cookingstar.web.controller.comment.dto.PostCommentDto;
import toy.cookingstar.web.argumentresolver.Login;
import toy.cookingstar.web.controller.comment.dto.PostCommentSaveDto;
import toy.cookingstar.web.controller.comment.dto.PostNestedCommentDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment/post")
public class PostCommentRestController {

    private final PostCommentService postCommentService;

    @PostMapping
    public ResponseEntity<?> createPostComment(@Login Member loginUser,
                                               @RequestBody PostCommentSaveDto postCommentSaveDto) throws Exception {

        if (postCommentSaveDto.getParentCommentId() == null) {
            postCommentService.create(loginUser.getId(), postCommentSaveDto.getPostId(), 0L, postCommentSaveDto.getContent());
            return ResponseEntity.ok().build();
        }

        postCommentService.create(loginUser.getId(), postCommentSaveDto.getPostId(),
                postCommentSaveDto.getParentCommentId(), postCommentSaveDto.getContent());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getCommentsByPostId(@RequestParam Long postId, @RequestParam int page,
                                                                     @RequestParam int size) {

        Slice<PostCommentDto> commentDtoPage = postCommentService.getCommentsByPostId(postId, page, size)
                .map(PostCommentDto::of);

        return ResponseEntity.ok().body(commentDtoPage);
    }

    @GetMapping("/{postId}/count")
    public ResponseEntity<Integer> countCommentsByPostId(@PathVariable("postId") Long postId) {
        int count = postCommentService.countComments(postId);
        return ResponseEntity.ok().body(count);
    }

    @GetMapping("/nested")
    public ResponseEntity<Slice<PostNestedCommentDto>> getNestedCommentsById(@RequestParam Long postId,
                                                                             @RequestParam("postCommentId") Long parentCommentId,
                                                                             @RequestParam int page, @RequestParam int size) {

        Slice<PostNestedCommentDto> nestedCommentDtoPage = postCommentService
                .getNestedCommentsByPostId(postId, parentCommentId, page, size)
                .map(PostNestedCommentDto::of);

        return ResponseEntity.ok().body(nestedCommentDtoPage);
    }

    @PostMapping("/deletion/{commentId}")
    public ResponseEntity<?> deleteComment(@Login Member loginUser, @PathVariable Long commentId) throws Exception {
        postCommentService.deleteComment(loginUser.getId(), commentId);

        return ResponseEntity.ok().build();
    }

}
