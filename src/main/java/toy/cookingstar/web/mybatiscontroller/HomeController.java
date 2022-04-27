package toy.cookingstar.web.mybatiscontroller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import toy.cookingstar.domain.Member;
import toy.cookingstar.mybatisservice.user.UserService;
import toy.cookingstar.web.argumentresolver.Login;

//@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;

    @GetMapping("/")
    public String loginHome(@Login Member loginMember, Model model) {

        // 세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }

        Member userPageInfo = userService.getUserInfo(loginMember.getUserId());
        model.addAttribute("userPageInfo", userPageInfo);
        return "redirect:/myPage";
    }
}
