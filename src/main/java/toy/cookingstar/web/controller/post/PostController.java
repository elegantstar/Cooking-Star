package toy.cookingstar.web.controller.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import toy.cookingstar.entity.Member;
import toy.cookingstar.entity.Post;
import toy.cookingstar.service.imagestore.ImageStoreService;
import toy.cookingstar.service.post.PostService;
import toy.cookingstar.service.post.StatusType;
import toy.cookingstar.service.post.dto.PostCreateDto;
import toy.cookingstar.service.post.dto.PostInfoDto;
import toy.cookingstar.service.user.UserService;
import toy.cookingstar.web.controller.user.dto.UserInfoDto;
import toy.cookingstar.web.argumentresolver.Login;
import toy.cookingstar.web.controller.post.form.DeleteForm;
import toy.cookingstar.web.controller.post.form.PostEditForm;
import toy.cookingstar.web.controller.post.form.PostForm;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PostController {

    private final UserService userService;
    private final PostService postService;
    private final ImageStoreService imageStoreService;

    @GetMapping("/post/create")
    public String createForm(@ModelAttribute("post") PostForm form) {
        return "post/createPage";
    }

    @GetMapping("/post/{postId}")
    public String postPage(@PathVariable Long postId, @Login Member loginUser, Model model) {

        Post post = postService.findById(postId);
        Member loginMember = userService.getUserInfoByUserId(loginUser.getUserId());

        if (post == null) {
            return "error-page/404";
        }

        PostInfoDto postInfoDto = PostInfoDto.of(post);
        UserInfoDto userInfoDto = postInfoDto.getUserInfo();
        model.addAttribute("userInfo", userInfoDto);

        String createdDateTimeDiff = getDateTimeDiff(postInfoDto.getCreatedDate());
        String updatedDateTimeDiff = getDateTimeDiff(postInfoDto.getUpdatedDate());

        //TODO: userId가 loginUser의 userId와 같으면 수정이 가능한 페이지로, 같지 않으면 조회만 가능한 페이지로 이동
        if (StringUtils.equals(userInfoDto.getUserId(), loginUser.getUserId())) {
            model.addAttribute("postInfo", postInfoDto);
            model.addAttribute("createdDateTimeDiff", createdDateTimeDiff);
            model.addAttribute("updatedDateTimeDiff", updatedDateTimeDiff);
            return "post/myPost";
        }

        //loginUser의 게시물이 아닌 경우, 임시보관 또는 비공개 상태인 게시물에 접근할 수 없음.
        if (postInfoDto.getStatus() != StatusType.POSTING) {
            return "error-page/404";
        }

        model.addAttribute("postInfo", postInfoDto);
        model.addAttribute("createdDateTimeDiff", createdDateTimeDiff);
        model.addAttribute("updatedDateTimeDiff", updatedDateTimeDiff);

        return "post/userPost";
    }

    @PostMapping("/post/deletion")
    public String deletePost(@ModelAttribute DeleteForm form, @Login Member loginUser) {

        Member user = userService.getUserInfoByUserId(loginUser.getUserId());
        Post foundPost = postService.findById(Long.parseLong(form.getPostId()));

        if (foundPost == null) {
            return "error-page/404";
        }

        if (!user.getId().equals(foundPost.getMember().getId())) {
            return "error-page/404";
        }

        postService.deletePost(user.getUserId(), foundPost.getId());

        return "redirect:/myPage";
    }

    @GetMapping("/post/edit/{postId}")
    public String editForm(@PathVariable Long postId, Model model) {
        Post post = postService.findById(postId);
        if (post == null) {
            return "error-page/404";
        }
        PostInfoDto postInfo = PostInfoDto.of(post);
        model.addAttribute("postInfo", postInfo);
        return "post/editForm";
    }

    @PostMapping("/post/edit/{postId}")
    public String update(@Validated @ModelAttribute PostEditForm form, @PathVariable Long postId,
                         BindingResult bindingResult, @Login Member loginUser) {

        if (bindingResult.hasErrors()) {
            log.error("errors={}", bindingResult);
            return "post/createForm";
        }

        Post post = postService.findById(postId);
        Member user = userService.getUserInfoByUserId(loginUser.getUserId());

        if (post == null) {
            return "error-page/404";
        }

        if (!Objects.equals(user.getId(), post.getMember().getId())) {
            return "error-page/404";
        }

        postService.updatePost(user.getUserId(), post.getId(), form.getContent(), form.getStatus());

        return "redirect:/post/" + postId;
    }

    @GetMapping("/post/edit")
    public String redirectEditForm(@RequestParam String postId) {
        return "redirect:/post/edit/" + postId;
    }

    private String getDateTimeDiff(LocalDateTime localDateTime) {
        LocalDateTime targetDays = localDateTime.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime nowDays = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);

        long dayDiff = targetDays.until(nowDays, ChronoUnit.DAYS);

        if (dayDiff == 0L) {
            LocalDateTime targetHours = localDateTime.truncatedTo(ChronoUnit.HOURS);
            LocalDateTime nowHours = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

            return targetHours.until(nowHours, ChronoUnit.HOURS) + "시간";
        }

        return dayDiff + "일";
    }

    @GetMapping("/post/create-sample-page")
    public String createSamplePage() {
        return "post/createPage";
    }
}
