package toy.cookingstar.service.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PwdUpdateParam {

    private String userId;
    private String currentPwd;
    private String newPassword1;
    private String newPassword2;
}
