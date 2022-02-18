package toy.cookingstar.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import toy.cookingstar.domain.SearchHistory;

@Mapper
public interface SearchHistoryRepository {

    void save(SearchHistory searchHistory);

    SearchHistory findHistory(@Param("memberId") Long memberId, @Param("searchedUserId") String searchedUserId);

    void updateLastSearchDate(@Param("memberId") Long memberId, @Param("searchedUserId") String searchedUserId);

    void clearAll(Long memberId);

    void deleteHistory(@Param("memberId") Long memberId, @Param("searchedUserId") String searchedUserId);
}
