package toy.cookingstar.web.controller.member.form;

import javax.validation.constraints.NotBlank;

public class MemberSaveForm {

    @NotBlank
    private String userId;

    @NotBlank
    private String password1;

    @NotBlank
    private String password2;

    @NotBlank
    private String name;

    @NotBlank
    private String email;

}
