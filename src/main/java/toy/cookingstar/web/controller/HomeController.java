package toy.cookingstar.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import toy.cookingstar.entity.Member;
import toy.cookingstar.service.post.StatusType;
import toy.cookingstar.service.user.UserService;
import toy.cookingstar.web.controller.post.dto.PostAndImageUrlDto;
import toy.cookingstar.web.controller.user.dto.UserInfoDto;
import toy.cookingstar.web.argumentresolver.Login;

import java.util.List;
import java.util.concurrent.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final UserService userService;

    @GetMapping("/")
    public String loginHome(@Login Member loginMember, Model model) {

        // 세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }

        Member member = userService.getUserInfoByUserId(loginMember.getUserId());
        UserInfoDto userInfo = UserInfoDto.of(member);
        model.addAttribute("userPageInfo", userInfo);
        return "redirect:/myPage";
    }
}
