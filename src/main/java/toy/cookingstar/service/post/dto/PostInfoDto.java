package toy.cookingstar.service.post.dto;

import lombok.Getter;
import toy.cookingstar.entity.Post;
import toy.cookingstar.entity.PostImage;
import toy.cookingstar.service.post.StatusType;
import toy.cookingstar.service.user.dto.UserInfoDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostInfoDto {

    private Long id;
    private UserInfoDto userInfo;
    private String content;
    private List<PostImage> postImages;
    private StatusType status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static PostInfoDto of(Post post) {
        PostInfoDto dto = new PostInfoDto();
        dto.id = post.getId();
        dto.userInfo = UserInfoDto.of(post.getMember());
        dto.content = post.getContent();
        dto.postImages = post.getPostImages();
        dto.status = post.getStatus();
        dto.createdDate = post.getCreatedDate();
        dto.updatedDate = post.getUpdatedDate();
        return dto;
    }
}
