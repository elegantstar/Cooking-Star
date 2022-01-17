package toy.cookingstar.config;


import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// @Mapper 인터페이스 패키지 위치 선언
@Configuration
@MapperScan(value = "toy.cookingstar.repository")
@EnableTransactionManagement
public class MybatisConfig {

    /**
     * @param dataSource : DB 접속을 위한 계정, DB 드라이버, 패스워드 등의 설정 정보
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {

        // SqlSessionFactoryBean 객체 생성
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();

        // SqlSessionFactoryBean 객체의 set 메소드를 사용하여 설정 사항 세팅
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        sessionFactory.setTypeAliasesPackage("toy.cookingstar.domain");

        return sessionFactory.getObject();
    }

    /**
     * Spring Mybatis SqlSessionTemplate 설정
     * @param sqlSessionFactory
     * @return
     */
    @Bean
    public SqlSessionTemplate sqlSession(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
