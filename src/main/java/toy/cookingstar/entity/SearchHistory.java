package toy.cookingstar.entity;

import static javax.persistence.FetchType.*;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "search_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "searched_member_id")
    private Member searchedUser;

    @Column(name = "last_search_date")
    private LocalDateTime lastSearchDate;

    public SearchHistory(Member member, Member searchedUser) {
        this.member = member;
        this.searchedUser = searchedUser;
    }

    public void updateLastSearchDate() {
        this.lastSearchDate = LocalDateTime.now();
    }
}
