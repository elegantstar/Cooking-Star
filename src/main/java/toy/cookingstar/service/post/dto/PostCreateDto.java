package toy.cookingstar.service.post.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import toy.cookingstar.service.post.StatusType;

@Getter
@AllArgsConstructor
public class PostCreateDto {

    private String userId;
    private String content;
    private StatusType status;
    private List<String> storedImages;

}
