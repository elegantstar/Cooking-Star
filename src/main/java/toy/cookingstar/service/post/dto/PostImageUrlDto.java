package toy.cookingstar.service.post.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class PostImageUrlDto {

    private List<Long> postIds;
    private List<String> imageUrls;
}
