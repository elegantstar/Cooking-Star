package toy.cookingstar.service.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.cookingstar.entity.Member;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.service.user.dto.PwdUpdateDto;
import toy.cookingstar.service.user.dto.UserInfoDto;
import toy.cookingstar.service.user.dto.UserInfoUpdateDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserJpaServiceTest {

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    UserJpaService userService;

    @Nested
    @DisplayName("UserId로 유저 정보 조회 테스트")
    class GetUserInfoByUserIdTest {

        @Test
        void getUserInfoByUserIdSuccessTest() throws Exception {
            //given
            Member user = mock(Member.class);
            given(user.getId()).willReturn(1L);
            given(user.getUserId()).willReturn("test_user");
            given(user.getName()).willReturn("tester");
            given(user.getEmail()).willReturn("elegant_tester@ggmail.com");
            given(user.getNickname()).willReturn("테스터");
            given(user.getIntroduction()).willReturn("테스트 계정 자기 소개");
            given(user.getGender()).willReturn("MALE");
            given(memberRepository.findByUserId("test_user")).willReturn(user);

            //when
            Member foundUser = userService.getUserInfoByUserId("test_user");

            //then
            then(memberRepository).should(times(1)).findByUserId(anyString());
            assertEquals(user.getId(), foundUser.getId());
            assertEquals(user.getUserId(), foundUser.getUserId());
            assertEquals(user.getName(), foundUser.getName());
            assertEquals(user.getEmail(), foundUser.getEmail());
            assertEquals(user.getNickname(), foundUser.getNickname());
            assertEquals(user.getIntroduction(), foundUser.getIntroduction());
            assertEquals(user.getGender(), foundUser.getGender());
        }

        @Test
        @DisplayName("실패_존재하지 않는 userId")
        void getUserInfoByUserIdFailureTest() throws Exception {
            //given
            given(memberRepository.findByUserId(anyString())).willReturn(null);

            //when & then
            assertThrows(IllegalArgumentException.class, () -> userService.getUserInfoByUserId("test_user"));
            then(memberRepository).should(times(1)).findByUserId(anyString());
        }
    }

    @Nested
    @DisplayName("memberId로 유저 정보 조회 테스트")
    class GetUserInfoByIdTest {

        @Test
        @DisplayName("성공")
        void getUserInfoByIdTest() throws Exception {
            //given
            Member user = mock(Member.class);
            given(user.getId()).willReturn(1L);
            given(user.getUserId()).willReturn("test_user");
            given(user.getName()).willReturn("tester");
            given(user.getEmail()).willReturn("elegant_tester@ggmail.com");
            given(user.getNickname()).willReturn("테스터");
            given(user.getIntroduction()).willReturn("테스트 계정 자기 소개");
            given(user.getGender()).willReturn("MALE");

            Optional<Member> optionalUser = Optional.of(user);
            given(memberRepository.findById(1L)).willReturn(optionalUser);

            //when
            Member foundUser = userService.getUserInfoById(1L);

            //then
            then(memberRepository).should(times(1)).findById(anyLong());
            assertEquals(user.getId(), foundUser.getId());
            assertEquals(user.getUserId(), foundUser.getUserId());
            assertEquals(user.getName(), foundUser.getName());
            assertEquals(user.getEmail(), foundUser.getEmail());
            assertEquals(user.getNickname(), foundUser.getNickname());
            assertEquals(user.getIntroduction(), foundUser.getIntroduction());
            assertEquals(user.getGender(), foundUser.getGender());
        }

        @Test
        @DisplayName("실패_존재하지 않는 memberId")
        void getUserInfoByIdFailureTest() throws Exception {
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

            //then
            assertThrows(IllegalArgumentException.class,
                    () -> userService.getUserInfoById(1L));
        }
    }

    @Nested
    @DisplayName("Email로 유저 조회")
    class GetUserInfoByEmailTest {

        @Test
        @DisplayName("성공")
        void getUserInfoByEmailSuccessTest() throws Exception {
            //given
            Member member = mock(Member.class);
            given(memberRepository.findByEmail("elegant_tester@ggmail.com")).willReturn(member);
            //when
            Member foundMember = userService.getUserInfoByEmail("elegant_tester@ggmail.com");
            //then
            then(memberRepository).should(times(1)).findByEmail(anyString());
            assertSame(member, foundMember);
        }

        @Test
        @DisplayName("실패_존재하지 않는 email")
        void getUserInfoByEmailFailureTest() throws Exception {
            //given
            given(memberRepository.findByEmail(anyString())).willReturn(null);

            //when & then
            assertThrows(IllegalArgumentException.class,
                    () -> userService.getUserInfoByEmail("elegant_tester@ggmail.com"));
            then(memberRepository).should(times(1)).findByEmail(anyString());
        }
    }

    @Nested
    @DisplayName("유저 정보 수정 테스트")
    class UpdateInfoTest {

        @Test
        @DisplayName("성공")
        void updateInfoSuccessTest() throws Exception {
            //given
            UserInfoUpdateDto dto = mock(UserInfoUpdateDto.class);
            given(dto.getId()).willReturn(1L);
            given(dto.getNickname()).willReturn("테스터");
            given(dto.getIntroduction()).willReturn("테스트 계정입니다.");
            given(dto.getEmail()).willReturn("elegant_tester@ggmail.com");
            given(dto.getGender()).willReturn("MALE");

            Member user = mock(Member.class);
            Optional<Member> optionalUser = Optional.of(user);
            given(memberRepository.findById(dto.getId())).willReturn(optionalUser);

            //when
            userService.updateInfo(dto);

            //then
            then(memberRepository).should(times(1)).findById(1L);
            then(optionalUser.orElse(null)).should(times(1))
                    .updateInfo(anyString(), anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("실패_존재하지 않는 Id")
        void updateInfoFailureTest() throws Exception {
            //given
            UserInfoUpdateDto dto = mock(UserInfoUpdateDto.class);
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

            //then
            assertThrows(IllegalArgumentException.class, () -> userService.updateInfo(dto));
        }
    }

    @Nested
    @DisplayName("유저 비밀번호 변경 테스트")
    class UpdatePwdTest {

        @Test
        @DisplayName("성공")
        void updatePwdSuccessTest() throws Exception {
            //given
            Member member = mock(Member.class);
            given(member.getPassword()).willReturn("34babaeacba74e9d514b57ac227c5e16d8ec189907d284beb9b1b5990757286b");
            given(member.getSalt()).willReturn("fc5a82ba-2319-4c");

            PwdUpdateDto dto = mock(PwdUpdateDto.class);
            given(dto.getNewPassword1()).willReturn("password123$%^");

            given(memberRepository.findByUserId(dto.getUserId())).willReturn(member);

            ArgumentCaptor<String> pwdCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> saltCaptor = ArgumentCaptor.forClass(String.class);

            //when
            userService.updatePwd(dto);

            //then
            then(member).should(times(1)).updatePwd(pwdCaptor.capture(), saltCaptor.capture());
            assertNotEquals(member.getPassword(), pwdCaptor.getValue());
            assertNotEquals(member.getSalt(), saltCaptor.getValue());
        }

        @Test
        @DisplayName("실패_존재하지 않는 userId")
        void updatePwdFailureTest() throws Exception {
            //given
            Member member = mock(Member.class);
            PwdUpdateDto dto = mock(PwdUpdateDto.class);
            given(dto.getUserId()).willReturn("test_user");
            given(memberRepository.findByUserId(anyString())).willReturn(null);

            //when & then
            assertThrows(IllegalArgumentException.class, () -> userService.updatePwd(dto));
            then(memberRepository).should(times(1)).findByUserId(dto.getUserId());
            then(member).should(never()).updatePwd(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("유저 프로필 이미지 삭제 테스트")
    class DeleteProfileImgTest {

        @Test
        @DisplayName("성공")
        void deleteProfileImgSuccessTest() throws Exception {
            //given
            Member member = mock(Member.class);
            Optional<Member> optionalMember = Optional.of(member);
            given(memberRepository.findById(1L)).willReturn(optionalMember);

            //when
            userService.deleteProfileImg(1L);

            //then
            then(memberRepository).should(times(1)).findById(1L);
            then(optionalMember.orElse(null)).should(times(1)).deleteProfileImage();
        }
        
        @Test
        @DisplayName("실패_존재하지 않는 Id")
        void deleteProfileImgFailureTest() throws Exception {
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

            //then
            assertThrows(IllegalArgumentException.class,
                    () -> userService.deleteProfileImg(1L));
        }
    }

    @Nested
    @DisplayName("유저 프로필 이미지 변경 테스트")
    class UpdateProfileImgTest {

        @Test
        @DisplayName("성공")
        void updateProfileImgSuccessTest() throws Exception {
            //given
            Member member = mock(Member.class);
            Optional<Member> optionalMember = Optional.of(member);
            given(memberRepository.findById(1L)).willReturn(optionalMember);

            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

            //when
            userService.updateProfileImg(1L, "updated profile image");

            //then
            then(memberRepository).should(times(1)).findById(1L);
            then(optionalMember.orElse(null)).should(times(1))
                    .updateProfileImage(captor.capture());
            assertEquals("updated profile image", captor.getValue());
        }

        @Test
        @DisplayName("실패_존재하지 않는 Id")
        void updateProfileImgFailureTest() throws Exception {
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

            //then
            assertThrows(IllegalArgumentException.class,
                    () -> userService.updateProfileImg(1L, "profile image"));

        }
    }
}