package toy.cookingstar.web.controller.user;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import toy.cookingstar.domain.Member;
import toy.cookingstar.service.imagestore.ImageStoreService;
import toy.cookingstar.service.imagestore.ImageType;
import toy.cookingstar.service.user.UserService;
import toy.cookingstar.utils.SessionUtils;
import toy.cookingstar.web.argumentresolver.Login;
import toy.cookingstar.web.controller.user.dto.ProfileImageDto;
import toy.cookingstar.web.controller.user.form.ProfileImgUpdateForm;

@RestController
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final ImageStoreService imageStoreService;

    @PostMapping("/myPage/profile-delete")
    public void deleteProfileImg(@Login Member loginUser, HttpServletRequest request) {

        Member loginMember = userService.getUserInfo(loginUser.getId());
        if (loginMember == null) {
            return;
        }

        userService.deleteProfileImg(loginMember.getId());

        // Session member update
        SessionUtils.refreshMember(userService.getUserInfo(loginUser.getUserId()), request);
    }

    @PostMapping("/myPage/profile-upload")
    public ProfileImageDto uploadProfileImg(@Login Member loginUser, @ModelAttribute ProfileImgUpdateForm form, HttpServletRequest request)
            throws IOException {

        Member loginMember = userService.getUserInfo(loginUser.getId());
        if (loginMember == null) {
            return null;
        }

        //프로필 이미지 업로드
        String storedProfileImage;

        if (StringUtils.isEmpty(form.getProfileImage().getOriginalFilename())) {
            storedProfileImage = loginUser.getProfileImage();
        } else {
            storedProfileImage = imageStoreService.storeImage(ImageType.PROFILE, form.getProfileImage());
        }

        userService.updateProfileImg(loginMember.getId(), storedProfileImage);

        // Session member update
        SessionUtils.refreshMember(userService.getUserInfo(loginUser.getUserId()), request);

        Member userInfo = userService.getUserInfo(loginMember.getId());

        return ProfileImageDto.of(userInfo);
    }
}
