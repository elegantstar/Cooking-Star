package toy.cookingstar.web.controller.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import toy.cookingstar.entity.Member;
import toy.cookingstar.service.member.MemberJpaService;
import toy.cookingstar.service.user.UserJpaService;
import toy.cookingstar.service.user.dto.UserInfoDto;
import toy.cookingstar.web.controller.member.form.MemberSaveForm;
import toy.cookingstar.web.controller.validator.PwdValidator;

@Slf4j
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberJpaController {

    private final MemberJpaService memberService;
    private final UserJpaService userService;
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

        Long memberId = memberService.saveMember(form.getUserId(), form.getPassword1(), form.getName(), form.getEmail());
        Member member = userService.getUserInfoById(memberId);

        UserInfoDto memberInfo = UserInfoDto.of(member);

        redirectAttributes.addFlashAttribute("member", memberInfo);
        return "redirect:/member/welcome";
    }

    @GetMapping("/welcome")
    public String welcome(@ModelAttribute("member") UserInfoDto member) {
        return "member/welcome";
    }

}
