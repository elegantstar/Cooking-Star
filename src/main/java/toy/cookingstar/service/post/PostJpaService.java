package toy.cookingstar.service.post;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import toy.cookingstar.entity.Member;
import toy.cookingstar.entity.Post;
import toy.cookingstar.entity.PostImage;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.repository.PostImageRepository;
import toy.cookingstar.repository.PostRepository;
import toy.cookingstar.service.post.dto.PostCreateDto;
import toy.cookingstar.service.post.dto.PostImageUrlDto;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostJpaService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;

    /**
     * 포스트 생성
     */
    @Transactional
    public void create(PostCreateDto postCreateDto) {

        Member user = memberRepository.findByUserId(postCreateDto.getUserId());

        Post post = Post.builder().member(user).content(postCreateDto.getContent()).status(
                postCreateDto.getStatus()).build();

        // post 테이블에 포스트 데이터 생성
        postRepository.save(post);

        List<String> storedImages1 = postCreateDto.getStoredImages();

        // postImage 테이블에 업로드한 이미지 데이터 생성
        List<String> storedImages = postCreateDto.getStoredImages();
        for (String storedImage : storedImages) {
            PostImage postImage = PostImage.builder()
                                           .post(post)
                                           .url(storedImage)
                                           .priority(storedImages.indexOf(storedImage) + 1)
                                           .build();
            postImageRepository.save(postImage);
        }
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);
    }

    /**
     * 유저 페이지 게시물 리스트 조회
     * @return PostImageUrlDto: 게시물 번호 리스트, 게시물 메인 이미지 URl 리스트
     */
    public PostImageUrlDto getUserPagePostImages(String userId, int page, int size, StatusType statusType) {

        Member user = memberRepository.findByUserId(userId);

        if (user == null) {
            return null;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Direction.DESC, "id"));
        Slice<Post> pages = postRepository.findPosts(user.getId(), StatusType.POSTING, pageable);

        if (pages.isEmpty()) {
            return null;
        }

        List<Long> postIds = pages.getContent().stream().map(Post::getId).collect(Collectors.toList());

        List<String> imageUrls = pages.getContent().stream()
                .map(Post::getPostImages).flatMap(Collection::stream)
                .map(PostImage::getUrl).collect(Collectors.toList());

        return new PostImageUrlDto(postIds, imageUrls);
    }

    /**
     * 게시물 단건 조회
     * @return Post 객체
     */
    public Post getPostInfo(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);

        if (CollectionUtils.isEmpty(post.getPostImages())) {
            return null;
        }
        return post;
    }

    /**
     * 유저 총 게시물 수 조회
     */
    public int countPosts(Long memberId) {
        return postRepository.countByMemberId(memberId);
    }

    /**
     * 게시물 삭제
     */
    public void deletePost(String userId, Long postId) {
        postImageRepository.deleteAllByPostId(postId);
        postRepository.deleteById(postId);
        log.info("DELETE POST: userId=[{}], deletedPostId=[{}]", userId, postId);
    }

    /**
     * 게시물 수정
     */
    @Transactional
    public void updatePost(String userId, Long postId, String content, StatusType status) {
        Post foundPost = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);
        foundPost.updatePost(content, status);
        log.info("UPDATE POST: userId=[{}], updatedPostId=[{}]", userId, postId);
    }

    /**
     * 임시 보관 게시물 조회
     */
    public List<Post> getTemporaryStorage(Long memberId, StatusType statusType, int offset, int limit) {
        Member user = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
        return postRepository.findTemporaryStoredPosts(user.getId(), statusType, offset, limit);
    }

}
