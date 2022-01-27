package toy.cookingstar.web.controller.user.form;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class InfoUpdateForm {

    @Size(max = 1)
    private List<MultipartFile> images;

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
