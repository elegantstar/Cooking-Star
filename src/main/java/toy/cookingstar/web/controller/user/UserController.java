package toy.cookingstar.web.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import toy.cookingstar.domain.Member;
import toy.cookingstar.service.user.GenderType;
import toy.cookingstar.service.user.PwdUpdateParam;
import toy.cookingstar.service.user.UserService;
import toy.cookingstar.service.user.UserUpdateParam;
import toy.cookingstar.utils.HashUtil;
import toy.cookingstar.web.argumentresolver.Login;
import toy.cookingstar.web.controller.user.form.InfoUpdateForm;
import toy.cookingstar.web.controller.user.form.PwdUpdateForm;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public String userPage(@PathVariable String userId, @Login Member loginUser, Model model) {

        // 요청 받은 userId와 일치하는 회원이 있는지 확인 후 데이터 가져오기 -> 일치하는 회원이 없을 경우 exception 처리 필요함.
        Member userPageInfo = userService.getUserInfo(userId);

        if (userPageInfo == null) {
            return "error-page/404";
        }

        model.addAttribute("userPageInfo", userPageInfo);

        // 요청 받은 userId가 로그인 유저의 userId와 같으면 myPage로
        if (userId.equals(loginUser.getUserId())) {
            return "user/myPage";
        }

        // userPage로
        return "user/userPage";
    }

    @GetMapping("/myPage")
    public String myPage(@Login Member loginUser, Model model) {
        Member userPageInfo = userService.getUserInfo(loginUser.getUserId());
        model.addAttribute("userPageInfo", userPageInfo);
        return "user/myPage";
    }

    @ModelAttribute("genderTypes")
    public GenderType[] genderTypes() {
        return GenderType.values();
    }

    @GetMapping("/myPage/edit")
    public String editForm(@Login Member loginUser, Model model) {
        Member userInfo = userService.getUserInfo(loginUser.getUserId());
        model.addAttribute("userInfo", userInfo);
        return "user/editForm";
    }

    @PostMapping("/myPage/edit")
    public String editInfo(@Validated @ModelAttribute("userInfo") InfoUpdateForm form,
                           BindingResult bindingResult, @Login Member loginUser) {

        form.setUserId(loginUser.getUserId());

        if (userService.isNotAvailableEmail(loginUser.getUserId(), form.getEmail())) {
            bindingResult.reject("edit.email.notAvailable");
        }

        if (bindingResult.hasErrors()) {
            log.error("errors={}", bindingResult);
            return "user/editForm";
        }

        UserUpdateParam userUpdateParam = new UserUpdateParam(form.getUserId(), form.getNickname(),
                                                              form.getIntroduction(),
                                                              form.getEmail(), form.getGender(),
                                                              form.getProfileImage());

        userService.updateInfo(userUpdateParam);

        return "redirect:/user/myPage/edit";
    }

    @GetMapping("/myPage/password/change")
    public String passwordForm(@ModelAttribute("userPwdInfo") PwdUpdateForm form, @Login Member loginUser) {

        Member userInfo = userService.getUserInfo(loginUser.getUserId());
        form.setUserId(userInfo.getUserId());
        form.setProfileImage(userInfo.getProfileImage());

        return "user/pwdForm";
    }

    @PostMapping("/myPage/password/change")
    public String updatePwd(@Validated @ModelAttribute("userPwdInfo") PwdUpdateForm form,
                            BindingResult bindingResult, @Login Member loginUser) {

        form.setUserId(loginUser.getUserId());
        form.setProfileImage(userService.getProfileImage(form.getUserId()));

        if (isWrongPwd(form, loginUser)) {
            bindingResult.reject("error.wrong.password");
        }

        if (form.getNewPassword1() != null && form.getNewPassword2() != null) {

            if (!form.getNewPassword1().equals(form.getNewPassword2())) {
                bindingResult.reject("update.pwd.inconsistency");
            }
        }

        if (bindingResult.hasErrors()) {
            log.error("errors={}", bindingResult);
            return "user/pwdForm";
        }

        PwdUpdateParam pwdUpdateParam = new PwdUpdateParam(form.getUserId(), form.getProfileImage(),
                                                           form.getCurrentPwd(), form.getNewPassword1(),
                                                           form.getNewPassword2());

        userService.updatePwd(pwdUpdateParam);



        return "redirect:/user/myPage/password/updated";
    }

    private boolean isWrongPwd(@ModelAttribute("userPwdInfo") @Validated PwdUpdateForm form, @Login Member loginUser) {
        return !loginUser.getPassword().equals(HashUtil.encrypt(form.getCurrentPwd() + loginUser.getSalt()));
    }

    @GetMapping("/myPage/password/updated")
    public String updatedPwd(@Login Member loginUser, Model model) {
        Member userInfo = userService.getUserInfo(loginUser.getUserId());
        String profileImage = userInfo.getProfileImage();
        String userId = userInfo.getUserId();
        String name = userInfo.getName();

        model.addAttribute("profileImage", profileImage);
        model.addAttribute("userId", userId);
        model.addAttribute("name", name);

        return "user/pwdUpdatedPage";
    }
}
