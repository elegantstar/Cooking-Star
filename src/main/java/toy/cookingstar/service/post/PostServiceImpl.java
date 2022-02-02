package toy.cookingstar.service.post;

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
    public Post findPostByPostId(Long postId) {
        return postRepository.findByPostId(postId);
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

        //TODO: 아래 로직 간단히 변경 (PostController, myPost.html, userPost.html 함께 변경)

        ArrayList<HashMap<String, String>> postImages = new ArrayList<>();

        for (PostWithImage postWithImage : postWithImages) {
            PostImage image = postWithImage.getImages().get(0);

            //HashMap을 이용해 imageUrl과 PostUrl을 넣어준다.
            HashMap<String, String> postImageUrls = new HashMap<>();
            postImageUrls.put("imageUrl", image.getUrl());
            postImageUrls.put("postUrl", postWithImage.getId().toString());

            postImages.add(postImageUrls);

        }
        return postImages;
    }

    @Override
    public PostWithImage getPostInfo(Long postId) {

        PostWithImage postInfo = postRepository.findPostInfo(postId);

        if (postInfo == null) {
            return null;
        } else if (CollectionUtils.isEmpty(postInfo.getImages())) {
            return null;
        }

        return postInfo;
    }

    @Override
    public int countPosts(Long memberId) {
        return postRepository.countPosts(memberId);
    }

    @Override
    @Transactional
    public void deletePost(String userId, Long postId) {
        postRepository.deletePostImages(postId);
        postRepository.deletePost(postId);
        log.info("DELETE POST: userId=[{}], deletedPostId=[{}]", userId, postId);
    }

    @Override
    @Transactional
    public void updatePost(String userId, Long id, String content, StatusType status) {
        postRepository.updatePost(id, content, status);
        log.info("UPDATE POST: userId=[{}], updatedPostId=[{}]", userId, id);
    }
}
