package toy.cookingstar.web.mybatiscontroller.member;

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
import toy.cookingstar.mybatisservice.member.MemberService;
import toy.cookingstar.web.controller.member.form.MemberSaveForm;
import toy.cookingstar.web.controller.validator.PwdValidator;

@Slf4j
//@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PwdValidator pwdValidator;

    @GetMapping("/join")
    public String joinForm(@ModelAttribute("member") MemberSaveForm form) {
        return "member/joinForm";
    }

    @PostMapping("/join")
    public String join(@Validated @ModelAttribute("member") MemberSaveForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        pwdValidator.validEquality(form.getPassword1(), form.getPassword2(), bindingResult);

        if (bindingResult.hasErrors()) {
            log.error("errors={}", bindingResult);
            return "member/joinForm";
        }

        Member member = memberService.saveMember(form.getUserId(), form.getPassword1(), form.getName(), form.getEmail());

        redirectAttributes.addFlashAttribute("member", member);
        return (member != null) ? "redirect:/member/welcome" : "member/joinForm";
    }

    @GetMapping("/welcome")
    public String welcome(@ModelAttribute("member") Member member) {
        return "member/welcome";
    }

}
