package toy.cookingstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import toy.cookingstar.entity.SearchHistory;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
}
