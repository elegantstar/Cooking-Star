package toy.cookingstar.web.controller.following.dto;

import lombok.Data;

@Data
public class FollowingDeleteDto {
    private String followingUserId;
    private String followedUserId;
}
