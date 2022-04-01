package toy.cookingstar.aws;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class AwsUtils {

    private final S3Client client;

    public void upload(MultipartFile multipartFile, String imageUrl) throws IOException {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                                                   .bucket("cookingstar-bucket")
                                                   .key(imageUrl)
                                                   .build();

        RequestBody requestBody = RequestBody
                .fromInputStream(multipartFile.getInputStream(), multipartFile.getSize());

        client.putObject(objectRequest, requestBody);
    }

}
