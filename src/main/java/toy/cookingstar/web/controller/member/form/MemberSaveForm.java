package toy.cookingstar.web.controller.member.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class MemberSaveForm {

    @NotBlank
    @Length(max = 20)
    private String userId;

    @NotBlank
    @Length(min = 12, max = 20)
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
