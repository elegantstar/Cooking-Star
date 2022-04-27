package toy.cookingstar.web.controller.post.dto;

import lombok.Data;

import java.util.List;

@Data
public class UploadedImageUrlDto {
    private List<String> imageUrls;

    public static UploadedImageUrlDto of(List<String> imageUrls) {
        UploadedImageUrlDto dto = new UploadedImageUrlDto();
        dto.imageUrls = imageUrls;
        return dto;
    }
}
