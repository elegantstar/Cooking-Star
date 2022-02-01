package toy.cookingstar.service.post;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface PostService {

    String getFullPath(String url);

    List<String> storeImages(List<MultipartFile> multipartFiles) throws IOException;

    String storeImage(MultipartFile multipartFile) throws IOException;

    void createPost(PostCreateParam postCreateParam);

    List<String> getUserPagePostImages(String userId, int start, int end);

    int countPosts(Long memberId);
}
