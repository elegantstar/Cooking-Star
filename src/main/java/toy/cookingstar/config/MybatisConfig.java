package toy.cookingstar.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.RequiredArgsConstructor;
import toy.cookingstar.service.post.StatusType;

// @Mapper 인터페이스 패키지 위치 선언
@Configuration
@MapperScan(basePackages = {"toy.cookingstar.mapper"},
        sqlSessionFactoryRef = "sqlSessionFactory", sqlSessionTemplateRef = "sqlSessionTemplate")
@EnableTransactionManagement
@RequiredArgsConstructor
public class MybatisConfig {

    private final ApplicationContext applicationContext;

    /**
     * @param dataSource : DB 접속을 위한 계정, DB 드라이버, 패스워드 등의 설정 정보
     */
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {

        // SqlSessionFactoryBean 객체 생성
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();

        // SqlSessionFactoryBean 객체의 set 메소드를 사용하여 설정 사항 세팅
        sessionFactory.setDataSource(dataSource);

        sessionFactory.setMapperLocations(applicationContext.getResource("classpath:mapper/*.xml"));
        sessionFactory.setTypeAliasesPackage("toy.cookingstar.domain");
        sessionFactory.setConfigLocation(applicationContext.getResource("classpath:mybatis-config.xml"));

        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        sessionFactory.setTypeAliasesPackage("toy.cookingstar.domain");
        sessionFactory.setTypeHandlers(new StatusType.TypeHandler());

        return sessionFactory.getObject();
    }

    /**
     * Spring Mybatis SqlSessionTemplate 설정
     * @param sqlSessionFactory
     * @return
     */
    @Bean(name="sqlSessionTemplate")
    public SqlSessionTemplate sqlSession(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
