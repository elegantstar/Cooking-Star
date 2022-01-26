package toy.cookingstar.service.post;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import toy.cookingstar.domain.Member;
import toy.cookingstar.domain.PostImage;
import toy.cookingstar.domain.PostWithImage;

public interface PostService {

    String getFullPath(String url);

    List<String> storeImages(List<MultipartFile> multipartFiles) throws IOException;

    String storeImage(MultipartFile multipartFile) throws IOException;

    Member createPost(PostCreateParam postCreateParam);

    List<String> getUserPagePostImages(PostImageParam postImageParam);

    int countPosts(Long memberId);
}
