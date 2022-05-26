package toy.cookingstar.entity;

import static javax.persistence.FetchType.*;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_image")
public class PostImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    private String url;
    private int priority;

    public void setPost(Post post) {
        this.post = post;
    }

    //생성 메서드
    public static PostImage createPostImage(String url, int priority) {
        PostImage postImage = new PostImage();
        postImage.url = url;
        postImage.priority = priority;
        return postImage;
    }
}
