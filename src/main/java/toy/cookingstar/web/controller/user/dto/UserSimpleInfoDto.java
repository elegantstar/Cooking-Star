package toy.cookingstar.web.controller.user.dto;

import lombok.Data;
import toy.cookingstar.entity.Member;

@Data
public class UserSimpleInfoDto {

    private Long id;
    private String userId;
    private String profileImage;

    public static UserSimpleInfoDto of(Member member) {
        UserSimpleInfoDto dto = new UserSimpleInfoDto();
        dto.id = member.getId();
        dto.userId = member.getUserId();
        dto.profileImage = member.getProfileImage();
        return dto;
    }

    public static UserSimpleInfoDto of(UserInfoDto userInfoDto) {
        UserSimpleInfoDto dto = new UserSimpleInfoDto();
        dto.id = userInfoDto.getId();
        dto.userId = userInfoDto.getUserId();
        dto.profileImage = userInfoDto.getProfileImage();
        return dto;
    }
}
