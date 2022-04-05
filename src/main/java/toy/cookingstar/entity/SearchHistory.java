package toy.cookingstar.entity;

import static javax.persistence.FetchType.*;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "searched_user_id")
    private String searchedUserId;

    @Column(name = "last_search_date")
    private LocalDateTime lastSearchDate;

    @Builder
    public SearchHistory(Member member, String searchedUserId) {
        this.member = member;
        this.searchedUserId = searchedUserId;
    }
}
