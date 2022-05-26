package toy.cookingstar.web.controller.post.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import toy.cookingstar.entity.Post;

@Data
@NoArgsConstructor
public class PostAndImageUrlDto {

    private Long postId;
    private String imageUrl;

    public static PostAndImageUrlDto of(Post post) {
        PostAndImageUrlDto dto = new PostAndImageUrlDto();
        dto.postId = post.getId();
        dto.imageUrl = post.getPostImages().get(0).getUrl();
        return dto;
    }
}
