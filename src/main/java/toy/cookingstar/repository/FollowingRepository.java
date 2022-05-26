package toy.cookingstar.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.cookingstar.entity.Following;
import toy.cookingstar.entity.Member;

public interface FollowingRepository extends JpaRepository<Following, Long> {

    @Query("select count(f) from Following f where f.followedMember = :followedMember")
    int countFollowers(@Param("followedMember") Member member);

    @Query("select count(f) from Following f where f.follower = :follower")
    int countFollowings(@Param("follower") Member member);

    @Query("select f from Following f where f.followedMember = :followedMember")
    Slice<Following> findFollowersByFollowedMember(@Param("followedMember") Member followedMember, Pageable pageable);

    @Query("select f from Following f where f.follower = :follower")
    Slice<Following> findFollowingsByFollower(@Param("follower") Member member, Pageable pageable);

    Boolean existsByFollowerAndFollowedMember(@Param("follower") Member follower, @Param("followedMember") Member followedMember);

    @Query("select f.id from Following f where f.follower = :follower and f.followedMember = :followedMember")
    Long findIdByFollowerAndFollowedMember(@Param("follower") Member follower, @Param("followedMember") Member followedMember);
}
