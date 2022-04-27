package toy.cookingstar.mybatisservice.user;

import toy.cookingstar.domain.Member;

public interface UserService {

    Member getUserInfo(String userId);

    boolean isNotAvailableEmail(String userId, String email);

    Member updateInfo(UserUpdateParam userUpdateParam);

    Member updatePwd(PwdUpdateParam pwdUpdateParam);

    Member getUserInfo(Long memberId);

    void deleteProfileImg(Long id);

    void updateProfileImg(Long memberId, String storedProfileImage);
}
