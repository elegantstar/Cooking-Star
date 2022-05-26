package toy.cookingstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.cookingstar.entity.Post;
import toy.cookingstar.entity.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from PostImage pi where pi.post = :post")
    void deleteAllByPost(@Param("post") Post post);
}
