package toy.cookingstar.service.user;

import java.time.LocalDateTime;

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
    private String profileImage;
    private LocalDateTime updatedDate;

}
