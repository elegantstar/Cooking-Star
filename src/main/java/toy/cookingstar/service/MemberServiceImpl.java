package toy.cookingstar.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import toy.cookingstar.domain.Member;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.utils.HashUtil;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public Member saveMember(String userId, String password, String name, String email) {
        //1. 아이디 중복 검증
        Member foundUserById = memberRepository.findByUserId(userId);
        if (foundUserById != null) {
            return null;
        }

        //2. 이메일 중복 검증
        Member foundUserByEmail = memberRepository.findByEmail(email);
        if (foundUserByEmail != null) {
            return null;
        }

        //3. salt 생성
        String uuid = UUID.randomUUID().toString();
        String salt = uuid.substring(0, 16);

        //4. hashing - sha-256
        String encryptPw = HashUtil.encrypt(password + salt);

        //5. member 생성
        Member member = Member.builder()
                              .userId(userId)
                              .password(encryptPw)
                              .name(name)
                              .email(email)
                              .nickname(name)
                              .salt(salt)
                              .build();

        //6. member 저장
        memberRepository.save(member);

        return member;
    }

}
