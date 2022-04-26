package toy.cookingstar.web.controller.user.form;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class InfoUpdateForm {

    @Length(min = 1, max = 20)
    private String nickname;

    @Length(max = 200)
    private String introduction;

    @NotBlank
    @Email
    private String email;

    private String gender;

}
