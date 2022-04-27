package toy.cookingstar.web.controller.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import toy.cookingstar.entity.Member;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileImageDto {

    private String imageUrl;

    public static ProfileImageDto of(Member member) {
        ProfileImageDto profileImageDto = new ProfileImageDto();
        profileImageDto.imageUrl = member.getProfileImage();
        return profileImageDto;
    }

}
