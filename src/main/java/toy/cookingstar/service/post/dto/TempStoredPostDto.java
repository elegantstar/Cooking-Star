package toy.cookingstar.service.post.dto;

import lombok.Data;
import toy.cookingstar.entity.Post;

import java.time.LocalDateTime;

@Data
public class TempStoredPostDto {
    private Long id;
    private String content;
    private LocalDateTime createdDate;
    private String imageUrl;

    public static TempStoredPostDto of(Post post) {
        TempStoredPostDto dto = new TempStoredPostDto();
        dto.id = post.getId();
        dto.content = post.getContent();
        dto.createdDate = post.getCreatedDate();
        dto.imageUrl = post.getPostImages().get(0).getUrl();
        return dto;
    }
}
