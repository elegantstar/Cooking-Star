package toy.cookingstar.web.controller.post.dto;

import lombok.Data;
import toy.cookingstar.service.post.StatusType;

@Data

public class PostDeleteDto {
    private Long memberId;
    private Long postId;
    private StatusType status;
}
