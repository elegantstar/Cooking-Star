package toy.cookingstar.service.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import toy.cookingstar.entity.Member;
import toy.cookingstar.entity.SearchHistory;

@Data
@AllArgsConstructor
public class UserSearchDto {
    private String userId;
    private String nickname;
    private String profileImage;

    public static UserSearchDto of(SearchHistory history) {
        return new UserSearchDto(history.getSearchedUser().getUserId(),
                history.getSearchedUser().getNickname(),
                history.getSearchedUser().getProfileImage());
    }

    public static UserSearchDto of(Member member) {
        return new UserSearchDto(member.getUserId(), member.getNickname(), member.getProfileImage());
    }
}
