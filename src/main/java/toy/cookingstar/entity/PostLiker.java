package toy.cookingstar.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Table(name = "post_liker")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLiker {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    public static PostLiker createPostLiker(Member member, Post post) {
        PostLiker postLiker = new PostLiker();
        postLiker.member = member;
        postLiker.post = post;
        return postLiker;
    }
}
