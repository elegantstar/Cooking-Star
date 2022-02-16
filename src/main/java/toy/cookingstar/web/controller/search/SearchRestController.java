package toy.cookingstar.web.controller.search;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import toy.cookingstar.domain.Member;
import toy.cookingstar.service.search.SearchService;
import toy.cookingstar.service.user.UserService;
import toy.cookingstar.web.argumentresolver.Login;
import toy.cookingstar.web.controller.search.dto.UserSearchDto;

@RestController
@RequiredArgsConstructor
public class SearchRestController {

    private final UserService userService;
    private final SearchService searchService;

    @GetMapping("/search/history")
    public List<UserSearchDto> recentHistory(@Login Member loginUser) {

        Member loginMember = userService.getUserInfo(loginUser.getUserId());
        if (loginMember == null) {
            return null;
        }

        List<Member> recentSearchHistory = searchService.getRecentSearchHistory(loginMember.getId());

        if (CollectionUtils.isEmpty(recentSearchHistory)) {
            return null;
        }

        return recentSearchHistory.stream().map(UserSearchDto::of).collect(Collectors.toList());
    }

    @GetMapping("/search/history/clearAll")
    public void clearAll(@Login Member loginUser) {

        Member loginMember = userService.getUserInfo(loginUser.getUserId());
        if (loginMember == null) {
            return;
        }

        searchService.clearAll(loginMember.getId());
    }

    @GetMapping("/search/users")
    public List<UserSearchDto> userSearch(@RequestParam String keyword) {
        List<Member> searchResults = searchService.searchUsers(keyword);

        if (CollectionUtils.isEmpty(searchResults)) {
            return null;
        }

        return searchResults.stream().map(UserSearchDto::of).collect(Collectors.toList());
    }

}
