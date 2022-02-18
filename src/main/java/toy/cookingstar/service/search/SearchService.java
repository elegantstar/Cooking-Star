package toy.cookingstar.service.search;

import java.util.List;

import toy.cookingstar.domain.Member;

public interface SearchService {
    List<Member> getRecentSearchHistory(Long memberId);

    void saveHistory(Member loginMember, String userId);

    void clearAll(Long memberId);

    List<Member> searchUsers(String keyword);

    void deleteHistory(Long memberId, String userId);
}
