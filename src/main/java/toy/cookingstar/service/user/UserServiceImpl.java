package toy.cookingstar.service.user;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import toy.cookingstar.domain.Member;
import toy.cookingstar.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final MemberRepository memberRepository;

    @Override
    public Member getUserInfo(String userId) {

        return memberRepository.findByUserId(userId);

    }

}
