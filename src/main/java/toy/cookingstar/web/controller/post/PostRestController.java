package toy.cookingstar.web.controller.post;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import toy.cookingstar.common.CustomSlice;
import toy.cookingstar.entity.Member;
import toy.cookingstar.service.imagestore.ImageStoreService;
import toy.cookingstar.service.post.PostCacheService;
import toy.cookingstar.service.post.PostDeleteService;
import toy.cookingstar.service.post.PostService;
import toy.cookingstar.service.post.StatusType;
import toy.cookingstar.service.post.dto.PostCreateDto;
import toy.cookingstar.service.post.dto.TempStoredPostDto;
import toy.cookingstar.service.user.UserService;
import toy.cookingstar.web.argumentresolver.Login;
import toy.cookingstar.web.controller.post.dto.PostAndImageUrlDto;
import toy.cookingstar.web.controller.post.dto.PostDeleteDto;
import toy.cookingstar.web.controller.post.dto.PostSaveDto;
import toy.cookingstar.web.controller.post.dto.UploadedImageUrlDto;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PostRestController {

    private final UserService userService;
    private final PostService postService;
    private final PostCacheService postCacheService;
    private final ImageStoreService imageStoreService;
    private final PostDeleteService postDeleteService;

    @GetMapping("/post/temporary-storage")
    public List<TempStoredPostDto> temporaryStorage(@Login Member loginUser) {
        Member member = userService.getUserInfoByUserId(loginUser.getUserId());

        List<TempStoredPostDto> tempStoredPosts = postService.getTemporaryStorage(member.getId(), StatusType.TEMPORARY_STORAGE, 0, 7)
                .stream()
                .map(TempStoredPostDto::of)
                .collect(Collectors.toList());

        for (TempStoredPostDto tempStoredPost : tempStoredPosts) {
            String dir = tempStoredPost.getImageUrl().substring(0, 10);
            tempStoredPost.setImageUrl("https://d9voyddk1ma4s.cloudfront.net/images/post/" + dir + "/" + tempStoredPost.getImageUrl());
        }

        return tempStoredPosts;
    }

    @GetMapping("/post")
    public ResponseEntity<?> getPostAndImageUrls(@Login Member loginUser, @RequestParam("userId") String userId,
                                                 @RequestParam(value = "postId", required = false) Long lastReadPostId,
                                                 @RequestParam(value = "size", required = false, defaultValue = "12") int size,
                                                 @RequestParam("status") StatusType statusType) {

        boolean isMyPage = isMyPage(loginUser.getUserId(), userId);

        CustomSlice<PostAndImageUrlDto> postSlice =
                postCacheService.getUserPagePostImageSlice(userId, lastReadPostId, size, statusType, isMyPage);

        for (PostAndImageUrlDto dto : postSlice.getContent()) {
            if (dto.getImageUrl() != null) {
                String dir = dto.getImageUrl().substring(0, 10);
                dto.setImageUrl("https://d9voyddk1ma4s.cloudfront.net/images/post/" + dir + "/" + dto.getImageUrl());
            }
        }
        return ResponseEntity.ok().body(postSlice);
    }

    private boolean isMyPage(String loginUserId, String userId) {
        return StringUtils.equals(loginUserId, userId);
    }

    @PostMapping("/post/image-upload")
    public ResponseEntity<?> uploadPostImages(@Login Member loginUser, @RequestParam("image") List<MultipartFile> multipartFiles) throws IOException {
        userService.getUserInfoById(loginUser.getId());
        UploadedImageUrlDto imageUrls = UploadedImageUrlDto.of(imageStoreService.storeImages(multipartFiles));
        return ResponseEntity.ok().body(imageUrls);
    }


    @PostMapping("/posting")
    public ResponseEntity<?> createPost(@Login Member loginUser, @RequestBody PostSaveDto postSaveDto) throws Exception {
        PostCreateDto postCreateDto = new PostCreateDto(loginUser.getId(), loginUser.getUserId(), postSaveDto.getContent(),
                postSaveDto.getPostImageUris(), postSaveDto.getStatus());
        Long postId = postService.create(postCreateDto);
        return ResponseEntity.ok().body(postId);
    }

    @DeleteMapping("/post")
    public ResponseEntity<?> deletePost(@Login Member loginUser, @RequestBody PostDeleteDto postDeleteDto) throws Exception {
        if (!Objects.equals(loginUser.getId(), postDeleteDto.getMemberId())) {
            return ResponseEntity.badRequest().build();
        }
        postService.changeIntoDeletedState(loginUser.getUserId(), postDeleteDto.getPostId(), postDeleteDto.getStatus());
        postDeleteService.deletePost(postDeleteDto.getMemberId(), postDeleteDto.getPostId());

        return ResponseEntity.ok().build();
    }
}
