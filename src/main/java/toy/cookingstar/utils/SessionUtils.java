package toy.cookingstar.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import toy.cookingstar.domain.Member;
import toy.cookingstar.web.controller.login.SessionConst;

public class SessionUtils {

    public static void refreshMember(Member loginMember, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
    }
}

