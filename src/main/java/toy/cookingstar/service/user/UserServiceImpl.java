package toy.cookingstar.service.user;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import toy.cookingstar.domain.Member;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.utils.HashUtil;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final MemberRepository memberRepository;

    @Override
    public Member getUserInfo(String userId) {
        return memberRepository.findByUserId(userId);
    }

    @Override
    public boolean isNotAvailableEmail(String userId, String email) {
        Member foundMember = memberRepository.findByEmail(email);

        if (foundMember != null) {
            return !StringUtils.equals(foundMember.getUserId(), userId);
        }
        return false;
    }

    @Override
    @Transactional
    public Member updateInfo(UserUpdateParam userUpdateParam) {

        String userId = userUpdateParam.getUserId();

        // userId가 DB에 존재하는지 검증
        if (isNotJoinedUser(userId)) {
            return null;
        }

        // 회원 정보 업데이트
        memberRepository.updateInfo(userUpdateParam);

        return getUserInfo(userId);
    }

    @Override
    @Transactional
    public Member updatePwd(PwdUpdateParam pwdUpdateParam) {

        String userId = pwdUpdateParam.getUserId();

        // userId가 DB에 존재하는지 검증
        if (isNotJoinedUser(userId)) {
            return null;
        }

        // salt 생성
        String newSalt = UUID.randomUUID().toString().substring(0, 16);

        // hashing - sha-256
        String newPassword = HashUtil.encrypt(pwdUpdateParam.getNewPassword1() + newSalt);

        // 회원 비밀번호 업데이트
        memberRepository.updatePwd(userId, newPassword, newSalt);

        return getUserInfo(userId);
    }

    @Override
    public Member getUserInfo(Long memberId) {
        return memberRepository.findById(memberId);
    }

    private boolean isNotJoinedUser(String userId) {
        return memberRepository.findByUserId(userId) == null;
    }

}
