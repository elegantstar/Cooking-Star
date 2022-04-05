package toy.cookingstar.service.post.dto;

import java.util.List;

import lombok.Getter;
import toy.cookingstar.service.post.StatusType;

@Getter
public class PostCreateDto {

    private String userId;
    private String content;
    private StatusType status;
    private List<String> storedImages;

}
