package toy.cookingstar.web.controller.member;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import toy.cookingstar.domain.Member;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.service.MemberService;
import toy.cookingstar.web.controller.member.form.MemberSaveForm;

@Slf4j
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @GetMapping("/join")
    public String joinForm(@ModelAttribute MemberSaveForm form) {
        return "member/joinForm";
    }

    @PostMapping("/join")
    public String join(@Validated @ModelAttribute MemberSaveForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (form.getPassword1() != null && form.getPassword2() != null) {
            if (!form.getPassword1().equals(form.getPassword2())) {
                bindingResult.reject("pwInconsistency");
            }
        }

        if (bindingResult.hasErrors()) {
            log.error("errors={}", bindingResult);
            return "member/joinForm";
        }

        Member member = memberService.saveMember(form.getUserId(), form.getPassword1(), form.getName(),
                                                 form.getEmail());

        if (member == null) {
            return "member/joinForm";
        }

        return "member/welcome";
    }

}
