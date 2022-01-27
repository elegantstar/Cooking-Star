package toy.cookingstar.service.post;

import java.util.HashMap;
import java.util.List;

import toy.cookingstar.domain.Member;


public interface PostService {

    Member createPost(PostCreateParam postCreateParam);

    List<HashMap<String, String>> getUserPagePostImages(PostImageParam postImageParam);

    int countPosts(Long memberId);
}
