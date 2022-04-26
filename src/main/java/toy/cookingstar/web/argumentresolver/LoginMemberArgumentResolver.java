package toy.cookingstar.web.argumentresolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import toy.cookingstar.entity.Member;
import toy.cookingstar.web.controller.login.SessionConst;

public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    // @Login 애노테이션이 존재하면서 Member 타입이면 해당 ArgumentResolver 실행
    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType());

        return hasLoginAnnotation && hasMemberType;
    }

    // 컨트롤러 호출 직전에 호출. 필요한 파라미터 정보 생성 -> 세션에 보관 중인 로그인 회원 정보(member 객체) 반환.
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);

        return (session != null) ? session.getAttribute(SessionConst.LOGIN_MEMBER) : null;
    }
}
