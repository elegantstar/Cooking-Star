package toy.cookingstar.service.liker;

import org.springframework.data.domain.Slice;
import toy.cookingstar.entity.Member;

public interface LikerService {

    void create(Long loginMemberId, Long entityId);

    int countLikers(Long entityId);

    Slice<Member> getLikers(Long entityId, int page, int size);

    boolean checkForLikes(Long loginMemberId, Long entityId);

    void deleteLiker(Long loginMemberId, Long entityId);
}
