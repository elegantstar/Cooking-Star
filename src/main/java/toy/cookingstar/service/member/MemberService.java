package toy.cookingstar.service.member;

import toy.cookingstar.domain.Member;

public interface MemberService {

    Member saveMember(String userId, String password, String name, String email);
}
