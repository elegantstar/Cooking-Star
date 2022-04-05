package toy.cookingstar.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}

