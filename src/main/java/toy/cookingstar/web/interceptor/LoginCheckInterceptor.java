package toy.cookingstar.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

import lombok.extern.slf4j.Slf4j;
import toy.cookingstar.web.controller.login.SessionConst;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();

        log.info("인증 체크 인터셉터 실행 {}", requestURI);
        HttpSession session = request.getSession(false);

        //세션이 없거나 세션에 회원 정보가 없는 경우 미인증 사용자이므로 로그인 화면으로 리다이렉트
        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            log.info("미인증 사용자 요청");
            //로그인 후에는 다시 기존 화면으로 되돌아 올 수 있도록, 원래 요청 경로인 RequestURI를 파라미터로 전달
            response.sendRedirect("/login?redirectURL=" + requestURI);

            //미인증 사용자는 더이상 다음으로 진행하지 않고 끝낸다.
            return false;
        }
        return true;
    }
}
