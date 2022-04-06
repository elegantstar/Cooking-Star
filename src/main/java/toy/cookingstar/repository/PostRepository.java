package toy.cookingstar.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import toy.cookingstar.entity.Post;
import toy.cookingstar.service.post.StatusType;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p join fetch p.postImages pi" +
            " where p.member.id = :memberId and p.status = :status and pi.priority = 1")
    Slice<Post> findPosts(@Param("memberId") Long memberId, @Param("status") StatusType status, Pageable pageable);

    int countByMemberId(Long memberId);

    @Query("select p from Post p join fetch p.postImages pi" +
            " where p.member.id = :memberId and p.status = :status and pi.priority = 1")
    List<Post> findTemporaryStoredPosts(@Param("memberId") Long memberId, @Param("status") StatusType status,
                                        Pageable pageable);
}
