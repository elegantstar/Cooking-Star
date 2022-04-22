package toy.cookingstar.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Following extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member follower;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "followed_member_id")
    private Member followedMember;

    public static Following createFollowing(Member follower, Member followedMember) {
        Following following = new Following();
        following.follower = follower;
        following.followedMember = followedMember;
        return following;
    }

}
