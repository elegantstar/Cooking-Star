package toy.cookingstar.service.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import toy.cookingstar.entity.PostComment;
import toy.cookingstar.service.user.dto.UserInfoDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentDto {

    private Long id;
    private UserInfoDto userInfoDto;
    private Long postId;
    private Long parentCommentId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime deletedDate;

    public static PostCommentDto of(PostComment postComment) {
        PostCommentDto dto = new PostCommentDto();
        dto.id = postComment.getId();
        dto.userInfoDto = UserInfoDto.of(postComment.getMember());
        dto.postId = postComment.getPost().getId();
        dto.parentCommentId = postComment.getParentComment().getId();
        dto.createdDate = postComment.getCreatedDate();
        dto.updatedDate = postComment.getUpdatedDate();
        dto.deletedDate = postComment.getDeletedDate();
        return dto;
    }
}
