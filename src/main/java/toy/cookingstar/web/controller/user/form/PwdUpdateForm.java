package toy.cookingstar.web.controller.user.form;

import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PwdUpdateForm {

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[a-zA-Z\\d~!@#$%^&*()+|=]{12,20}$")
    private String currentPwd;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[a-zA-Z\\d~!@#$%^&*()+|=]{12,20}$")
    private String newPassword1;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[a-zA-Z\\d~!@#$%^&*()+|=]{12,20}$")
    private String newPassword2;
}
