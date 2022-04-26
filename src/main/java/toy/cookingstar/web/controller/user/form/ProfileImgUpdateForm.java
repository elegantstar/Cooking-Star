package toy.cookingstar.web.controller.user.form;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ProfileImgUpdateForm {

    private MultipartFile profileImage;

}
