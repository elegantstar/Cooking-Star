package toy.cookingstar.service.user;

import toy.cookingstar.domain.Member;
import toy.cookingstar.domain.dto.UserPageDto;

public interface UserService {

    UserPageDto getUserPageInfo(String userId);

    UserPageDto getMyPageInfo(String userId);
}
