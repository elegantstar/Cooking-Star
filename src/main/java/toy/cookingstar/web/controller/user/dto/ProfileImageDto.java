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
        if (member.getProfileImage() != null) {
            String dir = member.getProfileImage().substring(0, 10);
            profileImageDto.imageUrl = "https://d9voyddk1ma4s.cloudfront.net/images/profile/" + dir + "/" + member.getProfileImage();
        } else {
            profileImageDto.imageUrl = member.getProfileImage();
        }
        return profileImageDto;
    }

}
