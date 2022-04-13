package toy.cookingstar.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import toy.cookingstar.entity.Member;
import toy.cookingstar.service.user.UserJpaService;
import toy.cookingstar.service.user.dto.UserInfoDto;
import toy.cookingstar.web.argumentresolver.Login;

@Controller
@RequiredArgsConstructor
public class HomeJpaController {

    private final UserJpaService userService;

    @GetMapping("/")
    public String loginHome(@Login Member loginMember, Model model) {

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
