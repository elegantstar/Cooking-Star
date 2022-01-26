package toy.cookingstar.repository;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import toy.cookingstar.domain.Post;
import toy.cookingstar.domain.PostImage;
import toy.cookingstar.domain.PostWithImage;

@Mapper
public interface PostRepository {
    void create(Post post);

    void saveImage(PostImage postImage);

    int countPosts(Long memberId);

    List<PostWithImage> findUserPagePostImage(HashMap<String, Object> map);

}
