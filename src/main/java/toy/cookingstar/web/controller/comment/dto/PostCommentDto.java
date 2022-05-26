package toy.cookingstar.web.controller.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import toy.cookingstar.entity.PostComment;
import toy.cookingstar.web.controller.user.dto.UserInfoDto;
import toy.cookingstar.web.controller.user.dto.UserSimpleInfoDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    private String createdDateTimeDiff;
    private String updatedDateTimeDiff;
    private String deletedDateTimeDiff;


    public static PostCommentDto of(PostComment postComment) {
        PostCommentDto dto = new PostCommentDto();
        dto.id = postComment.getId();
        dto.userSimpleInfoDto = UserSimpleInfoDto.of(postComment.getMember());
        dto.postId = postComment.getPost().getId();
        dto.content = postComment.getContent();
        dto.createdDate = postComment.getCreatedDate();
        dto.updatedDate = postComment.getUpdatedDate();
        dto.deletedDate = postComment.getDeletedDate();
        dto.createdDateTimeDiff = getDateTimeDiff(postComment.getCreatedDate());
        dto.updatedDateTimeDiff = getDateTimeDiff(postComment.getUpdatedDate());
        dto.deletedDateTimeDiff = getDateTimeDiff(postComment.getDeletedDate());
        return dto;
    }

    private static String getDateTimeDiff(LocalDateTime localDateTime) {

        if (localDateTime == null) {
            return null;
        }

        LocalDateTime targetDays = localDateTime.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime nowDays = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);

        long dayDiff = targetDays.until(nowDays, ChronoUnit.DAYS);

        if (dayDiff == 0L) {
            LocalDateTime targetHours = localDateTime.truncatedTo(ChronoUnit.HOURS);
            LocalDateTime nowHours = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

            return targetHours.until(nowHours, ChronoUnit.HOURS) + "시간";
        }

        return dayDiff + "일";
    }
}
