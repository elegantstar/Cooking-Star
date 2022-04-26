package toy.cookingstar.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import toy.cookingstar.entity.Member;
import toy.cookingstar.entity.SearchHistory;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class SearchHistoryRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    SearchHistoryRepository searchHistoryRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("검색 기록 저장 테스트")
    void saveSearchHistoryTest() throws Exception {
        //given
        Member member1 = Member.builder().build();
        Member member2 = Member.builder().build();

        Member user = memberRepository.save(member1);
        Member searchedUser = memberRepository.save(member2);

        SearchHistory history = new SearchHistory(user, searchedUser);

        //when
        SearchHistory savedSearchHistory = searchHistoryRepository.save(history);

        //then
        assertEquals(history.getMember(), savedSearchHistory.getMember());
        assertEquals(history.getSearchedUser(), savedSearchHistory.getSearchedUser());
        assertNotNull(savedSearchHistory.getId());
    }

    @Nested
    @DisplayName("회원으로 검색 기록 조회 테스트")
    class FindSearchHistoryByMemberTest {

        @Test
        @DisplayName("성공")
        void findSearchHistoryByMemberSuccessTest() throws Exception {
            //given
            Member member1 = Member.builder().build();
            Member member2 = Member.builder().build();
            Member member3 = Member.builder().build();

            Member user = memberRepository.save(member1);
            Member searchedUser1 = memberRepository.save(member2);
            Member searchedUser2 = memberRepository.save(member3);

            searchHistoryRepository.save(new SearchHistory(user, searchedUser1));
            searchHistoryRepository.save(new SearchHistory(user, searchedUser2));

            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("id")));
            //when
            Page<SearchHistory> searchHistory = searchHistoryRepository.findSearchHistoryByMember(user, pageable);

            //then
            assertEquals(2, searchHistory.getNumberOfElements());
            assertEquals(searchedUser2, searchHistory.getContent().get(0).getSearchedUser());
            assertEquals(searchedUser1, searchHistory.getContent().get(1).getSearchedUser());
        }

        @Test
        @DisplayName("실패")
        void findSearchHistoryByMemberFailureTest() throws Exception {
            //given
            Member member = Member.builder().build();
            Member savedMember = memberRepository.save(member);
            Pageable pageable = PageRequest.of(0, 10);
            //when
            Page<SearchHistory> history = searchHistoryRepository.findSearchHistoryByMember(savedMember, pageable);
            //then
            assertEquals(0, history.getContent().size());
        }
    }

    @Nested
    @DisplayName("회원 및 검색된 유저로 검색 기록 조회 테스트")
    class FindByMemberAndSearchedUserTest {

        @Test
        @DisplayName("성공")
        void findByMemberAndSearchedUserSuccessTest() throws Exception {
            //given
            Member member1 = Member.builder().build();
            Member member2 = Member.builder().build();

            Member user = memberRepository.save(member1);
            Member searchedUser = memberRepository.save(member2);

            SearchHistory savedHistory = searchHistoryRepository.save(new SearchHistory(user, searchedUser));

            em.flush();
            em.clear();

            //when
            SearchHistory foundHistory = searchHistoryRepository.findByMemberAndSearchedUser(user, searchedUser);

            //then
            assertEquals(savedHistory.getId(), foundHistory.getId());
            assertEquals(savedHistory.getMember().getId(), foundHistory.getMember().getId());
            assertEquals(savedHistory.getSearchedUser().getId(), foundHistory.getSearchedUser().getId());
        }

        @Test
        @DisplayName("실패")
        void findByMemberAndSearchedUserFailureTest() throws Exception {
            //given
            Member member = Member.builder().build();
            Member searchedUser = Member.builder().build();

            memberRepository.save(member);
            memberRepository.save(searchedUser);

            //when
            SearchHistory searchHistory = searchHistoryRepository.findByMemberAndSearchedUser(member, searchedUser);

            //then
            assertNull(searchHistory);
        }
    }

    @Test
    @DisplayName("회원 검색 기록 전체 삭제 테스트")
    void deletedAllByMemberTest() throws Exception {
        //given
        Member member1 = Member.builder().build();
        Member member2 = Member.builder().build();
        Member member3 = Member.builder().build();

        Member user = memberRepository.save(member1);
        Member searchedUser1 = memberRepository.save(member2);
        Member searchedUser2 = memberRepository.save(member3);

        searchHistoryRepository.save(new SearchHistory(user, searchedUser1));
        searchHistoryRepository.save(new SearchHistory(user, searchedUser2));

        //when
        searchHistoryRepository.deleteAllByMember(user);

        Pageable pageable = PageRequest.of(0, 10);
        Page<SearchHistory> history = searchHistoryRepository.findSearchHistoryByMember(user, pageable);

        //then
        assertEquals(0, history.getNumberOfElements());
    }

    @Test
    @DisplayName("회원 및 검색된 유저로 검색 기록 단건 삭제 테스트")
    void deleteByMemberAndSearchedUserTest() throws Exception {
        //given
        Member member1 = Member.builder().build();
        Member member2 = Member.builder().build();

        Member member = memberRepository.save(member1);
        Member searchedUser = memberRepository.save(member2);

        SearchHistory savedHistory = searchHistoryRepository.save(new SearchHistory(member, searchedUser));

        //when
        searchHistoryRepository.deleteByMemberAndSearchedUser(member, searchedUser);

        SearchHistory searchHistory = searchHistoryRepository.findByMemberAndSearchedUser(member, searchedUser);

        //then
        assertNull(searchHistory);
    }

    @Test
    @DisplayName("최근 검색 기록 갱신 테스트")
    void updateLastSearchDateTest() throws Exception {
        //given
        Member member1 = Member.builder().build();
        Member member2 = Member.builder().build();

        Member member = memberRepository.save(member1);
        Member searchedUser = memberRepository.save(member2);

        SearchHistory savedHistory = searchHistoryRepository.save(new SearchHistory(member, searchedUser));

        //when
        savedHistory.updateLastSearchDate();

        em.flush();
        em.clear();

        SearchHistory foundHistory = searchHistoryRepository.findByMemberAndSearchedUser(member, searchedUser);

        //then
        assertNotEquals(savedHistory.getLastSearchDate(), foundHistory.getLastSearchDate());
    }

}