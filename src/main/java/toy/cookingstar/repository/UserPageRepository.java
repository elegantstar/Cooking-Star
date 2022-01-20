package toy.cookingstar.repository;

import org.apache.ibatis.annotations.Mapper;

import toy.cookingstar.domain.dto.UserPageDto;

@Mapper
public interface UserPageRepository {

    boolean findUserByUserId(String userId);

    UserPageDto findUserPageInfoByUserId(String userId);
}
