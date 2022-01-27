package toy.cookingstar.service.post;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import lombok.RequiredArgsConstructor;
import toy.cookingstar.domain.Member;
import toy.cookingstar.domain.Post;
import toy.cookingstar.domain.PostImage;
import toy.cookingstar.domain.PostWithImage;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.repository.PostRepository;
import toy.cookingstar.utils.PagingVO;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

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

    @Override
    public List<HashMap<String, String>> getUserPagePostImages(PostImageParam postImageParam) {

        Member user = memberRepository.findByUserId(postImageParam.getUserId());

        if (user == null) {
            return null;
        }

        int totalPost = countPosts(user.getId());
        PagingVO pagingVO = new PagingVO(totalPost, postImageParam.getCurrentPageNo(),
                                         postImageParam.getCountPages(), postImageParam.getPostsPerPage());

        HashMap<String, Object> map = new HashMap<>();
        map.put("memberId", user.getId());
        map.put("pagingVO", pagingVO);

        List<PostWithImage> postWithImages = postRepository.findUserPagePostImage(map);

        if (CollectionUtils.isEmpty(postWithImages)) {
            return null;
        }

        ArrayList<HashMap<String, String>> postImages = new ArrayList<>();

        for (PostWithImage postWithImage : postWithImages) {
            for (PostImage image : postWithImage.getImages()) {
                if (!StringUtils.isEmpty(image.getUrl())) {

                    //HashMap을 이용해 imageUrl과 PostUrl을 넣어준다.
                    HashMap<String, String> postImageUrls = new HashMap<>();
                    postImageUrls.put("imageUrl", image.getUrl());
                    postImageUrls.put("postUrl", extractPostUrl(postWithImage));

                    postImages.add(postImageUrls);
                }
            }
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

    //String 타입인 postUrl을 LocalDateTime 타입의 createdDate로 변환
    private LocalDateTime postUrlToCreatedDate(String postUrl) {
        String stringDate = postUrl.substring(0, 3)
                            + "-"
                            + postUrl.substring(4, 5)
                            + "-"
                            + postUrl.substring(6, 7)
                            + " "
                            + postUrl.substring(8, 9)
                            + ":"
                            + postUrl.substring(10, 11)
                            + ":"
                            + postUrl.substring(12, 13);

        return LocalDateTime.parse(stringDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public int countPosts(Long memberId) {
        return postRepository.countPosts(memberId);
    }

}
