package toy.cookingstar.querytest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StopWatch;
import toy.cookingstar.repository.MemberRepository;
import toy.cookingstar.service.search.dto.UserSearchDto;

import java.util.List;
import java.util.stream.Collectors;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserSearchLikeTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void likeTest() throws Exception {
        //given
        Pageable pageable = PageRequest.of(0, 50);

        //when
        for (int i = 0; i < 10; i++) {
            StopWatch stopWatch = new StopWatch();

            stopWatch.start();

            List<UserSearchDto> userSearchDtos = memberRepository.findByKeyword("neighbor", pageable)
                    .map(UserSearchDto::of)
                    .getContent();

            for (UserSearchDto dto : userSearchDtos) {
                if (dto.getProfileImage() != null) {
                    String dir = dto.getProfileImage().substring(0, 10);
                    dto.setProfileImage("https://d9voyddk1ma4s.cloudfront.net/images/profile/" + dir + "/" + dto.getProfileImage());
                }
            }

            stopWatch.stop();

            //then
            System.out.println("[time elapsed - " + i + "]: " + stopWatch.getTotalTimeMillis() + "ms");
        }
    }

    @Test
    void FTSTest() throws Exception {
        //given
        Pageable pageable = PageRequest.of(0, 50);

        //when
        for (int i = 0; i < 10; i++) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            List<UserSearchDto> userSearchDtos = memberRepository.findByFullTextSearch("+\"ne\" +\"ei\" +\"ig\" +\"gh\" +\"hb\" +\"bo\" +\"or\"", pageable)
                    .stream()
                    .map(UserSearchDto::of)
                    .collect(Collectors.toList());

            for (UserSearchDto dto : userSearchDtos) {
                if (dto.getProfileImage() != null) {
                    String dir = dto.getProfileImage().substring(0, 10);
                    dto.setProfileImage("https://d9voyddk1ma4s.cloudfront.net/images/profile/" + dir + "/" + dto.getProfileImage());
                }
            }

            stopWatch.stop();

            //then
            System.out.println("[time elapsed - " + i + "]: " + stopWatch.getTotalTimeMillis() + "ms");
        }
    }
}
