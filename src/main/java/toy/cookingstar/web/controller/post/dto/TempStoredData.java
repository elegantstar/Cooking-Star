package toy.cookingstar.web.controller.post.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import toy.cookingstar.domain.Post;

@Data
@AllArgsConstructor
public class TempStoredData {

    List<Post> tempStoredPostInfo;

    List<String> tempStoredImages;
}
