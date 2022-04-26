package toy.cookingstar.service.search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import toy.cookingstar.entity.Member;
import toy.cookingstar.entity.SearchHistory;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.repository.SearchHistoryRepository;
import toy.cookingstar.service.search.dto.UserSearchDto;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SearchJpaServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    SearchHistoryRepository searchHistoryRepository;

    @InjectMocks
    SearchJpaService searchService;

    @Nested
    @DisplayName("최근 검색 기록 조회 테스트")
    class GetRecentSearchHistoryTest {

        @Test
        @DisplayName("성공")
        void getRecentSearchHistorySuccessTest() throws Exception {
            //given
            Member member = Member.builder().nickname("tester").build();

            Optional<Member> optionalMember = Optional.of(member);
            given(memberRepository.findById(1L)).willReturn(optionalMember);

            Member searchedUser = mock(Member.class);
            given(searchedUser.getUserId()).willReturn("searched_user1").willReturn("searched_user2");
            given(searchedUser.getNickname()).willReturn("searched tester1").willReturn("searched tester2");
            given(searchedUser.getProfileImage()).willReturn("target_profile_image1").willReturn("target_profile_image2");

            SearchHistory searchHistory = mock(SearchHistory.class);
            given(searchHistory.getSearchedUser()).willReturn(searchedUser);

            Page<SearchHistory> page = new PageImpl<>(Arrays.asList(searchHistory, searchHistory));

            given(searchHistoryRepository.findSearchHistoryByMember(eq(optionalMember.orElse(null)), any(Pageable.class)))
                    .willReturn(page);

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

            //when
            List<UserSearchDto> recentSearchHistory = searchService.getRecentSearchHistory(1L);

            //then
            then(memberRepository).should(times(1)).findById(1L);
            then(searchHistoryRepository).should(times(1)).findSearchHistoryByMember(any(Member.class), captor.capture());

            Pageable pageable = captor.getValue();
            assertEquals(0, pageable.getOffset());
            assertEquals(50, pageable.getPageSize());
            assertEquals(Sort.by(Sort.Order.desc("lastSearchDate")), pageable.getSort());

            assertNotNull(recentSearchHistory);
            assertEquals(2, recentSearchHistory.size());
            assertEquals("searched_user1", recentSearchHistory.get(0).getUserId());
            assertEquals("searched tester1", recentSearchHistory.get(0).getNickname());
            assertEquals("target_profile_image1", recentSearchHistory.get(0).getProfileImage());
            assertEquals("searched_user2", recentSearchHistory.get(1).getUserId());
            assertEquals("searched tester2", recentSearchHistory.get(1).getNickname());
            assertEquals("target_profile_image2", recentSearchHistory.get(1).getProfileImage());
        }

        @Test
        @DisplayName("실패_존재하지 않는 memberId")
        void getRecentSearchHistoryFailureTest() throws Exception {
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

            //then
            assertThrows(IllegalArgumentException.class,
                    () -> searchService.getRecentSearchHistory(1L));
            then(searchHistoryRepository).should(never()).findSearchHistoryByMember(any(Member.class), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("검색 기록 저장 테스트")
    class SaveHistoryTest {

        @Test
        @DisplayName("성공_검색 기록 생성 후 저장")
        void saveHistorySuccessTest_constructNewSearchHistory() throws Exception {
            //given
            Member loginMember = mock(Member.class);
            given(loginMember.getId()).willReturn(1L);

            Member searchedMember = mock(Member.class);
            given(searchedMember.getId()).willReturn(2L);

            given(memberRepository.findById(loginMember.getId())).willReturn(Optional.of(loginMember));
            given(memberRepository.findById(searchedMember.getId())).willReturn(Optional.of(searchedMember));

            given(searchHistoryRepository.findByMemberAndSearchedUser(loginMember, searchedMember)).willReturn(null);

            ArgumentCaptor<SearchHistory> captor = ArgumentCaptor.forClass(SearchHistory.class);

            //when
            searchService.saveHistory(loginMember, searchedMember);

            //then
            then(memberRepository).should(times(2)).findById(anyLong());
            then(searchHistoryRepository).should(times(1))
                    .findByMemberAndSearchedUser(any(Member.class), any(Member.class));

            then(searchHistoryRepository).should(times(1)).save(captor.capture());
            SearchHistory history = captor.getValue();
            assertEquals(loginMember, history.getMember());
            assertEquals(searchedMember, history.getSearchedUser());
        }

        @Test
        @DisplayName("성공_이미 존재하는 검색 기록 최근 검색 일자 최신화")
        void saveHistorySuccessTest_updateLastSearchDate() throws Exception {
            //given
            Member loginMember = mock(Member.class);
            given(loginMember.getId()).willReturn(1L);

            Member searchedMember = mock(Member.class);
            given(searchedMember.getId()).willReturn(2L);

            given(memberRepository.findById(loginMember.getId())).willReturn(Optional.of(loginMember));
            given(memberRepository.findById(searchedMember.getId())).willReturn(Optional.of(searchedMember));

            SearchHistory history = mock(SearchHistory.class);
            given(searchHistoryRepository.findByMemberAndSearchedUser(loginMember, searchedMember)).willReturn(history);

            //when
            searchService.saveHistory(loginMember, searchedMember);

            //then
            then(memberRepository).should(times(2)).findById(anyLong());
            then(searchHistoryRepository).should(times(1))
                    .findByMemberAndSearchedUser(any(Member.class), any(Member.class));
            then(searchHistoryRepository).should(never()).save(any(SearchHistory.class));
        }

        @Test
        @DisplayName("실패_존재하지 않는 memberId")
        void saveHistoryFailureTest_MemberDoesNotExist() throws Exception {
            //given
            Member loginMember = mock(Member.class);
            given(loginMember.getId()).willReturn(1L);
            given(memberRepository.findById(loginMember.getId())).willReturn(Optional.empty());

            Member searchedUser = mock(Member.class);

            //when
            searchService.saveHistory(loginMember, searchedUser);

            //then
            then(memberRepository).should(times(1)).findById(anyLong());
            then(searchHistoryRepository).should(never()).findByMemberAndSearchedUser(any(Member.class), any(Member.class));
            then(searchHistoryRepository).should(never()).save(any(SearchHistory.class));
        }
    }

    @Nested
    @DisplayName("검색 기록 전체 삭제 테스트")
    class ClearAllTest {

        @Test
        @DisplayName("성공")
        void clearAllSuccessTest() throws Exception {
            //given
            Member member = mock(Member.class);
            given(member.getId()).willReturn(1L);
            given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

            //when
            searchService.clearAll(member);

            //then
            then(memberRepository).should(times(1)).findById(anyLong());
            then(searchHistoryRepository).should(times(1)).deleteAllByMember(any(Member.class));
        }

        @Test
        @DisplayName("실패_존재하지 않는 회원")
        void clearAllFailureTest() throws Exception {
            //given
            Member member = mock(Member.class);
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

            //then
            assertThrows(IllegalArgumentException.class,
                    () -> searchService.clearAll(member));
            then(searchHistoryRepository).should(never()).deleteAllByMember(any(Member.class));
        }
    }

    @Test
    @DisplayName("유저 검색 테스트")
    void searchUsersTest() throws Exception {
        //given
        Member member = mock(Member.class);
        given(member.getUserId()).willReturn("tester").willReturn("com");
        given(member.getNickname()).willReturn("테스터").willReturn("computer");

        Page<Member> page = new PageImpl<>(Arrays.asList(member, member));
        given(memberRepository.findByKeyword(eq("te"), any(Pageable.class))).willReturn(page);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

        //when
        List<UserSearchDto> result = searchService.searchUsers("te");

        //then
        then(memberRepository).should(times(1)).findByKeyword(anyString(), captor.capture());
        assertEquals(0, captor.getValue().getOffset());
        assertEquals(50, captor.getValue().getPageSize());

        assertEquals(2, result.size());
        assertEquals("tester", result.get(0).getUserId());
        assertEquals("com", result.get(1).getUserId());
        assertEquals("테스터", result.get(0).getNickname());
        assertEquals("computer", result.get(1).getNickname());
    }

    @Nested
    @DisplayName("검색 기록 단건 삭제 테스트")
    class DeleteHistoryTest {

        @Test
        @DisplayName("성공")
        void deleteHistorySuccessTest() throws Exception {
            //given
            Member loginMember = mock(Member.class);
            given(loginMember.getId()).willReturn(1L);

            Member searchedMember = mock(Member.class);
            given(searchedMember.getId()).willReturn(2L);

            given(memberRepository.findById(loginMember.getId())).willReturn(Optional.of(loginMember));
            given(memberRepository.findById(searchedMember.getId())).willReturn(Optional.of(searchedMember));

            //when
            searchService.deleteHistory(loginMember, searchedMember);

            //then
            then(memberRepository).should(times(2)).findById(anyLong());
            then(searchHistoryRepository).should(times(1))
                    .deleteByMemberAndSearchedUser(any(Member.class), any(Member.class));
        }

        @Test
        void deleteHistoryFailureTest() throws Exception {
            //given
            Member loginMember = mock(Member.class);
            given(loginMember.getId()).willReturn(1L);
            given(memberRepository.findById(loginMember.getId())).willReturn(Optional.empty());

            Member searchedUser = mock(Member.class);

            //when
            searchService.deleteHistory(loginMember, searchedUser);

            //then
            then(memberRepository).should(times(1)).findById(anyLong());
            then(searchHistoryRepository).should(never())
                    .deleteByMemberAndSearchedUser(any(Member.class), any(Member.class));
        }
    }
}