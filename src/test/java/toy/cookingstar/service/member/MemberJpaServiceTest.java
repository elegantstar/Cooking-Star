package toy.cookingstar.service.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.cookingstar.entity.Member;
import toy.cookingstar.repository.MemberRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberJpaServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Spy
    @InjectMocks
    MemberJpaService memberService;

    @Nested
    @DisplayName("회원 저장 테스트")
    class SaveMemberTest {

        /**
         * 아이디 중복 검증
         * 이메일 중복 검증
         * salt 생성
         * hashing
         * member 객체 생성
         * member 객체 저장
         */
        @Test
        @DisplayName("성공")
        void saveMemberSuccessTest() throws Exception {
            //given
            Member member = Member.builder()
                    .userId("test_user")
                    .password("123password!@#")
                    .name("tester")
                    .email("elegant_tester@ggmail.com")
                    .build();

            Member savedMember = mock(Member.class);
            given(savedMember.getId()).willReturn(1L);

            given(memberRepository.findByUserId(anyString())).willReturn(null);
            given(memberRepository.findByEmail(anyString())).willReturn(null);
            given(memberRepository.save(any(Member.class))).willReturn(savedMember);

            ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

            //when
            Long savedMemberId = memberService.saveMember(member.getUserId(), member.getPassword(), member.getName(), member.getEmail());

            //then
            assertNotNull(savedMemberId);
            then(memberRepository).should(times(1)).findByUserId(anyString());
            then(memberRepository).should(times(1)).findByEmail(anyString());
            then(memberRepository).should(times(1)).save(any(Member.class));
            then(memberService).should(times(1)).saveMember(anyString(), anyString(), anyString(), anyString());

            then(memberRepository).should().save(captor.capture());
            Member capturedMember = captor.getValue();

            assertEquals(member.getUserId(), capturedMember.getUserId());
            assertEquals(member.getName(), capturedMember.getName());
            assertEquals(member.getName(), capturedMember.getNickname());
            assertEquals(member.getEmail(), capturedMember.getEmail());
            assertNotEquals(member.getPassword(), capturedMember.getPassword());
            assertNotNull(capturedMember.getSalt());
        }

        @Test
        @DisplayName("실패_이미 존재하는 userId")
        void saveMemberFailureTest_userIdAlreadyExist() throws Exception {
            //given
            Member member = Member.builder()
                    .userId("test_user")
                    .password("123password!@#")
                    .name("tester")
                    .email("elegant_tester@ggmail.com")
                    .build();

            given(memberRepository.findByUserId("test_user")).willReturn(member);

            //when
            Long savedMemberId = memberService.saveMember("test_user", "", "", "");

            //then
            assertNull(savedMemberId);
            then(memberRepository).should(times(1)).findByUserId("test_user");
            then(memberRepository).should(never()).findByEmail(anyString());
            then(memberRepository).should(never()).save(any(Member.class));
        }

        @Test
        @DisplayName("실패_이미 존재하는 이메일")
        void saveMemberFailureTest_emailAlreadyExist() throws Exception {
            //given
            Member member = Member.builder()
                    .userId("test_user")
                    .password("123password!@#")
                    .name("tester")
                    .email("elegant_tester@ggmail.com")
                    .build();

            given(memberRepository.findByEmail("elegant_tester@ggmail.com")).willReturn(member);

            //when
            Long savedMemberId = memberService.saveMember("", "", "", "elegant_tester@ggmail.com");

            //then
            assertNull(savedMemberId);
            then(memberRepository).should(times(1)).findByEmail("elegant_tester@ggmail.com");
            then(memberRepository).should(times(1)).findByUserId(anyString());
            then(memberRepository).should(never()).save(any(Member.class));
        }
    }

}