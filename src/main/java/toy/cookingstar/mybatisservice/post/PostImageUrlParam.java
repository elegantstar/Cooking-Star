package toy.cookingstar.mybatisservice.post;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostImageUrlParam {

    List<String> imageUrls;
    List<String> postIds;
}
