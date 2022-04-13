package toy.cookingstar.web.controller.search;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
import toy.cookingstar.domain.Member;
import toy.cookingstar.service.search.SearchService;
import toy.cookingstar.service.user.UserService;
import toy.cookingstar.web.argumentresolver.Login;

//@Controller
@RequiredArgsConstructor
public class SearchController {

    private final UserService userService;
    private final SearchService searchService;

    @PostMapping("/search/{userId}")
    public String saveSearchHistory(@Login Member loginUser, @PathVariable("userId") String searchedUserId) {

        Member loginMember = userService.getUserInfo(loginUser.getUserId());
        if (loginMember == null) {
            return null;
        }

        searchService.saveHistory(loginMember, searchedUserId);

        return "redirect:/user/" + searchedUserId;
    }

}
