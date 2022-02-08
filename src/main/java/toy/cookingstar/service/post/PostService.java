package toy.cookingstar.service.post;

import java.util.List;

public interface PostService {

    void createPost(PostCreateParam postCreateParam);

    List<String> getUserPagePostImages(String userId, int start, int end);

    int countPosts(Long memberId);
}
