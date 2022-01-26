package toy.cookingstar.service.user;

import toy.cookingstar.domain.Member;

public interface UserService {

    Member getUserInfo(String userId);

    boolean isNotAvailableEmail(String userId, String email);

    Member updateInfo(UserUpdateParam userUpdateParam);

    Member updatePwd(PwdUpdateParam pwdUpdateParam);

}
