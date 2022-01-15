package toy.cookingstar.web.controller.member;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import toy.cookingstar.domain.Member;
import toy.cookingstar.web.controller.member.form.MemberSaveForm;

@Controller
@RequestMapping("/member")
public class MemberController {

    @PostMapping("/join")
    public String join(@Validated @ModelAttribute MemberSaveForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

    }

    public Member save() {
        return Member.builder()
                     .name("spring")
                     .email("spring@naver.com")
                     .nickname("ym")
                     .password("dsfsdf")
                     .salt("sdfsdf")
                     .build();
    }

}
