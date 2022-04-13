package toy.cookingstar.web.controller.user;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.cookingstar.entity.Member;
import toy.cookingstar.service.imagestore.ImageStoreService;
import toy.cookingstar.service.imagestore.ImageType;
import toy.cookingstar.service.user.UserJpaService;
import toy.cookingstar.utils.SessionJpaUtils;
import toy.cookingstar.web.argumentresolver.Login;
import toy.cookingstar.web.controller.user.dto.ProfileImageDto;
import toy.cookingstar.web.controller.user.form.ProfileImgUpdateForm;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UserJpaRestController {

    private final UserJpaService userService;
    private final ImageStoreService imageStoreService;

    @PostMapping("/myPage/profile-delete")
    public void deleteProfileImg(@Login Member loginUser, HttpServletRequest request) {

        Member loginMember = userService.getUserInfoById(loginUser.getId());
        userService.deleteProfileImg(loginMember.getId());

        // Session member update
        SessionJpaUtils.refreshMember(loginMember, request);
    }

    @PostMapping("/myPage/profile-upload")
    public ProfileImageDto uploadProfileImg(@Login Member loginUser, @ModelAttribute ProfileImgUpdateForm form, HttpServletRequest request)
            throws IOException {

        Member loginMember = userService.getUserInfoById(loginUser.getId());

        //프로필 이미지 업로드
        String storedProfileImage;

        if (StringUtils.isEmpty(form.getProfileImage().getOriginalFilename())) {
            storedProfileImage = loginUser.getProfileImage();
        } else {
            storedProfileImage = imageStoreService.storeImage(ImageType.PROFILE, form.getProfileImage());
        }

        userService.updateProfileImg(loginMember.getId(), storedProfileImage);

        // Session member update
        SessionJpaUtils.refreshMember(loginMember, request);

        return ProfileImageDto.of(userService.getUserInfoById(loginUser.getId()));
    }
}
