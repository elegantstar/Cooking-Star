package toy.cookingstar.domain;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Member {

    private Long id;
    private String userId;
    private String password;
    private String name;
    private String email;
    private String salt;
    private String nickname;
    private String introduction;
    private String gender;
    private String profileImage;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @Builder
    public Member(String userId, String password, String name, String email, String salt, String nickname) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.salt = salt;
        this.nickname = nickname;
    }
}
