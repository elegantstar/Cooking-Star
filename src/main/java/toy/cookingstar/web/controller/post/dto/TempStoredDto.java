package toy.cookingstar.web.controller.post.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import toy.cookingstar.domain.PostWithImage;
import toy.cookingstar.entity.Post;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TempStoredDto {

    private Long id;
    private String content;
    private LocalDateTime createdDate;
    private String imageUrl;

    public static TempStoredDto of(PostWithImage postWithImage) {
        TempStoredDto tempStoredDto = new TempStoredDto();
        tempStoredDto.id = postWithImage.getId();
        tempStoredDto.content = postWithImage.getContent();
        tempStoredDto.createdDate = postWithImage.getCreatedDate();
        tempStoredDto.imageUrl = postWithImage.getImages().get(0).getUrl();

        return tempStoredDto;
    }
}
