package toy.cookingstar.web.mybatiscontroller.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import toy.cookingstar.domain.Member;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchDto {

    private String userId;
    private String nickname;
    private String profileImage;

    public static UserSearchDto of(Member member) {
        UserSearchDto userSearchDto = new UserSearchDto();
        userSearchDto.userId = member.getUserId();
        userSearchDto.nickname = member.getNickname();
        userSearchDto.profileImage = member.getProfileImage();
        return userSearchDto;
    }
}
