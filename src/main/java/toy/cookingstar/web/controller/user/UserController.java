package toy.cookingstar.web.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import toy.cookingstar.domain.Member;
import toy.cookingstar.service.user.UserService;
import toy.cookingstar.web.argumentresolver.Login;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public String userPage(@PathVariable String userId, @Login Member loginUser, Model model) {

        // 요청 받은 userId와 일치하는 회원이 있는지 확인 후 데이터 가져오기 -> 일치하는 회원이 없을 경우 exception 처리 필요함.
        Member userPageInfo = userService.getUserInfo(userId);

        if (userPageInfo == null) {
            return "error-page/404";
        }

        model.addAttribute("userPageInfo", userPageInfo);

        // 요청 받은 userId가 로그인 유저의 userId와 같으면 myPage로
        if (userId.equals(loginUser.getUserId())) {
            return "user/myPage";
        }

        // userPage로
        return "user/userPage";
    }

    @GetMapping("/myPage")
    public String myPage(@Login Member loginUser, Model model) {

        Member userPageInfo = userService.getUserInfo(loginUser.getUserId());

        model.addAttribute("userPageInfo", userPageInfo);

        return "user/myPage";
    }
}
