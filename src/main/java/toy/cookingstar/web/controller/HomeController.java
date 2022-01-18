package toy.cookingstar.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import toy.cookingstar.domain.Member;
import toy.cookingstar.service.member.MemberService;
import toy.cookingstar.web.argumentresolver.Login;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberService memberService;

    @GetMapping("/")
    public String loginHome(@Login Member loginMember, Model model) {

        // 세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }

        // 세션에 회원 데이터가 유지되면 로그인 홈
        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}
