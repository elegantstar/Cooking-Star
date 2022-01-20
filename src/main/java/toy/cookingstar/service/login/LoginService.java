package toy.cookingstar.service.login;

import toy.cookingstar.domain.Member;

public interface LoginService {

    public Member login(String userId, String password);
}
