package toy.cookingstar.service.post;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import toy.cookingstar.domain.Member;

public interface PostService {

    String getFullPath(String imageName);

    List<String> storeImages(List<MultipartFile> multipartFiles) throws IOException;

    String storeImage(MultipartFile multipartFile) throws IOException;

    Member createPost(PostCreateParam postCreateParam);
}
