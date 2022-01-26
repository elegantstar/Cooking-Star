package toy.cookingstar.domain;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import toy.cookingstar.service.post.StatusType;

@Getter
@Setter
@NoArgsConstructor
public class Post {

    private Long id;
    private Long memberId;
    private String content;
    private StatusType status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @Builder
    public Post(Long memberId, String content, StatusType status) {
        this.memberId = memberId;
        this.content = content;
        this.status = status;
    }
}
