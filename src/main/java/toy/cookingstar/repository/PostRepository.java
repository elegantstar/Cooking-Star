package toy.cookingstar.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.cookingstar.entity.Post;
import toy.cookingstar.service.post.StatusType;

public interface PostRepository extends JpaRepository<Post, Long> {

    //JAP 표준 스펙에서 fetch join 대상에는 alias를 줄 수 없음(Hibernate는 허용)
    //fetch join 대상은 where에 포함되어서는 안 됨. 조회 결과가 DB와 동일한 일관성을 유지하지 못 하기 때문.
    @Query("select p from Post p join fetch p.postImages where p.member.id = :memberId and p.status = :status")
    Slice<Post> findPosts(@Param("memberId") Long memberId, @Param("status") StatusType status, Pageable pageable);

    int countByMemberId(Long memberId);

    @Query("select p from Post p join fetch p.postImages where p.member.id = :memberId and p.status = :status and p.id < :postId")
    Slice<Post> findPostsByLastReadPostId(@Param("memberId") Long memberId, @Param("postId") Long lastReadPostId,
                                          Pageable pageable, @Param("status") StatusType statusType);
}
