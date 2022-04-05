package toy.cookingstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import toy.cookingstar.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);

    Member findByUserId(String userId);
}
