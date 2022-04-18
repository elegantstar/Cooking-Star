package toy.cookingstar.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Table(name = "post_comment_liker")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCommentLiker {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_comment_id")
    private PostComment postComment;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    public static PostCommentLiker createCommentLiker(Member member, PostComment comment) {
        PostCommentLiker postCommentLiker = new PostCommentLiker();
        postCommentLiker.member = member;
        postCommentLiker.postComment = comment;
        return postCommentLiker;
    }
}
