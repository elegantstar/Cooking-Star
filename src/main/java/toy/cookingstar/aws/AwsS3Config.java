package toy.cookingstar.aws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsS3Config {

    private final String accessKey;
    private final String secretKey;
    private final String region;

    public AwsS3Config() {
        this.accessKey = System.getenv("AWS_ACCESS_KEY_ID");
        this.secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        this.region = System.getenv("AWS_REGION");
    }

    @Bean
    public S3Client s3Client() {

        AwsBasicCredentials awsCreds = AwsBasicCredentials
                .create(accessKey, secretKey);

        return S3Client.builder()
                       .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                       .region(Region.of(region))
                       .build();
    }

}
