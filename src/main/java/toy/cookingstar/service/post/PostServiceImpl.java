package toy.cookingstar.service.post;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import toy.cookingstar.domain.Member;
import toy.cookingstar.domain.Post;
import toy.cookingstar.domain.PostImage;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.repository.PostRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Value("${file.dir}")
    private String fileDir;

    // 이미지 저장 경로 찾기
    public String getFullPath(String imageName) {
        return fileDir + imageName;
    }

    // 이미지 다건 등록
    public List<String> storeImages(List<MultipartFile> multipartFiles) throws IOException {

        if (multipartFiles.isEmpty()) {
            return null;
        }

        List<String> storeImageResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeImageResult.add(storeImage(multipartFile));
            }
        }
        return storeImageResult;
    }

    // 이미지 단건 등록 로직
    public String storeImage(MultipartFile multipartFile) throws IOException {

        // 업로드한 이미지 파일명 추출
        String originalFilename = multipartFile.getOriginalFilename();
        // UUID를 이용하여 이미지 파일명 변경 + 확장자 추가
        String storeImageName = createStoreImageName(originalFilename);
        // 이미지 파일 저장
        multipartFile.transferTo(new File(getFullPath(storeImageName)));

        return getFullPath(storeImageName);
    }

    @Override
    @Transactional
    public Member createPost(PostCreateParam postCreateParam) {

        String userId = postCreateParam.getUserId();
        Member user = memberRepository.findByUserId(userId);

        // 존재하는 유저인지 확인
        if (user == null) {
            return null;
        }

        Post post = Post.builder()
                        .memberId(user.getId())
                        .content(postCreateParam.getContent())
                        .status(postCreateParam.getStatus())
                        .build();

        // post 테이블에 포스트 데이터 생성
        postRepository.create(post);

        // postImage 테이블에 업로드한 이미지 데이터 생성
        List<String> storedImages = postCreateParam.getStoredImages();
        for (String storedImage : storedImages) {
            PostImage postImage = PostImage.builder()
                                           .postId(post.getId())
                                           .url(storedImage)
                                           .priority(storedImages.indexOf(storedImage) + 1)
                                           .build();

            postRepository.saveImage(postImage);
        }

        return user;
    }

    // 저장할 이미지 이름 생성(중복 방지)
    private String createStoreImageName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    // 이미지 확장자 추출
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

}
