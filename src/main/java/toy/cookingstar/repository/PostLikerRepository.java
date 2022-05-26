package toy.cookingstar.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.cookingstar.entity.Member;
import toy.cookingstar.entity.Post;
import toy.cookingstar.entity.PostLiker;

public interface PostLikerRepository extends JpaRepository<PostLiker, Long> {

    int countByPost(Post post);

    @Query("select l from PostLiker l where l.post = :post")
    Slice<PostLiker> findLikersByPost(@Param("post") Post post, Pageable pageable);

    boolean existsByMemberAndPost(Member member, Post post);

    PostLiker findByMemberAndPost(Member member, Post post);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from PostLiker l where l.post = :post")
    void deleteAllByPost(@Param("post") Post post);
}
