package toy.cookingstar.mybatisservice.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserUpdateParam {

    private String userId;
    private String nickname;
    private String introduction;
    private String email;
    private String gender;
}
