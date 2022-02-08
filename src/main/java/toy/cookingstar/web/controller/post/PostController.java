package toy.cookingstar.web.controller.post;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import toy.cookingstar.domain.Member;
import toy.cookingstar.domain.Post;
import toy.cookingstar.domain.PostWithImage;
import toy.cookingstar.service.imagestore.ImageStoreService;
import toy.cookingstar.service.post.PostCreateParam;
import toy.cookingstar.service.post.PostService;
import toy.cookingstar.service.post.StatusType;
import toy.cookingstar.service.user.UserService;
import toy.cookingstar.web.argumentresolver.Login;
import toy.cookingstar.web.controller.post.form.DeleteForm;
import toy.cookingstar.web.controller.post.form.PostEditForm;
import toy.cookingstar.web.controller.post.form.PostForm;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PostController {

    private final UserService userService;
    private final PostService postService;
    private final ImageStoreService imageStoreService;

    @GetMapping("/post/create")
    public String createForm(@ModelAttribute("post") PostForm form) {
        return "post/createForm";
    }

    @PostMapping("/post/create")
    public String create(@Validated @ModelAttribute("post") PostForm form, BindingResult bindingResult,
                         @Login Member loginUser) throws IOException {

        form.removeEmptyImage();

        if (CollectionUtils.isEmpty(form.getImages())) {
            bindingResult.reject("post.images.empty");
        }

        if (bindingResult.hasErrors()) {
            log.error("errors={}", bindingResult);
            return "post/createForm";
        }

        Member userInfo = userService.getUserInfo(loginUser.getUserId());

        if (userInfo == null) {
            return "error-page/404";
        }

        // 서버에 이미지 업로드
        List<String> storedImages = imageStoreService.storeImages(form.getImages());

        PostCreateParam postCreateParam = new PostCreateParam(loginUser.getUserId(), form.getContent(), form.getStatus(), storedImages);

        // DB에 post 데이터 저장 + postImage 데이터 저장
        postService.createPost(postCreateParam);

        return "redirect:/myPage";
    }

    @GetMapping("/post/{postUrl}")
    public String postPage(@PathVariable String postUrl, @Login Member loginUser, Model model) {

        long postId = Long.parseLong(postUrl);
        Post foundPost = postService.findPostByPostId(postId);
        Member loginMember = userService.getUserInfo(loginUser.getUserId());

        if (foundPost == null || loginMember == null) {
            return "error-page/404";
        }

        Member userInfo = userService.getUserInfo(foundPost.getMemberId());

        if (userInfo == null) {
            return "error-page/404";
        }

        model.addAttribute("userInfo", userInfo);

        //user의 memberId와 postUrl을 통해 해당 post의 data를 가져온다.
        PostWithImage postInfo = postService.getPostInfo(postId);

        if (postInfo == null) {
            return "error-page/404";
        }

        String createdDateDiff = getDayDiff(postInfo.getCreatedDate());
        String updatedDateDiff = getDayDiff(postInfo.getUpdatedDate());

        //TODO: userId가 loginUser의 userId와 같으면 수정이 가능한 페이지로, 같지 않으면 조회만 가능한 페이지로 이동
        if (StringUtils.equals(userInfo.getUserId(), loginUser.getUserId())) {
            model.addAttribute("postInfo", postInfo);
            model.addAttribute("createdDateDiff", createdDateDiff);
            model.addAttribute("updatedDateDiff", updatedDateDiff);
            return "post/myPost";
        }

        //loginUser의 게시물이 아닌 경우, 임시보관 또는 비공개 상태인 게시물에 접근할 수 없음.
        if (postInfo.getStatus() != StatusType.POSTING) {
            return "error-page/404";
        }

        model.addAttribute("postInfo", postInfo);
        model.addAttribute("createdDateDiff", createdDateDiff);
        model.addAttribute("updatedDateDiff", updatedDateDiff);

        return "post/userPost";
    }

    @PostMapping("/post/deletion")
    public String deletePost(@ModelAttribute DeleteForm form, @Login Member loginUser, Model model) {
        Member userInfo = userService.getUserInfo(loginUser.getUserId());
        Post foundPost = postService.findPostByPostId(Long.parseLong(form.getPostId()));

        if (userInfo == null || foundPost == null) {
            return "error-page/404";
        }

        if (!userInfo.getId().equals(foundPost.getMemberId())) {
            return "error-page/404";
        }

        postService.deletePost(userInfo.getUserId(), foundPost.getId());

        return "redirect:/myPage";
    }

    @GetMapping("/post/edit/{postUrl}")
    public String editForm(@PathVariable String postUrl, Model model) {
        PostWithImage postInfo = postService.getPostInfo(Long.parseLong(postUrl));
        if (postInfo == null) {
            return "error-page/404";
        }
        model.addAttribute("postInfo", postInfo);
        return "post/editForm";
    }

    @PostMapping("/post/edit/{postUrl}")
    public String update(@Validated @ModelAttribute PostEditForm form, @PathVariable String postUrl,
                         BindingResult bindingResult, @Login Member loginUser) {

        if (bindingResult.hasErrors()) {
            log.error("errors={}", bindingResult);
            return "post/createForm";
        }

        PostWithImage postInfo = postService.getPostInfo(Long.parseLong(postUrl));
        Member userInfo = userService.getUserInfo(loginUser.getUserId());

        if (postInfo == null || userInfo == null) {
            return "error-page/404";
        }

        if (!userInfo.getId().equals(postInfo.getMemberId())) {
            return "error-page/404";
        }

        postService.updatePost(userInfo.getUserId(), postInfo.getId(), form.getContent(), form.getStatus());

        return "redirect:/post/" + postUrl;
    }

    private String getDayDiff(LocalDateTime localDateTime) {
        LocalDateTime targetDay = localDateTime.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime nowDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);

        int compareResult = nowDay.compareTo(targetDay);

        return compareResult + "일";
    }
}
