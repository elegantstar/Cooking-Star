package toy.cookingstar.service.post.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import toy.cookingstar.service.post.StatusType;

@Getter
@AllArgsConstructor
public class PostCreateDto {

    private Long memberId;
    private String userId;
    private String content;
    private List<String> postImageUrls;
    private StatusType status;

}
