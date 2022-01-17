package toy.cookingstar.web.controller.member.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class MemberSaveForm {

    @NotBlank
    @Length(max = 20)
    private String userId;

    @NotBlank
    @Length(min = 12, max = 20)
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[a-zA-Z\\d~!@#$%^&*()+|=]{12,20}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 12자 이상 20자 이내로 입력해야 합니다.")
    private String password1;

    @NotBlank
    @Length(min = 12, max = 20)
    private String password2;

    @NotBlank
    @Length(max = 20)
    private String name;

    @NotBlank
    @Email
    private String email;

}
