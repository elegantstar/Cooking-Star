package toy.cookingstar.web.controller.user.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class InfoUpdateForm {

    private String userId;

    @Length(min = 1, max = 20)
    private String nickname;

    @Length(max = 200)
    private String introduction;

    @NotBlank
    @Email
    private String email;

    private String gender;

    private String profileImage;

}
