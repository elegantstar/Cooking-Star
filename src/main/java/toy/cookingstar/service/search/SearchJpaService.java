package toy.cookingstar.service.search;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.cookingstar.entity.Member;
import toy.cookingstar.entity.SearchHistory;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.repository.SearchHistoryRepository;
import toy.cookingstar.service.search.dto.UserSearchDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchJpaService {

    private final MemberRepository memberRepository;
    private final SearchHistoryRepository searchHistoryRepository;

    public List<UserSearchDto> getRecentSearchHistory(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);

        Pageable limitFifty = PageRequest.of(0, 50, Sort.by(Sort.Order.desc("lastSearchDate")));

        return searchHistoryRepository.findSearchHistoryByMember(member, limitFifty)
                                      .map(UserSearchDto::of)
                                      .getContent();
    }

    @Transactional
    public void saveHistory(Member loginMember, Member searchedUser) {
        if (memberRepository.findById(loginMember.getId()).isEmpty()
                || memberRepository.findById(searchedUser.getId()).isEmpty()) {
            return;
        }

        //userId 검색 기록이 이미 존재하는 경우에는 lastSearchedDate 최신화
        SearchHistory history = searchHistoryRepository.findByMemberAndSearchedUser(loginMember, searchedUser);
        if (history != null) {
            history.updateLastSearchDate();
            return;
        }

        //userId 검색 기록이 존재하지 않는 경우에는 새로운 기록 저장
        searchHistoryRepository.save(new SearchHistory(loginMember, searchedUser));
    }

    @Transactional
    public void clearAll(Member loginMember) {
        Member member = memberRepository.findById(loginMember.getId()).orElseThrow(IllegalArgumentException::new);

        searchHistoryRepository.deleteAllByMember(member);
    }

    public List<UserSearchDto> searchUsers(String keyword) {
        Pageable limitFifty = PageRequest.of(0, 50);
        return memberRepository.findByKeyword(keyword, limitFifty)
                               .map(UserSearchDto::of)
                               .getContent();
    }

    @Transactional
    public void deleteHistory(Member loginMember, Member searchedUser) {
        if (memberRepository.findById(loginMember.getId()).isEmpty()
                || memberRepository.findById(searchedUser.getId()).isEmpty()) {
            return;
        }
        searchHistoryRepository.deleteByMemberAndSearchedUser(loginMember, searchedUser);
    }
}
