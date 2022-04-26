package toy.cookingstar.web.controller.post;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import toy.cookingstar.domain.Member;
import toy.cookingstar.domain.PostWithImage;
import toy.cookingstar.service.post.PostService;
import toy.cookingstar.service.post.StatusType;
import toy.cookingstar.service.user.UserService;
import toy.cookingstar.web.argumentresolver.Login;
import toy.cookingstar.web.controller.post.dto.TempStoredDto;

@RestController
@RequiredArgsConstructor
public class PostRestController {

    private final UserService userService;
    private final PostService postService;

    @GetMapping("/post/temporary-storage")
    public List<TempStoredDto> temporaryStorage(@Login Member loginUser) {

        Member loginMember = userService.getUserInfo(loginUser.getUserId());

        if (loginMember == null) {
            return null;
        }

        List<PostWithImage> temporaryStorage = postService.getTemporaryStorage(loginMember.getId(),
                                                                               StatusType.TEMPORARY_STORAGE,
                                                                               0, 7);

        return temporaryStorage.stream().map(TempStoredDto::of).collect(Collectors.toList());
    }

}
