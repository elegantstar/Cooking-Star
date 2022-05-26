package toy.cookingstar.web.controller.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import toy.cookingstar.entity.Member;
import toy.cookingstar.service.search.SearchService;
import toy.cookingstar.service.user.UserService;
import toy.cookingstar.web.argumentresolver.Login;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final UserService userService;
    private final SearchService searchService;

    @PostMapping("/search/{userId}")
    public String saveSearchHistory(@Login Member loginUser, @PathVariable("userId") String searchedUserId) {
        Member searchedUser = userService.getUserInfoByUserId(searchedUserId);
        searchService.saveHistory(loginUser, searchedUser);
        return "redirect:/user/" + searchedUserId;
    }

}
