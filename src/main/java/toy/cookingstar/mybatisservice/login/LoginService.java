package toy.cookingstar.mybatisservice.login;

import toy.cookingstar.domain.Member;

public interface LoginService {

    Member login(String userId, String password);
}
