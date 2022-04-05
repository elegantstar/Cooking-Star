package toy.cookingstar.service.member;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import toy.cookingstar.entity.Member;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.utils.HashUtil;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberJpaService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member saveMember(String userId, String password, String name, String email) {

        //1. 아이디 중복 검증
        if (idAlreadyExist(userId)) {
            return null;
        }

        //2. 이메일 중복 검증
        if (emailAlreadyExist(email)) {
            return null;
        }

        //3. salt 생성
        String salt = UUID.randomUUID().toString().substring(0, 16);

        //4. hashing - sha-256
        String encryptedPwd = HashUtil.encrypt(password + salt);

        //5. member 생성
        Member member = Member.builder()
                              .userId(userId)
                              .password(encryptedPwd)
                              .name(name)
                              .email(email)
                              .nickname(name)
                              .salt(salt)
                              .build();

        //6. member 저장
        memberRepository.save(member);

        return member;
    }

    private boolean emailAlreadyExist(String email) {
        return memberRepository.findByEmail(email) != null;
    }

    private boolean idAlreadyExist(String userId) {
        return memberRepository.findByUserId(userId) != null;
    }
}
