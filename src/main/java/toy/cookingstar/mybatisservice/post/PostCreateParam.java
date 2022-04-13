package toy.cookingstar.mybatisservice.post;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import toy.cookingstar.service.post.StatusType;

@Data
@AllArgsConstructor
public class PostCreateParam {

    private String userId;
    private String content;
    private StatusType status;
    private List<String> storedImages;

}
