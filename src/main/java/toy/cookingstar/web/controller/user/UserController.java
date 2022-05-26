package toy.cookingstar.web.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import toy.cookingstar.entity.Member;
import toy.cookingstar.service.following.FollowingService;
import toy.cookingstar.service.imagestore.ImageStoreService;
import toy.cookingstar.service.imagestore.ImageType;
import toy.cookingstar.service.post.PostService;
import toy.cookingstar.service.post.StatusType;
import toy.cookingstar.service.user.GenderType;
import toy.cookingstar.service.user.UserService;
import toy.cookingstar.service.user.dto.PwdUpdateDto;
import toy.cookingstar.web.controller.post.dto.PostAndImageUrlDto;
import toy.cookingstar.web.controller.user.dto.UserInfoDto;
import toy.cookingstar.service.user.dto.UserInfoUpdateDto;
import toy.cookingstar.utils.HashUtil;
import toy.cookingstar.utils.SessionUtils;
import toy.cookingstar.web.argumentresolver.Login;
import toy.cookingstar.web.controller.user.form.InfoUpdateForm;
import toy.cookingstar.web.controller.user.form.PwdUpdateForm;
import toy.cookingstar.web.controller.validator.PwdValidator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PwdValidator pwdValidator;
    private final PostService postService;
    private final ImageStoreService imageStoreService;
    private final FollowingService followingService;

    /**
     * 유저 페이지
     */
    @GetMapping("/user/{userId}")
    public String userPage(@PathVariable String userId, @Login Member loginUser, Model model) throws Exception {

        // 요청 받은 userId가 로그인 유저의 userId와 같으면 myPage로
        if (StringUtils.equals(userId, loginUser.getUserId())) {
            return "redirect:/myPage";
        }

        // 요청 받은 userId와 일치하는 회원이 있는지 확인 후 데이터 가져오기 -> 일치하는 회원이 없을 경우 exception 처리 필요함.
        Member member = userService.getUserInfoByUserId(userId);
        UserInfoDto userPageInfo = UserInfoDto.of(member);

        model.addAttribute("userPageInfo", userPageInfo);

        int totalPost = postService.countPosts(userPageInfo.getId());
        model.addAttribute("totalPost", totalPost);

        int totalFollower = followingService.countFollowers(member.getId());
        int totalFollowing = followingService.countFollowings(member.getId());
        model.addAttribute("totalFollower", totalFollower);
        model.addAttribute("totalFollowing", totalFollowing);

        // userPage로
        return "user/userPage";
    }

    /**
     * 마이 페이지
     */
    @GetMapping("/myPage")
    public String myPage(@Login Member loginUser, Model model) throws Exception {

        Member member = userService.getUserInfoByUserId(loginUser.getUserId());
        UserInfoDto userInfo = UserInfoDto.of(member);

        model.addAttribute("userPageInfo", userInfo);

        int totalPost = postService.countPosts(userInfo.getId());
        model.addAttribute("totalPost", totalPost);

        int totalFollower = followingService.countFollowers(userInfo.getId());
        int totalFollowing = followingService.countFollowings(userInfo.getId());
        model.addAttribute("totalFollower", totalFollower);
        model.addAttribute("totalFollowing", totalFollowing);

        return "user/myPage";
    }

    @GetMapping("/myPage/edit")
    public String editForm(@Login Member loginUser, Model model) {
        Member member = userService.getUserInfoByUserId(loginUser.getUserId());
        UserInfoDto userInfo = UserInfoDto.of(member);
        model.addAttribute("userInfo", userInfo);
        return "user/editForm";
    }

    @PostMapping("/myPage/edit")
    public String editInfo(@Validated @ModelAttribute("userInfo") InfoUpdateForm form, BindingResult bindingResult,
                           @Login Member loginUser, HttpServletRequest request) throws IOException {

        Member member = userService.getUserInfoByUserId(loginUser.getUserId());
        UserInfoDto userInfo = UserInfoDto.of(member);
        Member foundMember = userService.getUserInfoByEmail(form.getEmail());

        if (foundMember != null) {
            if (!StringUtils.equals(foundMember.getUserId(), userInfo.getUserId())) {
                bindingResult.reject("edit.email.notAvailable");
            }
        }

        if (bindingResult.hasErrors()) {
            log.error("errors={}", bindingResult);
            return "user/editForm";
        }

        UserInfoUpdateDto userInfoUpdateDto = new UserInfoUpdateDto(userInfo.getId(), form.getEmail(),
                form.getNickname(), form.getWebsite(), form.getIntroduction(), form.getGender());

        userService.updateInfo(userInfoUpdateDto);

        // Session member update
        SessionUtils.refreshMember(userService.getUserInfoById(loginUser.getId()), request);

        return "redirect:/myPage/edit";
    }

    @GetMapping("/myPage/password/change")
    public String passwordForm(@ModelAttribute("userPwdInfo") PwdUpdateForm form, @Login Member loginUser, Model model) {
        Member member = userService.getUserInfoByUserId(loginUser.getUserId());
        UserInfoDto userInfo = UserInfoDto.of(member);
        model.addAttribute("userInfo", userInfo);
        return "user/pwdForm";
    }

    @PostMapping("/myPage/password/change")
    public String updatePwd(@Validated @ModelAttribute("userPwdInfo") PwdUpdateForm form,
                            BindingResult bindingResult, @Login Member loginUser, HttpServletRequest request) {

        if (isWrongPwd(form, loginUser)) {
            bindingResult.reject("error.wrong.password");
        }

        pwdValidator.validEquality(form.getNewPassword1(), form.getNewPassword2(), bindingResult);

        if (bindingResult.hasErrors()) {
            log.error("errors={}", bindingResult);
            return "user/pwdForm";
        }

        PwdUpdateDto pwdUpdateDto = new PwdUpdateDto(loginUser.getUserId(), form.getCurrentPwd(),
                form.getNewPassword1(), form.getNewPassword2());

        userService.updatePwd(pwdUpdateDto);

        // Session update
        SessionUtils.refreshMember(userService.getUserInfoById(loginUser.getId()), request);

        return "redirect:/myPage/password/updated";
    }

    @GetMapping("/myPage/password/updated")
    public String updatedPwd(@Login Member loginUser, Model model) {
        Member member = userService.getUserInfoByUserId(loginUser.getUserId());
        UserInfoDto userInfo = UserInfoDto.of(member);
        model.addAttribute("userInfo", userInfo);

        return "user/pwdUpdatedPage";
    }

    @GetMapping("/myPage/private")
    public String privatePage(@Login Member loginUser, Model model) throws Exception {

        Member member = userService.getUserInfoByUserId(loginUser.getUserId());
        UserInfoDto userInfo = UserInfoDto.of(member);

        model.addAttribute("userPageInfo", userInfo);

        int totalPost = postService.countPosts(userInfo.getId());
        model.addAttribute("totalPost", totalPost);

        int totalFollower = followingService.countFollowers(userInfo.getId());
        int totalFollowing = followingService.countFollowings(userInfo.getId());
        model.addAttribute("totalFollower", totalFollower);
        model.addAttribute("totalFollowing", totalFollowing);

        return "user/privatePage";
    }

    /**
     * 프로필 이미지 가져오기
     */
    @ResponseBody
    @GetMapping("/profile/{imageUrl}")
    public Resource userProfileImage(@PathVariable String imageUrl, HttpServletResponse response)
            throws MalformedURLException {
        response.setHeader("Cache-Control", "max-age=120");
        return new UrlResource("https://d9voyddk1ma4s.cloudfront.net/" + imageStoreService
                .getFullPath(ImageType.PROFILE, imageUrl));
    }

    /**
     * 포스트 이미지 가져오기
     */
    @ResponseBody
    @GetMapping("/image/{imageUrl}")
    public Resource userPageImage(@PathVariable String imageUrl, HttpServletResponse response)
            throws MalformedURLException {
        response.setHeader("Cache-Control", "max-age=120");
        return new UrlResource("https://d9voyddk1ma4s.cloudfront.net/" + imageStoreService
                .getFullPath(ImageType.POST, imageUrl));
    }

    @ModelAttribute("genderTypes")
    public GenderType[] genderTypes() {
        return GenderType.values();
    }

    private boolean isWrongPwd(PwdUpdateForm form, Member loginUser) {
        return !StringUtils.equals(loginUser.getPassword(),
                HashUtil.encrypt(form.getCurrentPwd() + loginUser.getSalt()));
    }
}
