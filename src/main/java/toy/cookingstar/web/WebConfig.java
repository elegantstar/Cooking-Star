package toy.cookingstar.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import toy.cookingstar.web.argumentresolver.LoginMemberArgumentResolver;
import toy.cookingstar.web.interceptor.LogInterceptor;
import toy.cookingstar.web.interceptor.LoginCheckInterceptor;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        CacheControl cacheControl = CacheControl
//                .maxAge(60 * 60 * 24, TimeUnit.SECONDS)
//                .noTransform()
//                .mustRevalidate();
//
//        registry.addResourceHandler("resources/**")
//                .addResourceLocations("classpath:/static/")
//                .setCacheControl(cacheControl);
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error");

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/member/join", "/login", "/logout", "/member/welcome",
                                     "/images/**", "/css/**", "/*.ico", "/error");
    }
}
