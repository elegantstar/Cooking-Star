package toy.cookingstar.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

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

    @Column(name = "last_search_date", insertable = false)
    private LocalDateTime lastSearchDate;

    public SearchHistory(Member member, Member searchedUser) {
        this.member = member;
        this.searchedUser = searchedUser;
    }

    public void updateLastSearchDate() {
        this.lastSearchDate = LocalDateTime.now();
    }
}
