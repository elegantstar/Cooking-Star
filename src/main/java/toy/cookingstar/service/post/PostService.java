package toy.cookingstar.service.post;

import java.util.HashMap;
import java.util.List;

import toy.cookingstar.domain.Member;
import toy.cookingstar.domain.Post;
import toy.cookingstar.domain.PostWithImage;

public interface PostService {

    Member createPost(PostCreateParam postCreateParam);

    List<HashMap<String, String>> getUserPagePostImages(PostImageParam postImageParam);

    int countPosts(Long memberId);

    PostWithImage getPostInfo(Long memberId, String postUrl);
}
