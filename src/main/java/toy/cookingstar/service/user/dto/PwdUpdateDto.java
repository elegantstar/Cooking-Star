package toy.cookingstar.service.user.dto;

import lombok.Getter;

@Getter
public class PwdUpdateDto {

    private String userId;
    private String currentPwd;
    private String newPassword1;
    private String newPassword2;
}
