package toy.cookingstar.repository;

import org.apache.ibatis.annotations.Mapper;

import toy.cookingstar.domain.Member;

@Mapper
public interface MemberRepository {

    Member findByUserId(String userId);

    Member findByEmail(String email);

    void save(Member member);
}
