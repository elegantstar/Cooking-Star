package toy.cookingstar.web.controller.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import toy.cookingstar.common.RedisCacheSets;
import toy.cookingstar.entity.Member;

import static toy.cookingstar.common.RedisCacheSets.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {

    private Long id;
    private String userId;
    private String name;
    private String email;
    private String website;
    private String nickname;
    private String introduction;
    private String gender;
    private String profileImage;

    public static UserInfoDto of(Member member) {
        UserInfoDto dto = new UserInfoDto();
        dto.id = member.getId();
        dto.userId = member.getUserId();
        dto.name = member.getName();
        dto.email = member.getEmail();
        dto.website = member.getWebsite();
        dto.nickname = member.getNickname();
        dto.introduction = member.getIntroduction();
        dto.gender = member.getGender();

        if (member.getProfileImage() != null) {
            dto.profileImage = "https://d9voyddk1ma4s.cloudfront.net/images/profile/"
                    + member.getProfileImage().substring(0, 10) + "/" + member.getProfileImage();
        } else {
            dto.profileImage = member.getProfileImage();
        }

        return dto;
    }
}
