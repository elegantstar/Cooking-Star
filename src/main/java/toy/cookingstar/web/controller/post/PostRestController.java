package toy.cookingstar.web.controller.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import toy.cookingstar.entity.Member;
import toy.cookingstar.service.post.PostService;
import toy.cookingstar.service.post.StatusType;
import toy.cookingstar.service.post.dto.TempStoredPostDto;
import toy.cookingstar.service.user.UserService;
import toy.cookingstar.web.argumentresolver.Login;
import toy.cookingstar.web.controller.post.dto.PostAndImageUrlDto;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PostRestController {

    private final UserService userService;
    private final PostService postService;

    @GetMapping("/post/temporary-storage")
    public List<TempStoredPostDto> temporaryStorage(@Login Member loginUser) {
        Member member = userService.getUserInfoByUserId(loginUser.getUserId());

        return postService.getTemporaryStorage(member.getId(), StatusType.TEMPORARY_STORAGE, 0, 7)
                .stream()
                .map(TempStoredPostDto::of)
                .collect(Collectors.toList());
    }

    @GetMapping("/post")
    public ResponseEntity<?> getPostAndImageUrls(@RequestParam("userId") String userId,
                                                 @RequestParam(value = "postId", required = false) Long lastReadPostId,
                                                 @RequestParam(value = "size", required = false, defaultValue = "6") int size,
                                                 @RequestParam("status") StatusType statusType) throws Exception {

        Slice<PostAndImageUrlDto> postSlice = postService.getUserPagePostImageSlice(userId, lastReadPostId, 0, size, statusType)
                .map(PostAndImageUrlDto::of);
        return ResponseEntity.ok().body(postSlice);
    }
}
