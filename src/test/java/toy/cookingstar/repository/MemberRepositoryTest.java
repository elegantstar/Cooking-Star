package toy.cookingstar.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import toy.cookingstar.entity.Member;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    @DisplayName("회원 저장 테스트")
    void saveMemberTest() {
        //given
        Member member = Member.builder()
                .userId("eleganttest")
                .password("12ABCdefgh!@")
                .name("테스터")
                .email("elegant_test@naver.com")
                .salt("tteesstteerr")
                .nickname("test man")
                .build();

        //when
        Member savedMember = memberRepository.save(member);

        //then
        assertEquals(savedMember.getUserId(), member.getUserId());
        assertEquals(savedMember.getPassword(), member.getPassword());
        assertEquals(savedMember.getName(), member.getName());
        assertEquals(savedMember.getEmail(), member.getEmail());
        assertEquals(savedMember.getSalt(), member.getSalt());
        assertEquals(savedMember.getNickname(), member.getNickname());
    }

    @Nested
    @DisplayName("Id로 회원 조회 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("성공")
        void findByIdSuccessTest() throws Exception {
            //given
            Member member = Member.builder()
                    .userId("eleganttest")
                    .email("elegant_test@naver.com")
                    .build();

            Long savedMemberId = memberRepository.save(member).getId();
            em.flush();
            em.clear();

            //when
            Optional<Member> foundMember = memberRepository.findById(savedMemberId);

            //then
            foundMember.ifPresent(value -> assertEquals(savedMemberId, value.getId()));
        }

        @Test
        @DisplayName("실패")
        void findByIdFailureTest() throws Exception {
            //when
            Optional<Member> foundMember = memberRepository.findById(-1L);
            //then
            assertEquals(Optional.empty(), foundMember);
        }
    }

    @Nested
    @DisplayName("Email로 회원 조회 테스트")
    class FindByEmailTest {

        @Test
        @DisplayName("성공")
        void findByEmailSuccessTest() throws Exception {
            //given
            Member member = Member.builder()
                    .userId("eleganttest")
                    .email("elegant_test@naver.com")
                    .build();
            //when
            Member savedMember = memberRepository.save(member);
            Member foundMember = memberRepository.findByEmail(member.getEmail());

            //then
            assertEquals(foundMember.getEmail(), savedMember.getEmail());
            assertEquals(foundMember.getId(), savedMember.getId());
            assertEquals(foundMember.getUserId(), savedMember.getUserId());
        }

        @Test
        @DisplayName("실패")
        void findByEmailFailureTest() throws Exception {
            //when
            Member foundMember = memberRepository.findByEmail("not exist email");
            //then
            assertNull(foundMember);
        }
    }

    @Nested
    @DisplayName("UserId로 회원 조회 테스트")
    class FindByUserIdTest {

        @Test
        @DisplayName("성공")
        void findByUserIdSuccessTest() {
            //given
            Member member = Member.builder()
                    .userId("eleganttest")
                    .email("elegant_test@naver.com")
                    .build();

            //when
            Member savedMember = memberRepository.save(member);

            //then
            assertEquals(savedMember.getUserId(), member.getUserId());
            assertEquals(savedMember.getEmail(), member.getEmail());
        }

        @Test
        @DisplayName("실패")
        void findByUserIdFailureTest() throws Exception {
            //when
            Member foundMember = memberRepository.findByUserId("not exist userId");
            //then
            assertNull(foundMember);
        }
    }

    @Nested
    @DisplayName("키워드로 유저 검색 테스트")
    class FindByKeywordTest {

        @Test
        @DisplayName("성공")
        void findByKeywordSuccessTest() throws Exception {
            //given
            Member member1 = Member.builder()
                    .userId("eleganttest")
                    .nickname("repositorytest123")
                    .build();

            Member member2 = Member.builder()
                    .userId("keywordtester")
                    .nickname("testosterone")
                    .build();

            Member member3 = Member.builder()
                    .userId("1234567")
                    .nickname("7654321")
                    .build();

            memberRepository.save(member1);
            memberRepository.save(member2);
            memberRepository.save(member3);

            String keyword = "test";
            Pageable limitTen = PageRequest.of(0, 10);

            //when
            List<Member> foundMembers = memberRepository.findByKeyword(keyword, limitTen).getContent();

            //then
            assertEquals(foundMembers.size(), 2);
            assertTrue(foundMembers.contains(member1));
            assertTrue(foundMembers.contains(member2));
        }

        @Test
        @DisplayName("실패")
        void findByKeywordFailureTest() throws Exception {
            //given
            Member member1 = Member.builder()
                    .userId("eleganttest")
                    .nickname("repositorytest123")
                    .build();

            Member member2 = Member.builder()
                    .userId("keywordtester")
                    .nickname("testosterone")
                    .build();

            Member member3 = Member.builder()
                    .userId("1234567")
                    .nickname("7654321")
                    .build();

            memberRepository.save(member1);
            memberRepository.save(member2);
            memberRepository.save(member3);

            String keyword = "abcd";
            Pageable limitTen = PageRequest.of(0, 10);

            //when
            List<Member> foundMembers = memberRepository.findByKeyword(keyword, limitTen).getContent();

            //then
            assertEquals(foundMembers.size(), 0);
        }
    }

}