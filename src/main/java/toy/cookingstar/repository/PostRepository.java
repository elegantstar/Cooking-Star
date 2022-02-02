package toy.cookingstar.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import toy.cookingstar.domain.Post;
import toy.cookingstar.domain.PostImage;
import toy.cookingstar.domain.PostWithImage;
import toy.cookingstar.service.post.StatusType;

@Mapper
public interface PostRepository {
    void create(Post post);

    Post findByPostId(Long id);

    void saveImage(PostImage postImage);

    int countPosts(Long memberId);

    List<PostWithImage> findUserPagePostImage(@Param("memberId") Long memberId, @Param("start") int start, @Param("end") int end);

    PostWithImage findPostInfo(@Param("id") Long id);

    void deletePostImages(Long postId);

    void deletePost(Long id);

    void updatePost(@Param("id") Long id, @Param("content") String content, @Param("status") StatusType status);
}
