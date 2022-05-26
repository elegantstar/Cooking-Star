package toy.cookingstar.web.controller.user.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class InfoUpdateForm {

    @Length(min = 1, max = 20)
    private String nickname;

    @Pattern(regexp = "^((https?):\\/\\/([^:\\/\\s]+)(:([^\\/]*))?((\\/[^\\s/\\/]+)*)?\\/?([^#\\s\\?]*)(\\?([^#\\s]*))?(#(\\w*))?)?")
    private String website;

    @Length(max = 200)
    private String introduction;

    @NotBlank
    @Email
    private String email;

    private String gender;

}
