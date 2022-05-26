package toy.cookingstar.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;
    private String password;
    private String name;
    private String email;
    private String salt;
    private String nickname;
    private String website;
    private String introduction;
    private String gender;

    @Column(name = "profile_image")
    private String profileImage;

    @Builder
    public Member(String userId, String password, String name, String email, String salt, String nickname) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.salt = salt;
        this.nickname = nickname;
    }

    public void updateInfo(String email, String nickname, String website, String introduction, String gender) {
        this.email = email;
        this.nickname = nickname;
        this.website = website;
        this.introduction = introduction;
        this.gender = gender;
    }

    public void updatePwd(String newPassword, String newSalt) {
        this.password = newPassword;
        this.salt = newSalt;
    }

    public void deleteProfileImage() {
        this.profileImage = null;
    }

    public void updateProfileImage(String storedProfileImage) {
        this.profileImage = storedProfileImage;
    }
}

