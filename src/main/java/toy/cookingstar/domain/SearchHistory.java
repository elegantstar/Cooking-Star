package toy.cookingstar.domain;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchHistory {

    private Long id;
    private Long memberId;
    private String searchedUserId;
    private LocalDateTime lastSearchDate;

    @Builder
    public SearchHistory(Long memberId, String searchedUserId) {
        this.memberId = memberId;
        this.searchedUserId = searchedUserId;
    }
}
