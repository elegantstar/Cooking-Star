package toy.cookingstar.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import toy.cookingstar.common.RedisCacheSets;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    /**
     * Spring Redis는 org.springframework.data.redis.cache 패키지를 통해 Spring Cache 추상화를 구현할 수 있다.
     *
     * RedisCacheManager를 Configuration에 추가하여 Redis를 사용할 수 있다.
     * RedisCacheManager의 동작은 RedisCacheManagerBuilder로 Configure 할 수 있고,
     * default RedisCacheConfiguration, 트랜잭션 동작, 및 미리 정의된 캐시를 설정할 수 있다.
     *
     * RedisCacheManager를 사용하면 캐시마다 Configuration을 정의할 수 있다.
     * RedisCacheManager로 생성된 RedisCache의 동작은 RedisCacheConfiguration으로 정의된다.
     *
     * Configuration을 사용하면 binary storage format으로 변환하거나, 변환을 위한 key 만료 시간, prefix, RedisSerializer 구현을 설정할 수 있다.
     *
     * Spring Data에서 사용자 커스텀 타입과 raw data 간의 변환은 org.springframework.data.redis.serializer 패키지의 Redis에서 처리된다.
     * Serialization 프로세스는 두 가지 타입의 Serializer로 처리된다.
     *   RedisSerializer 기반의 양방향 Serializer / RedisElementReader 및 RedisElementWriter를 사용하는 element reader와 element writer
     * RedisSerializer는 우선적으로 byte[]로 직렬화하는 반면, reader & writer는 ByteBuffer를 사용한다.
     *
     * Serializer는 복합적으로 구현할 수 있다.
     * - RedisCache와 RedisTemplate에 default로 사용되는 JdkSerializationRedisSerializer
     * - StringRedisSerializer (String의 key를 byte[](UTF-8)로 변환)
     * JSON 형식으로 데이터를 저장하고자 한다면?
     * - Jackson2JsonRedisSerializer 또는 GenericJackson2JsonRedisSerializer 를 사용할 수 있다.
     *
     * 저장 형식은 value에만 국한되는 것은 아니고, key, value, hash에 아무 제약 없이 사용할 수 있다.
     */
    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper())));

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory())
                .withCacheConfiguration(RedisCacheSets.POST_CACHE, redisCacheConfiguration.entryTtl(Duration.ofMinutes(5)))
                .build();
    }


    private ObjectMapper objectMapper() {
        /*
         * Deserialization 하는 과정에서 변환될 타입을 직접적으로 명시하지 않는 경우, LinkedHashMap으로 변환을 시도함
         * PolymorphicTypeValidator는 Serialization 되는 객체 타입을 넣어줌으로써 원래 객체 타입으로 변환될 수 있게 만듦.
         * 해당 ObjectMapper를 Bean으로 등록 시, 모든 ObjectMapper를 해당 Bean으로 대체하게 되므로 Bean 등록은 하면 안 됨!
         */
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator
                .builder()
                .allowIfSubType(Object.class)
                .build();

        return new ObjectMapper()
                .findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModules(new JavaTimeModule(), new Hibernate5Module())
                .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
    }
}
