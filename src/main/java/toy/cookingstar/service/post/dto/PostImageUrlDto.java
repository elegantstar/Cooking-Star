package toy.cookingstar.service.post.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostImageUrlDto {

    private List<Long> postIds;
    private List<String> imageUrls;
}
