package toy.cookingstar.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import toy.cookingstar.domain.SearchHistory;

@Mapper
public interface SearchHistoryRepository {

    void save(SearchHistory searchHistory);

    SearchHistory findHistory(Long memberId, String searchedUserId);

    void updateLastSearchDate(Long memberId, String searchedUserId);

    void clearAll(Long memberId);
}
