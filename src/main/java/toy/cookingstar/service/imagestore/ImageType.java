package toy.cookingstar.service.imagestore;

import lombok.Getter;

@Getter
public enum ImageType {
    POST("postImage"),
    PROFILE("profileImage");

    private final String imageType;

    ImageType(String imageType) {
        this.imageType = imageType;
    }
}
