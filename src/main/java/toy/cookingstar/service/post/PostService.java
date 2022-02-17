package toy.cookingstar.service.post;

import java.util.List;

import toy.cookingstar.domain.Post;
import toy.cookingstar.domain.PostWithImage;
import toy.cookingstar.web.controller.post.dto.TempStoredDto;

public interface PostService {

    void createPost(PostCreateParam postCreateParam);

    Post findById(Long postId);

    PostImageUrlParam getUserPagePostImages(String userId, int start, int end, StatusType statusType);

    int countPosts(Long memberId);

    PostWithImage getPostInfo(Long postId);

    void deletePost(String userId, Long postId);

    void updatePost(String userId, Long id, String content, StatusType status);

    List<PostWithImage> getTemporaryStorage(Long memberId, StatusType statusType, int start, int end);
}
