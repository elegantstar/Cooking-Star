package toy.cookingstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.cookingstar.entity.Member;
import toy.cookingstar.entity.SearchHistory;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    @EntityGraph(attributePaths = {"searchedUser"})
    Page<SearchHistory> findSearchHistoryByMember(Member member, Pageable pageable);

    SearchHistory findByMemberAndSearchedUser(Member member, Member searchedUser);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from SearchHistory s where s.member = :member")
    void deleteAllByMember(@Param("member") Member member);

    void deleteByMemberAndSearchedUser(Member member, Member searchedUser);
}
