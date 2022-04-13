package toy.cookingstar.web.controller.post;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.cookingstar.entity.Member;
import toy.cookingstar.service.post.PostJpaService;
import toy.cookingstar.service.post.StatusType;
import toy.cookingstar.service.post.dto.TempStoredPostDto;
import toy.cookingstar.service.user.UserJpaService;
import toy.cookingstar.web.argumentresolver.Login;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PostJpaRestController {

    private final UserJpaService userService;
    private final PostJpaService postService;

    @GetMapping("/post/temporary-storage")
    public List<TempStoredPostDto> temporaryStorage(@Login Member loginUser) {
        Member member = userService.getUserInfoByUserId(loginUser.getUserId());

        return postService.getTemporaryStorage(member.getId(), StatusType.TEMPORARY_STORAGE, 0, 7)
                .stream()
                .map(TempStoredPostDto::of)
                .collect(Collectors.toList());
    }

}
