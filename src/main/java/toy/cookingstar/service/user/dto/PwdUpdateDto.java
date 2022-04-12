package toy.cookingstar.service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PwdUpdateDto {

    private String userId;
    private String currentPwd;
    private String newPassword1;
    private String newPassword2;
}
