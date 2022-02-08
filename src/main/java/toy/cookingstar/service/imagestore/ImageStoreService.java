package toy.cookingstar.service.imagestore;

import static toy.cookingstar.service.imagestore.ImageType.*;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageStoreService {

    @Value("${image.dir}")
    private String imageDir;

    @Value("${profile.dir}")
    private String profileDir;

    //이미지 저장 경로 찾기
    public String getFullPath(ImageType imageType, String uri) {
        String dirPath = getDirPath(imageType);
        if (StringUtils.isEmpty(dirPath)) {
            return null;
        }
        return dirPath + uri.substring(0, 10) + "/" + uri;
    }

    //현재 날짜 로직
    public String getCurrentDate() {
        return new Timestamp(System.currentTimeMillis()).toString().substring(0, 10);
    }

    //이미지 다건 등록 = 포스트 이미지 등록
    public List<String> storeImages(List<MultipartFile> multipartFiles) throws IOException {

        if (multipartFiles.isEmpty()) {
            return null;
        }

        List<String> storeImageResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeImageResult.add(storeImage(POST, multipartFile));
            }
        }
        return storeImageResult;
    }

    //이미지 단건 등록
    public String storeImage(ImageType imageType, MultipartFile multipartFile) throws IOException {

        String dirPath = getDirPath(imageType);

        if (StringUtils.isEmpty(dirPath)) {
            return null;
        }

        // 업로드한 이미지 파일명 추출
        String originalFilename = multipartFile.getOriginalFilename();
        // UUID를 이용하여 이미지 파일명 변경 + 확장자 추가
        String storedImageName = createStoreImageName(originalFilename);
        // 저장 폴더가 없으면 생성
        makeDir(dirPath);
        // 이미지 파일 저장
        multipartFile.transferTo(new File(dirPath + getCurrentDate() + "/" + storedImageName));

        return storedImageName;
    }

    //이미지 저장 경로 결정
    private String getDirPath(ImageType imageType) {

        switch (imageType) {
            case POST:
                return imageDir;
            case PROFILE:
                return profileDir;
        }
        return null;
    }

    // 저장할 이미지 이름 생성(중복 방지)
    private String createStoreImageName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString().substring(0, 13);
        return getCurrentDate() + uuid + "." + ext;
    }

    // 이미지 확장자 추출
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    private void makeDir(String dirPath) {
        File imageFolder = new File(dirPath + getCurrentDate() + "/");

        if (!imageFolder.exists()) {
            imageFolder.mkdir();
        }
    }
}
