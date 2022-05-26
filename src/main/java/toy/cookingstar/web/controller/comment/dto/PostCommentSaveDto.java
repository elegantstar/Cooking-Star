package toy.cookingstar.web.controller.comment.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class PostCommentSaveDto {

    private Long postId;
    private Long parentCommentId;
    private String content;

}
