package toy.cookingstar.service.post;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import toy.cookingstar.domain.Member;
import toy.cookingstar.domain.Post;
import toy.cookingstar.domain.PostImage;
import toy.cookingstar.domain.PostWithImage;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.repository.PostRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void createPost(PostCreateParam postCreateParam) {

        String userId = postCreateParam.getUserId();
        Member user = memberRepository.findByUserId(userId);

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
    }

    @Override
    public List<HashMap<String, String>> getUserPagePostImages(String userId, int start, int end) {

        Member user = memberRepository.findByUserId(userId);

        if (user == null) {
            return null;
        }

        List<PostWithImage> postWithImages = postRepository.findUserPagePostImage(user.getId(), start, end);

        if (CollectionUtils.isEmpty(postWithImages)) {
            return null;
        }

//        List<String> imageUrls = postWithImages.stream().map(PostWithImage::getImages).flatMap(Collection::stream)
//                                               .collect(Collectors.toList()).stream().map(PostImage::getUrl)
//                                               .collect(Collectors.toList());

        ArrayList<HashMap<String, String>> postImages = new ArrayList<>();

        for (PostWithImage postWithImage : postWithImages) {
            PostImage image = postWithImage.getImages().get(0);

            //HashMap을 이용해 imageUrl과 PostUrl을 넣어준다.
            HashMap<String, String> postImageUrls = new HashMap<>();
            postImageUrls.put("imageUrl", image.getUrl());
            postImageUrls.put("postUrl", extractPostUrl(postWithImage));

            postImages.add(postImageUrls);

        }
        return postImages;
    }

    @Override
    public PostWithImage getPostInfo(Long memberId, String postUrl) {

        LocalDateTime createdDate = postUrlToCreatedDate(postUrl);

        PostWithImage postInfo = postRepository.findPostInfo(memberId, createdDate);

        if (postInfo == null) {
            return null;
        } else if (CollectionUtils.isEmpty(postInfo.getImages())) {
            return null;
        }

        return postInfo;
    }

    //createdDate를 Post Page의 Url로 사용하기 위해 숫자만 추출
    private String extractPostUrl(PostWithImage postWithImage) {
        return postWithImage.getCreatedDate()
                            .toString()
                            .replace("T", "")
                            .replace(" ", "")
                            .replace("-", "")
                            .replace(":", "");
    }

    private String extractPostUrls(PostWithImage postWithImage) {
        DateTimeFormatter postUrlFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return postWithImage.getCreatedDate().format(postUrlFormatter);
    }

    //String 타입인 postUrl을 LocalDateTime 타입의 createdDate로 변환
    private LocalDateTime postUrlToCreatedDate(String postUrl) {
        String stringDate = postUrl.substring(0, 4)
                            + "-"
                            + postUrl.substring(4, 6)
                            + "-"
                            + postUrl.substring(6, 8)
                            + " "
                            + postUrl.substring(8, 10)
                            + ":"
                            + postUrl.substring(10, 12)
                            + ":"
                            + postUrl.substring(12);

        return LocalDateTime.parse(stringDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public int countPosts(Long memberId) {
        return postRepository.countPosts(memberId);
    }

}
