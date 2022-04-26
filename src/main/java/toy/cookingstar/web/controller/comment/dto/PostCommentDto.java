package toy.cookingstar.web.controller.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import toy.cookingstar.entity.PostComment;
import toy.cookingstar.web.controller.user.dto.UserInfoDto;
import toy.cookingstar.web.controller.user.dto.UserSimpleInfoDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentDto {

    private Long id;
    private UserSimpleInfoDto userSimpleInfoDto;
    private Long postId;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime deletedDate;

    public static PostCommentDto of(PostComment postComment) {
        PostCommentDto dto = new PostCommentDto();
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
