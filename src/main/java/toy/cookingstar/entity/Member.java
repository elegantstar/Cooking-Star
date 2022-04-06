package toy.cookingstar.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import toy.cookingstar.service.user.dto.UserInfoDto;

@Getter @Setter
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

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
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

    public void updateInfo(UserInfoDto dto) {
        this.name = dto.getName();
        this.email = dto.getEmail();
        this.nickname = dto.getNickname();
        this.introduction = dto.getIntroduction();
        this.gender = dto.getGender();
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

