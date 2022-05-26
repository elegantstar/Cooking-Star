package toy.cookingstar.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.cookingstar.service.post.StatusType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST)
    private List<PostImage> postImages = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusType status;

    @OneToMany(mappedBy = "post")
    private List<PostLiker> postLikers = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostComment> postComments = new ArrayList<>();

    //연관 관계 편의 메서드
    public void addPostImage(PostImage postImage) {
        postImages.add(postImage);
        postImage.setPost(this);
    }

    //생성 메서드
    public static Post createPost(Member member, String content, StatusType status, List<PostImage> postImages) {
        Post post = new Post();
        post.member = member;
        post.content = content;
        post.status = status;
        postImages.forEach(post::addPostImage);
        return post;
    }

    public void updatePost(String content, StatusType status) {
        this.content = content;
        this.status = status;
    }

    public void deletePost(StatusType status) {
        this.status = status;
    }
}
