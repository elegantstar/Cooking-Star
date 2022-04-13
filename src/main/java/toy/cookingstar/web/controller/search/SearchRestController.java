package toy.cookingstar.web.controller.search;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import toy.cookingstar.entity.Member;
import toy.cookingstar.service.search.SearchService;
import toy.cookingstar.service.search.dto.UserSearchDto;
import toy.cookingstar.service.user.UserService;
import toy.cookingstar.web.argumentresolver.Login;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class SearchRestController {

    private final UserService userService;
    private final SearchService searchService;

    @GetMapping("/search/history")
    public List<UserSearchDto> recentHistory(@Login Member loginUser) {
        Member loginMember = userService.getUserInfoByUserId(loginUser.getUserId());
        return searchService.getRecentSearchHistory(loginMember.getId())
                .stream()
                .map(UserSearchDto::of)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/search/history/clearAll")
    public void clearAll(@Login Member loginUser) {
        searchService.clearAll(loginUser);
    }

    @GetMapping("/search/users")
    public List<UserSearchDto> userSearch(@RequestParam String keyword) {
        return searchService.searchUsers(keyword);
    }

    @PostMapping("/search/history/add")
    public void addSearchHistory(@RequestParam("userId") String searchedUserId, @Login Member loginUser) {
        Member searchedUser = userService.getUserInfoByUserId(searchedUserId);
        searchService.saveHistory(loginUser, searchedUser);
    }

    @DeleteMapping("/search/history/delete")
    public void deleteSearchHistory(@Login Member loginUser, @RequestParam("userId") String userId) {
        Member searchedUser = userService.getUserInfoByUserId(userId);
        searchService.deleteHistory(loginUser, searchedUser);
    }

}
