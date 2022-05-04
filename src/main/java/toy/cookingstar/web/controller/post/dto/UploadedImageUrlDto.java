package toy.cookingstar.web.controller.post.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UploadedImageUrlDto {
    private List<String> imageUrls;

    public static UploadedImageUrlDto of(List<String> imageUris) {
        UploadedImageUrlDto dto = new UploadedImageUrlDto();
        List<String> imageUrls = new ArrayList<>();
        for (String imageUri : imageUris) {
            String dir = imageUri.substring(0, 10);
            imageUrls.add("https://d9voyddk1ma4s.cloudfront.net/images/post/" + dir + "/" + imageUri);
        }
        dto.imageUrls = imageUrls;
        return dto;
    }
}
