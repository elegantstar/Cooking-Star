package toy.cookingstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.cookingstar.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);

    Member findByUserId(String userId);

    @Query("select m from Member m" +
            " where m.userId like concat('%', :keyword,'%') or m.nickname like concat('%', :keyword, '%')")
    Page<Member> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
