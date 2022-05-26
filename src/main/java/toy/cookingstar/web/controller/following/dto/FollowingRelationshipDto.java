package toy.cookingstar.web.controller.following.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowingRelationshipDto {
    private boolean following;
    private boolean followed;
}
