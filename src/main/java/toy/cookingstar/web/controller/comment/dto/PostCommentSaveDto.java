package toy.cookingstar.web.controller.comment.dto;

import lombok.Getter;

@Getter
public class PostCommentSaveDto {

    private Long postId;
    private Long parentCommentId;
    private String content;

}
