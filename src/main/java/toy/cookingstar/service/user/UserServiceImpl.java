package toy.cookingstar.service.user;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import toy.cookingstar.domain.Member;
import toy.cookingstar.domain.dto.UserPageDto;
import toy.cookingstar.repository.UserPageRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserPageRepository userPageRepository;

    @Override
    public UserPageDto getUserPageInfo(String userId) {

        // 존재하는 userId인지 확인
        if (isNull(userId)) {
            return null;
        }

        return userPageRepository.findUserPageInfoByUserId(userId);

    }

    private boolean isNull(String userId) {
        return !userPageRepository.findUserByUserId(userId);
    }

    @Override
    public UserPageDto getMyPageInfo(String userId) {
        return userPageRepository.findUserPageInfoByUserId(userId);
    }
}
