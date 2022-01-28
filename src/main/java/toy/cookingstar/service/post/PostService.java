package toy.cookingstar.service.post;

import java.util.HashMap;
import java.util.List;

import toy.cookingstar.domain.PostWithImage;

public interface PostService {

    void createPost(PostCreateParam postCreateParam);

    List<HashMap<String, String>> getUserPagePostImages(String userId, int start, int end);

    int countPosts(Long memberId);

    PostWithImage getPostInfo(Long memberId, String postUrl);
}
