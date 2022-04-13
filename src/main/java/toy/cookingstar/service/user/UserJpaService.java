package toy.cookingstar.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.cookingstar.entity.Member;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.service.user.dto.PwdUpdateDto;
import toy.cookingstar.service.user.dto.UserInfoUpdateDto;
import toy.cookingstar.utils.HashUtil;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserJpaService {

    private final MemberRepository memberRepository;

    /**
     * 유저ID로 유저 정보 조회
     * @return UserInfoDto = [ id, userId, name, email, nickname, introduction, gender ]
     */
    public Member getUserInfoByUserId(String userId) throws IllegalArgumentException {
        Member user = memberRepository.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException();
        }
        return user;
    }

    /**
     * memberId로 유저 정보 조회
     * @return UserInfoDto = [ id, userId, name, email, nickname, introduction, gender ]
     */
    public Member getUserInfoById(Long memberId) {
        Member user = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
        if (user == null) {
            throw new IllegalArgumentException();
        }
        return user;
    }

    /**
     * 이메일로 유저 조회
     */
    public Member getUserInfoByEmail(String email) throws IllegalArgumentException {
        Member user = memberRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException();
        }
        return user;
    }

    /**
     * 유저 정보 수정(Controller 수정 필요)
     */
    @Transactional
    public void updateInfo(UserInfoUpdateDto userInfoUpdateDto) {
        Member foundUser = memberRepository.findById(userInfoUpdateDto.getId()).orElseThrow(IllegalArgumentException::new);
        foundUser.updateInfo(userInfoUpdateDto.getEmail(), userInfoUpdateDto.getNickname(),
                userInfoUpdateDto.getIntroduction(), userInfoUpdateDto.getGender());
    }

    /**
     * 유저 비밀번호 변경(Controller 수정 필요)
     */
    @Transactional
    public void updatePwd(PwdUpdateDto pwdUpdateDto) throws IllegalArgumentException {
        Member foundUser = memberRepository.findByUserId(pwdUpdateDto.getUserId());
        if (foundUser == null) {
            throw new IllegalArgumentException();
        }

        // 새로운 salt 생성
        String newSalt = UUID.randomUUID().toString().substring(0, 16);

        // hashing - using sha-256
        String newPassword = HashUtil.encrypt(pwdUpdateDto.getNewPassword1() + newSalt);

        // 회원 비밀번호 업데이트
        foundUser.updatePwd(newPassword, newSalt);
    }

    //TODO: 공통 로직 private method로 추출 고려.
    /**
     * 유저 프로필 이미지 삭제
     */
    @Transactional
    public void deleteProfileImg(Long id) throws IllegalArgumentException {
        Member user = memberRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        if (user == null) {
            throw new IllegalArgumentException();
        }
        user.deleteProfileImage();
    }

    /**
     * 유저 프로필 이미지 변경
     */
    @Transactional
    public void updateProfileImg(Long id, String storedProfileImage) {
        Member user = memberRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        user.updateProfileImage(storedProfileImage);
    }

}
