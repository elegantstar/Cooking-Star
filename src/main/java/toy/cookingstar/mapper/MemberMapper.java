package toy.cookingstar.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import toy.cookingstar.domain.Member;
import toy.cookingstar.mybatisservice.user.UserUpdateParam;

@Mapper
public interface MemberMapper {

    Member findByUserId(String userId);

    Member findByEmail(String email);

    Member findById(Long id);

    void save(Member member);

    void updateInfo(UserUpdateParam userUpdateParam);

    void updatePwd(@Param("userId") String userId, @Param("newPassword") String newPassword, @Param("newSalt") String newSalt);

    List<Member> findSearchHistoryById(Long memberId);

    List<Member> findByKeyword(String keyword);

    void deleteProfileImage(Long id);

    void updateProfileImage(@Param("id") Long id, @Param("profileImage") String profileImage);
}
