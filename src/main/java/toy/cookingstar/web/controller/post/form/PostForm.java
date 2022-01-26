package toy.cookingstar.web.controller.post.form;

import java.util.List;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NonNull;
import toy.cookingstar.service.post.StatusType;

@Data
public class PostForm {

    @Size(min = 1, max = 10)
    private List<MultipartFile> images;

    @Length(max = 2200)
    private String content;

    public void removeEmptyImage() {
        images.removeIf(image -> StringUtils.isEmpty(image.getOriginalFilename()));
    }
}
