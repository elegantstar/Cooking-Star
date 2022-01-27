package toy.cookingstar.web.controller.post;

import java.io.IOException;
import java.util.List;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import toy.cookingstar.domain.Member;
import toy.cookingstar.service.post.PostCreateParam;
import toy.cookingstar.service.post.PostService;
import toy.cookingstar.service.post.StatusType;
import toy.cookingstar.service.user.UserService;
import toy.cookingstar.web.argumentresolver.Login;
import toy.cookingstar.web.controller.post.form.PostForm;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @GetMapping("/create")
    public String createForm(@ModelAttribute("post") PostForm form) {
        return "post/createForm";
    }

    /**
     * @param status : view에서 임시저장, 등록 두 개의 button submit에 따라 status enum 값을 달리 주기 위한 요청 파라미터
     */
    @PostMapping("/create")
    public String create(@Validated @ModelAttribute("post") PostForm form, @RequestParam StatusType status,
                         BindingResult bindingResult, @Login Member loginUser,
                         RedirectAttributes redirectAttributes) throws IOException {

        form.removeEmptyImage();

        if (CollectionUtils.isEmpty(form.getImages())) {
            bindingResult.reject("post.images.empty");
        }

        if (bindingResult.hasErrors()) {
            log.error("errors={}", bindingResult);
            return "post/createForm";
        }

        Member userInfo = userService.getUserInfo(loginUser.getUserId());

        if (userInfo == null) {
            return "error-page/404";
        }

        // 서버에 이미지 업로드
        List<String> storedImages = postService.storeImages(form.getImages());

        PostCreateParam postCreateParam = new PostCreateParam(loginUser.getUserId(), form.getContent(), status, storedImages);

        // DB에 post 데이터 저장 + postImage 데이터 저장
        postService.createPost(postCreateParam);

        redirectAttributes.addFlashAttribute("userPageInfo", loginUser);
        return "redirect:/myPage";
    }
}
