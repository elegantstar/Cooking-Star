package toy.cookingstar.domain;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostWithImage extends Post {

    private List<PostImage> images;

}
