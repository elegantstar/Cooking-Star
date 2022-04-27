package toy.cookingstar.web.controller.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.cookingstar.entity.Member;

@Getter
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
        dto.profileImage = member.getProfileImage();
        return dto;
    }
}
