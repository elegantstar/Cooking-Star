package toy.cookingstar.web.controller.liker;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toy.cookingstar.entity.Member;
import toy.cookingstar.service.liker.PostLikerServiceImpl;
import toy.cookingstar.service.search.dto.UserSearchDto;
import toy.cookingstar.web.argumentresolver.Login;
import toy.cookingstar.web.controller.liker.dto.PostLikerSaveDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like/post")
public class PostLikerRestController {

    private final PostLikerServiceImpl postLikerService;

    @PostMapping
    public ResponseEntity<?> createPostLiker(@Login Member loginUser, @RequestBody PostLikerSaveDto postLikerSaveDto) {
        postLikerService.create(loginUser.getId(), postLikerSaveDto.getPostId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/count")
    public ResponseEntity<?> countPostLiker(@PathVariable("postId") Long postId) {
        int count = postLikerService.countLikers(postId);
        return ResponseEntity.ok().body(count);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostLikers(@PathVariable("postId") Long postId,
                                                              @RequestParam int page, @RequestParam int size) {
        Slice<UserSearchDto> postLikerSlice = postLikerService.getLikers(postId, page, size).map(UserSearchDto::of);
        return ResponseEntity.ok().body(postLikerSlice);
    }

    @GetMapping
    public ResponseEntity<?> checkForLikes(@Login Member loginUser, @RequestParam Long postId) {
        boolean likeOrNot = postLikerService.checkForLikes(loginUser.getId(), postId);
        return ResponseEntity.ok().body(likeOrNot);
    }

    @PostMapping("/{postId}/deletion")
    public ResponseEntity<?> deletePostLiker(@Login Member loginUser, @PathVariable("postId") Long postId) {
        postLikerService.deleteLiker(loginUser.getId(), postId);
        return ResponseEntity.ok().build();
    }
}
