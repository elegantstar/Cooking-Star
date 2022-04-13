package toy.cookingstar.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.cookingstar.service.post.StatusType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    private String content;

    //TODO: orphanRemoval을 true로 두고 PostRepository가 post 삭제 시 postImages를 함께 삭제하도록 할 것인지(postImage 수만큼 delete query 발생)
    //TODO: postImages는 PostImageRepository에서 jpql을 통해 한 번에 삭제하도록 할 것인지 결정(삭제할 post의 자식에 해당하는 postImage를 query 한 번에 삭제)
    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST)
    private List<PostImage> postImages = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusType status;

    @Column(name = "created_date", insertable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date", insertable = false, updatable = false)
    private LocalDateTime updatedDate;

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
}
