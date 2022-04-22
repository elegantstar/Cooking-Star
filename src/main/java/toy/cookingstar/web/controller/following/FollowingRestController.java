package toy.cookingstar.web.controller.following;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toy.cookingstar.entity.Member;
import toy.cookingstar.service.following.FollowingService;
import toy.cookingstar.service.search.dto.UserSearchDto;
import toy.cookingstar.web.argumentresolver.Login;
import toy.cookingstar.web.controller.following.dto.FollowingRelationshipDto;
import toy.cookingstar.web.controller.following.dto.FollowingSaveDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/following")
public class FollowingRestController {

    private final FollowingService followingService;

    @PostMapping
    public ResponseEntity<?> createFollowing(@Login Member loginUser,
                                             @RequestBody FollowingSaveDto followingSaveDto) throws Exception {

        boolean following = followingService.checkForFollowing(loginUser.getId(), followingSaveDto.getFollowedUserId());
        if (following) {
            return ResponseEntity.badRequest().build();
        }

        followingService.create(loginUser.getId(), followingSaveDto.getFollowedUserId());
        return ResponseEntity.ok().build();
    }

    // 팔로워 리스트 조회
    @GetMapping("/{userId}/followers")
    public ResponseEntity<Slice<UserSearchDto>> getFollowers(@PathVariable("userId") String userId,
                                                             @RequestParam int page, @RequestParam int size) throws Exception {
        Slice<UserSearchDto> followerSlice = followingService.getFollowers(userId, page, size).map(UserSearchDto::of);
        return ResponseEntity.ok().body(followerSlice);
    }

    // 팔로잉 리스트 조회
    @GetMapping("/{userId}/followings")
    public ResponseEntity<Slice<UserSearchDto>> getFollowings(@PathVariable("userId") String userId,
                                                              @RequestParam int page, @RequestParam int size) throws Exception {
        Slice<UserSearchDto> followingSlice = followingService.getFollowings(userId, page, size).map(UserSearchDto::of);
        return ResponseEntity.ok().body(followingSlice);
    }

    // 팔로잉 관계 확인(로그인 유저의 특정 유저 팔로잉 여부, 특정 유저의 로그인 유저 팔로잉 여부)
    @GetMapping
    public ResponseEntity<FollowingRelationshipDto> checkForFollowingRelationship(@Login Member loginUser,
                                                                      @RequestParam String userId) throws Exception{
        // loginUser가 user를 팔로잉 하는지 여부
        boolean following = followingService.checkForFollowing(loginUser.getId(), userId);
        // user가 loginUser를 팔로잉 하는지 여부
        boolean followed = followingService.checkForFollowed(loginUser.getId(), userId);
        FollowingRelationshipDto followingRelationshipDto = new FollowingRelationshipDto(following, followed);
        return ResponseEntity.ok().body(followingRelationshipDto);
    }

    // 언팔로잉
    @PostMapping("/{userId}/deletion")
    public ResponseEntity<?> deleteFollowing(@Login Member loginUser,
                                             @PathVariable("userId") String userId) throws Exception {
        followingService.deleteFollowing(loginUser.getId(), userId);
        return ResponseEntity.ok().build();
    }
}
