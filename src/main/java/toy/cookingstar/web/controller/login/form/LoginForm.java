package toy.cookingstar.web.controller.login.form;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LoginForm {

    @NotBlank
    private String loginId;

    @NotBlank
    private String password;
}
