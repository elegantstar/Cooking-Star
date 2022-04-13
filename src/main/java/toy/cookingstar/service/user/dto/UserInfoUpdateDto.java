package toy.cookingstar.service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoUpdateDto {

    private Long id;
    private String email;
    private String nickname;
    private String introduction;
    private String gender;
}
