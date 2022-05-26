package toy.cookingstar.service.post;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import toy.cookingstar.common.CustomSlice;
import toy.cookingstar.web.controller.post.dto.PostAndImageUrlDto;

import static toy.cookingstar.common.RedisCacheSets.POST_CACHE;

@RequiredArgsConstructor
@Service
public class PostCacheService {
    private final PostService postService;

    @Cacheable(cacheNames = POST_CACHE, key = "#userId + #statusType + #lastReadPostId + #isMyPage",
            condition = "not #isMyPage or (#lastReadPostId == null and #isMyPage)")
    public CustomSlice<PostAndImageUrlDto> getUserPagePostImageSlice(String userId, Long lastReadPostId,
                                                                     int size, StatusType statusType, boolean isMyPage) {

        return CustomSlice.convertFromSlice(
                postService.getUserPagePostImageSlice(userId, lastReadPostId, size, statusType)
                        .map(PostAndImageUrlDto::of));
    }
}