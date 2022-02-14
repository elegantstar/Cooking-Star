package toy.cookingstar.web.controller.post;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import toy.cookingstar.domain.Member;
import toy.cookingstar.service.post.PostService;
import toy.cookingstar.service.post.StatusType;
import toy.cookingstar.service.user.UserService;
import toy.cookingstar.web.argumentresolver.Login;
import toy.cookingstar.web.controller.post.dto.TempStoredData;

@RestController
@RequiredArgsConstructor
public class PostRestController {

    private final UserService userService;
    private final PostService postService;

    @GetMapping("/post/temporary-storage")
    public TempStoredData temporaryStorage(@Login Member loginUser) {

        Member loginMember = userService.getUserInfo(loginUser.getUserId());

        if (loginMember == null) {
            return null;
        }

        return postService.getTemporaryStorage(loginMember.getId(), 0, 7, StatusType.TEMPORARY_STORAGE);
    }

}
