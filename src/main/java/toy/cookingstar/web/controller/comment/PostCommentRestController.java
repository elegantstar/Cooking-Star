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
            Long postCommentId = postCommentService.create(loginUser.getId(), postCommentSaveDto.getPostId(), 0L, postCommentSaveDto.getContent());
            return ResponseEntity.ok().body(postCommentId);
        }

        Long postCommentId = postCommentService.create(loginUser.getId(), postCommentSaveDto.getPostId(),
                postCommentSaveDto.getParentCommentId(), postCommentSaveDto.getContent());
        return ResponseEntity.ok().body(postCommentId);
    }

    @GetMapping("/postComment")
    public ResponseEntity<?> getCommentByPostId(@RequestParam Long postCommentId) throws Exception {
        PostCommentDto postCommentDto = PostCommentDto.of(postCommentService.getCommentByPostId(postCommentId));

        if (postCommentDto.getUserSimpleInfoDto().getProfileImage() != null) {
            String dir = postCommentDto.getUserSimpleInfoDto().getProfileImage().substring(0, 10);
            postCommentDto.getUserSimpleInfoDto().setProfileImage("https://d9voyddk1ma4s.cloudfront.net/images/profile/" + dir
                    + "/" + postCommentDto.getUserSimpleInfoDto().getProfileImage());
        }

        return ResponseEntity.ok().body(postCommentDto);
    }

    @GetMapping
    public ResponseEntity<?> getCommentsByPostId(@RequestParam Long postId, @RequestParam int page,
                                                                     @RequestParam int size) {

        Slice<PostCommentDto> commentDtoPage = postCommentService.getCommentsByPostId(postId, page, size)
                .map(PostCommentDto::of);

        for (PostCommentDto dto : commentDtoPage) {
            if (dto.getUserSimpleInfoDto().getProfileImage() != null) {
                String dir = dto.getUserSimpleInfoDto().getProfileImage().substring(0, 10);
                dto.getUserSimpleInfoDto().setProfileImage("https://d9voyddk1ma4s.cloudfront.net/images/profile/" + dir
                        + "/" + dto.getUserSimpleInfoDto().getProfileImage());
            }
        }

        return ResponseEntity.ok().body(commentDtoPage);
    }

    @GetMapping("/{postId}/count")
    public ResponseEntity<?> countCommentsByPostId(@PathVariable("postId") Long postId) {
        int count = postCommentService.countComments(postId);
        return ResponseEntity.ok().body(count);
    }

    @GetMapping("/nested")
    public ResponseEntity<?> getNestedCommentsById(@RequestParam Long postId,
                                                                             @RequestParam("postCommentId") Long parentCommentId,
                                                                             @RequestParam int page, @RequestParam int size) {

        Slice<PostNestedCommentDto> nestedCommentDtoPage = postCommentService
                .getNestedCommentsByPostId(postId, parentCommentId, page, size)
                .map(PostNestedCommentDto::of);

        for (PostNestedCommentDto dto : nestedCommentDtoPage) {
            if (dto.getUserSimpleInfoDto().getProfileImage() != null) {
                String dir = dto.getUserSimpleInfoDto().getProfileImage().substring(0, 10);
                dto.getUserSimpleInfoDto().setProfileImage("https://d9voyddk1ma4s.cloudfront.net/images/profile/" + dir
                        + "/" + dto.getUserSimpleInfoDto().getProfileImage());
            }
        }

        return ResponseEntity.ok().body(nestedCommentDtoPage);
    }

    @PostMapping("/deletion/{commentId}")
    public ResponseEntity<?> deleteComment(@Login Member loginUser, @PathVariable Long commentId) throws Exception {
        postCommentService.deleteComment(loginUser.getId(), commentId);

        return ResponseEntity.ok().build();
    }

}
