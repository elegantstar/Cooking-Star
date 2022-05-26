package toy.cookingstar.web.interceptor;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    public static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        if (1 == 1) return true;
        String requestURI = request.getRequestURI();

        String logId = UUID.randomUUID().toString();
        request.setAttribute(LOG_ID, logId);

        //@Controller, @RequestMapping을 활용한 핸들러 매핑을 사용하는 경우, 핸들러 정보로 HandlerMethod가 넘어옴.
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler; //호출할 컨트롤러 메서드의 모든 정보를 포함하고 있음.
        }

        log.info("REQUEST [{}][{}][{}]", logId, requestURI, handler);

        return true; //false인 경우 더이상 다음 인터셉터나 컨트롤러가 호출되지 않음.
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
//        if (1 == 1) return;
        // 예외가 발생할 경우 postHandle은 호출되지 않음.
        log.info("REQUEST [{}]", modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        String requestURI = request.getRequestURI();
        String logId = (String) request.getAttribute(LOG_ID);

        // 종료 로그. 예외 발생 시에도 호출 보장.
        log.info("RESPONSE [{}][{}]", logId, requestURI);

        if (ex != null) {
            log.error("afterCompletion error!!", ex);
        }

    }
}
