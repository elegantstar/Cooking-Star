package toy.cookingstar.service.search;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import toy.cookingstar.domain.Member;
import toy.cookingstar.domain.SearchHistory;
import toy.cookingstar.mapper.MemberRepository;
import toy.cookingstar.mapper.SearchHistoryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {

    private final MemberRepository memberRepository;
    private final SearchHistoryRepository searchHistoryRepository;

    @Override
    public List<Member> getRecentSearchHistory(Long memberId) {

        if (memberRepository.findById(memberId) == null) {
            return null;
        }

        return memberRepository.findSearchHistoryById(memberId);
    }

    @Override
    @Transactional
    public void saveHistory(Member loginMember, String searchedUserId) {

        if (memberRepository.findById(loginMember.getId()) == null) {
            return;
        }

        //userId 검색 기록이 이미 존재하는 경우에는 lastSearchedDate 최신화
        SearchHistory history = searchHistoryRepository.findHistory(loginMember.getId(), searchedUserId);
        if (history != null) {
            searchHistoryRepository.updateLastSearchDate(loginMember.getId(), searchedUserId);
            return;
        }

        //userId 검색 기록이 존재하지 않는 경우에는 새로운 기록 저장
        SearchHistory searchHistory = SearchHistory.builder()
                                                   .memberId(loginMember.getId())
                                                   .searchedUserId(searchedUserId)
                                                   .build();
        searchHistoryRepository.save(searchHistory);
    }

    @Override
    @Transactional
    public void clearAll(Long memberId) {

        if (memberRepository.findById(memberId) == null) {
            return;
        }
        searchHistoryRepository.clearAll(memberId);
    }

    @Override
    public List<Member> searchUsers(String keyword) {

        return memberRepository.findByKeyword(keyword);
    }

    @Override
    @Transactional
    public void deleteHistory(Long memberId, String userId) {
        if (memberRepository.findById(memberId) == null) {
            return;
        }

        searchHistoryRepository.deleteHistory(memberId, userId);
    }
}
