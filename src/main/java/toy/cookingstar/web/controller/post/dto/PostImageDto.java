package toy.cookingstar.web.controller.post.dto;

import lombok.Data;
import toy.cookingstar.entity.PostImage;

@Data
public class PostImageDto {

    private Long id;
    private String url;
    private int priority;

    public static PostImageDto of(PostImage postImage) {
        PostImageDto dto = new PostImageDto();
        dto.id = postImage.getId();
        dto.url = "https://d9voyddk1ma4s.cloudfront.net/images/post/" + postImage.getUrl().substring(0, 10) + "/" + postImage.getUrl();
        dto.priority = postImage.getPriority();
        return dto;
    }
}
