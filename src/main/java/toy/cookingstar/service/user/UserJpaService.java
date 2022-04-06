package toy.cookingstar.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.cookingstar.entity.Member;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.service.user.dto.PwdUpdateDto;
import toy.cookingstar.service.user.dto.UserInfoDto;
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
    public UserInfoDto getUserInfo(String userId) {
        Member user = memberRepository.findByUserId(userId);
        if (user == null) {
            return null;
        }
        return UserInfoDto.of(user);
    }

    /**
     * memberId로 유저 정보 조회
     * @return UserInfoDto = [ id, userId, name, email, nickname, introduction, gender ]
     */
    public UserInfoDto getUserInfo(Long memberId) {
        Member user = memberRepository.findById(memberId).orElse(null);
        if (user == null) {
            return null;
        }
        return UserInfoDto.of(user);
    }

    /**
     * 이메일로 유저 조회
     */
    public Member getUserInfoByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    /**
     * 유저 정보 수정(Controller 수정 필요)
     */
    @Transactional
    public void updateInfo(UserInfoDto userInfoDto) {
        Member foundUser = memberRepository.findById(userInfoDto.getId()).orElseThrow(IllegalArgumentException::new);
        foundUser.updateInfo(userInfoDto);
    }

    /**
     * 유저 비밀번호 변경(Controller 수정 필요)
     */
    @Transactional
    public void updatePwd(PwdUpdateDto pwdUpdateDto) {
        Member foundUser = memberRepository.findByUserId(pwdUpdateDto.getUserId());
        if (foundUser == null) {
            return;
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
    public void deleteProfileImg(Long id) {
        Member user = memberRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        if (user == null) {
            return;
        }
        user.deleteProfileImage();
    }

    /**
     * 유저 프로필 이미지 변경
     */
    @Transactional
    public void updateProfileImg(Long id, String storedProfileImage) {
        Member user = memberRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        if (user == null) {
            return;
        }
        user.updateProfileImage(storedProfileImage);
    }

}
