package toy.cookingstar.web.controller.liker;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toy.cookingstar.entity.Member;
import toy.cookingstar.service.liker.PostCommentLikerServiceImpl;
import toy.cookingstar.service.search.dto.UserSearchDto;
import toy.cookingstar.web.argumentresolver.Login;
import toy.cookingstar.web.controller.liker.dto.PostCommentSaveDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like/post-comment")
public class PostCommentLikerRestController {

    private final PostCommentLikerServiceImpl postCommentLikerService;

    @PostMapping
    public ResponseEntity<?> createPostLiker(@Login Member loginUser, @RequestBody PostCommentSaveDto postCommentSaveDto) {
        postCommentLikerService.create(loginUser.getId(), postCommentSaveDto.getPostCommentId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postCommentId}/count")
    public ResponseEntity<Integer> countPostLiker(@PathVariable("postCommentId") Long postCommentId) {
        int count = postCommentLikerService.countLikers(postCommentId);
        return ResponseEntity.ok().body(count);
    }

    @GetMapping("/{postCommentId}")
    public ResponseEntity<Slice<UserSearchDto>> getPostLikers(@PathVariable("postCommentId") Long postCommentId, int page, int size) {
        Slice<UserSearchDto> postLikerSlice = postCommentLikerService.getLikers(postCommentId, page, size).map(UserSearchDto::of);
        return ResponseEntity.ok().body(postLikerSlice);
    }

    @GetMapping
    public ResponseEntity<Boolean> checkForLikes(@Login Member loginUser, @RequestParam Long postCommentId) {
        boolean likeOrNot = postCommentLikerService.checkForLikes(loginUser.getId(), postCommentId);
        return ResponseEntity.ok().body(likeOrNot);
    }

    @PostMapping("/{postCommentId}/deletion")
    public ResponseEntity<?> deletePostLiker(@Login Member loginUser, @PathVariable("postCommentId") Long postCommentId) {
        postCommentLikerService.deleteLiker(loginUser.getId(), postCommentId);
        return ResponseEntity.ok().build();
    }
}
