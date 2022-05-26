package toy.cookingstar.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import toy.cookingstar.entity.Member;
import toy.cookingstar.service.user.UserService;
import toy.cookingstar.web.controller.login.form.LoginForm;
import toy.cookingstar.web.controller.user.dto.UserInfoDto;
import toy.cookingstar.web.argumentresolver.Login;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final UserService userService;

    @GetMapping("/")
    public String loginHome(@Login Member loginMember, Model model, @ModelAttribute("loginForm") LoginForm form) {

        // 세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }

        Member member = userService.getUserInfoByUserId(loginMember.getUserId());
        UserInfoDto userInfo = UserInfoDto.of(member);
        model.addAttribute("userPageInfo", userInfo);
        return "redirect:/myPage";
    }
}
