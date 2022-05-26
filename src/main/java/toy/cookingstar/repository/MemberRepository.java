package toy.cookingstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.cookingstar.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);

    Member findByUserId(String userId);

    @Query("select m from Member m where m.userId like concat('%', :keyword,'%') or m.nickname like concat('%', :keyword, '%')")
    Page<Member> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "select m.*, match(m.user_id) against(:keyword in boolean mode) as score" +
            " from Member m where match(m.user_id) against(:keyword in boolean mode) order by score desc", nativeQuery = true)
    List<Member> findByFullTextSearch(@Param("keyword") String keyword, Pageable pageable);
}
