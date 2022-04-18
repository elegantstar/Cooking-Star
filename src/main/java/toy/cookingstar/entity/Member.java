package toy.cookingstar.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;
    private String password;
    private String name;
    private String email;
    private String salt;
    private String nickname;
    private String introduction;
    private String gender;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "created_date", insertable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date", insertable = false, updatable = false)
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

    public void updateInfo(String email, String nickname, String introduction, String gender) {
        this.email = email;
        this.nickname = nickname;
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

