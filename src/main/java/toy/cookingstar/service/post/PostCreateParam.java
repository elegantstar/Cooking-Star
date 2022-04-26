package toy.cookingstar.service.post;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostCreateParam {

    private String userId;
    private String content;
    private StatusType status;
    private List<String> storedImages;

}
