package toy.cookingstar.service.post;

import toy.cookingstar.domain.Post;
import toy.cookingstar.domain.PostWithImage;
import toy.cookingstar.web.controller.post.dto.TempStoredData;

public interface PostService {

    void createPost(PostCreateParam postCreateParam);

    Post findPostByPostId(Long postId);

    PostImageUrlParam getUserPagePostImages(String userId, int start, int end, StatusType statusType);

    int countPosts(Long memberId);

    PostWithImage getPostInfo(Long postId);

    void deletePost(String userId, Long postId);

    void updatePost(String userId, Long id, String content, StatusType status);

    TempStoredData getTemporaryStorage(Long memberId, int start, int end, StatusType statusType);
}
