package toy.cookingstar.mybatisservice.search;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import toy.cookingstar.domain.Member;
import toy.cookingstar.domain.SearchHistory;
import toy.cookingstar.mapper.MemberMapper;
import toy.cookingstar.mapper.SearchHistoryMapper;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {

    private final MemberMapper memberMapper;
    private final SearchHistoryMapper searchHistoryMapper;

    @Override
    public List<Member> getRecentSearchHistory(Long memberId) {

        if (memberMapper.findById(memberId) == null) {
            return null;
        }

        return memberMapper.findSearchHistoryById(memberId);
    }

    @Override
    @Transactional
    public void saveHistory(Member loginMember, String searchedUserId) {

        if (memberMapper.findById(loginMember.getId()) == null) {
            return;
        }

        //userId 검색 기록이 이미 존재하는 경우에는 lastSearchedDate 최신화
        SearchHistory history = searchHistoryMapper.findHistory(loginMember.getId(), searchedUserId);
        if (history != null) {
            searchHistoryMapper.updateLastSearchDate(loginMember.getId(), searchedUserId);
            return;
        }

        //userId 검색 기록이 존재하지 않는 경우에는 새로운 기록 저장
        SearchHistory searchHistory = SearchHistory.builder()
                                                   .memberId(loginMember.getId())
                                                   .searchedUserId(searchedUserId)
                                                   .build();
        searchHistoryMapper.save(searchHistory);
    }

    @Override
    @Transactional
    public void clearAll(Long memberId) {

        if (memberMapper.findById(memberId) == null) {
            return;
        }
        searchHistoryMapper.clearAll(memberId);
    }

    @Override
    public List<Member> searchUsers(String keyword) {

        return memberMapper.findByKeyword(keyword);
    }

    @Override
    @Transactional
    public void deleteHistory(Long memberId, String userId) {
        if (memberMapper.findById(memberId) == null) {
            return;
        }

        searchHistoryMapper.deleteHistory(memberId, userId);
    }
}
