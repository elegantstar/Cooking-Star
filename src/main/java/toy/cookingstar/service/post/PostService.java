package toy.cookingstar.service.post;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
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
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;

    /**
     * 포스트 생성
     */
    @Transactional
    public void create(PostCreateDto postCreateDto) throws IllegalArgumentException {

        Member user = memberRepository.findByUserId(postCreateDto.getUserId());

        if (user == null) {
            throw new IllegalArgumentException();
        }

        //게시물 이미지 생성
        AtomicInteger priority = new AtomicInteger(1);
        List<PostImage> postImages = postCreateDto.getStoredImages().stream()
                .map(image -> PostImage.createPostImage(image, priority.getAndIncrement()))
                .collect(Collectors.toList());

        //게시물 생성
        Post post = Post.createPost(user, postCreateDto.getContent(), postCreateDto.getStatus(), postImages);

        //게시물 저장
        postRepository.save(post);
    }

    /**
     * 게시물 단건 조회
     * @return Post 객체
     */
    public Post findById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);

        if (CollectionUtils.isEmpty(post.getPostImages())) {
            return null;
        }
        post.getMember().getUserId();
        return post;
    }

    /**
     * 유저 페이지 게시물 리스트 조회
     * @return PostImageUrlDto: 게시물 번호 리스트, 게시물 메인 이미지 URl 리스트
     */
    public PostImageUrlDto getUserPagePostImages(String userId, int page, int size, StatusType statusType) throws IllegalArgumentException {

        Member user = memberRepository.findByUserId(userId);

        if (user == null) {
            throw new IllegalArgumentException();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Direction.DESC, "id"));
        Slice<Post> pages = postRepository.findPosts(user.getId(), statusType, pageable);

        List<Long> postIds = pages.getContent().stream().map(Post::getId).collect(Collectors.toList());

        List<String> imageUrls = pages.getContent().stream()
                                                   .map(Post::getPostImages).flatMap(Collection::stream)
                                                   .filter(e -> e.getPriority() == 1)
                                                   .map(PostImage::getUrl).collect(Collectors.toList());

        return new PostImageUrlDto(postIds, imageUrls);
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
    @Transactional
    public void deletePost(String userId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);
        postImageRepository.deleteAllByPost(post);
        postRepository.delete(post);
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
        Pageable pageable = PageRequest.of(offset, limit, Sort.by(Direction.DESC, "createdDate"));

        List<Post> posts = postRepository.findPosts(user.getId(), statusType, pageable).getContent();
        posts.forEach(post -> post.getPostImages().get(0).getId());

        return posts;
    }

    public Slice<Post> getUserPagePostImageSlice(String userId, Long lastReadPostId,
                                                 int page, int size, StatusType statusType) throws Exception {

        Member user = memberRepository.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Direction.DESC, "createdDate"));

        if (lastReadPostId == null) {
            Slice<Post> slice = postRepository.findPosts(user.getId(), statusType, pageable);
            slice.forEach(post -> post.getPostImages().get(0).getUrl());
            return slice;
        }

        Slice<Post> slice = postRepository.findPostsByLastReadPostId(user.getId(), lastReadPostId, pageable, statusType);
        slice.forEach(post -> post.getPostImages().get(0).getUrl());

        return slice;
    }
}
