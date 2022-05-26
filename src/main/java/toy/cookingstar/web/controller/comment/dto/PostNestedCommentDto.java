package toy.cookingstar.web.controller.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import toy.cookingstar.entity.PostComment;
import toy.cookingstar.web.controller.user.dto.UserSimpleInfoDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostNestedCommentDto {

    private Long id;
    private UserSimpleInfoDto userSimpleInfoDto;
    private Long postId;
    private Long parentCommentId;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime deletedDate;

    public static PostNestedCommentDto of(PostComment postComment) {
        PostNestedCommentDto dto = new PostNestedCommentDto();
        dto.id = postComment.getId();
        dto.userSimpleInfoDto = UserSimpleInfoDto.of(postComment.getMember());
        dto.postId = postComment.getPost().getId();
        dto.content = postComment.getContent();
        dto.createdDate = postComment.getCreatedDate();
        dto.updatedDate = postComment.getUpdatedDate();
        dto.deletedDate = postComment.getDeletedDate();
        return dto;
    }
}
