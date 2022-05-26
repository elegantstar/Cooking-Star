package toy.cookingstar.mybatisservice.user;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import toy.cookingstar.domain.Member;
import toy.cookingstar.mapper.MemberMapper;
import toy.cookingstar.utils.HashUtil;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final MemberMapper memberMapper;

    @Override
    public Member getUserInfo(String userId) {
        return memberMapper.findByUserId(userId);
    }

    @Override
    public boolean isNotAvailableEmail(String userId, String email) {
        Member foundMember = memberMapper.findByEmail(email);

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
        memberMapper.updateInfo(userUpdateParam);

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
        memberMapper.updatePwd(userId, newPassword, newSalt);

        return getUserInfo(userId);
    }

    @Override
    public Member getUserInfo(Long memberId) {
        return memberMapper.findById(memberId);
    }

    @Override
    @Transactional
    public void deleteProfileImg(Long id) {
        if (memberMapper.findById(id) == null) {
            return;
        }
        memberMapper.deleteProfileImage(id);
    }

    @Override
    @Transactional
    public void updateProfileImg(Long memberId, String storedProfileImage) {

        if (memberMapper.findById(memberId) == null) {
            return;
        }

        memberMapper.updateProfileImage(memberId, storedProfileImage);

    }

    private boolean isNotJoinedUser(String userId) {
        return memberMapper.findByUserId(userId) == null;
    }

}
