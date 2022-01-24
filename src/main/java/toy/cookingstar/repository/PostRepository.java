package toy.cookingstar.repository;

import org.apache.ibatis.annotations.Mapper;

import toy.cookingstar.domain.Post;
import toy.cookingstar.domain.PostImage;

@Mapper
public interface PostRepository {
    void create(Post post);

    void saveImage(PostImage postImage);
}
