package toy.cookingstar.service.login;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.cookingstar.entity.Member;
import toy.cookingstar.repository.MemberRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    LoginService loginService;

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        @Test
        @DisplayName("성공")
        void loginSuccessTest() throws Exception {
            //given
            Member member = mock(Member.class);
            given(member.getUserId()).willReturn("test_user");
            given(member.getPassword()).willReturn("34babaeacba74e9d514b57ac227c5e16d8ec189907d284beb9b1b5990757286b");
            given(member.getSalt()).willReturn("fc5a82ba-2319-4c");

            given(memberRepository.findByUserId("test_user")).willReturn(member);

            //when
            Member loginMember = loginService.login("test_user", "123password!@#");

            //then
            assertEquals(member, loginMember);
            then(memberRepository).should(times(1)).findByUserId(anyString());
            assertEquals(member.getUserId(), loginMember.getUserId());
            assertEquals(member.getPassword(), loginMember.getPassword());
        }

        @Test
        @DisplayName("실패_존재하지 않는 userId")
        void loginFailureTest_userIdDoesNotExist() throws Exception {
            //given
            given(memberRepository.findByUserId("test_user")).willReturn(null);

            //when
            Member loginMember = loginService.login("test_user", "123password!@#");

            //then
            assertNull(loginMember);
            then(memberRepository).should(times(1)).findByUserId(anyString());
        }

        @Test
        @DisplayName("실패_잘못된 비밀번호")
        void loginFailureTest_wrongPassword() throws Exception {
            //given
            Member member = mock(Member.class);
            given(member.getPassword()).willReturn("34babaeacba74e9d514b57ac227c5e16d8ec189907d284beb9b1b5990757286b");
            given(member.getSalt()).willReturn("fc5a82ba-2319-4c");

            given(memberRepository.findByUserId("test_user")).willReturn(member);

            //when
            Member loginMember = loginService.login("test_user", "123password!");

            //then
            assertNull(loginMember);
            then(memberRepository).should(times(1)).findByUserId(anyString());
        }
    }
}