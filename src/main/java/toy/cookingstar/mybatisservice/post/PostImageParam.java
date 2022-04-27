package toy.cookingstar.mybatisservice.post;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostImageParam {

    private String userId;
    private int currentPageNo;
    private int postsPerPage;
    private int countPages;


}
