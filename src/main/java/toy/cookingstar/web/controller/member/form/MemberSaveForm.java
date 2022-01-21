package toy.cookingstar.web.controller.member.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class MemberSaveForm {

    @Pattern(regexp = "^[a-z\\d]{5,20}")
    private String userId;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[a-zA-Z\\d~!@#$%^&*()+|=]{12,20}$")
    private String password1;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[a-zA-Z\\d~!@#$%^&*()+|=]{12,20}$")
    private String password2;

    @NotBlank
    @Length(max = 20)
    private String name;

    @NotBlank
    @Email
    private String email;

}
