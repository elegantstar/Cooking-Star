package toy.cookingstar.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostImage {

    private Long id;
    private Long postId;
    private String url;
    private int priority;

    @Builder
    public PostImage(Long postId, String url, int priority) {
        this.postId = postId;
        this.url = url;
        this.priority = priority;
    }
}
