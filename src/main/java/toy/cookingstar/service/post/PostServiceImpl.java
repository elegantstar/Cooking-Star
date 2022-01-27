package toy.cookingstar.service.post;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

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
    public List<String> getUserPagePostImages(PostImageParam postImageParam) {

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

        ArrayList<String> postImages = new ArrayList<>();
        for (PostWithImage postWithImage : postWithImages) {
            for (PostImage image : postWithImage.getImages()) {
                if (!StringUtils.isEmpty(image.getUrl())) {
                    postImages.add(image.getUrl());
                }
            }
        }

        return postImages;
    }

    @Override
    public int countPosts(Long memberId) {
        return postRepository.countPosts(memberId);
    }

}
