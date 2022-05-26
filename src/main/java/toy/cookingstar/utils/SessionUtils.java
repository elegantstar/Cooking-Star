package toy.cookingstar.utils;

import toy.cookingstar.entity.Member;
import toy.cookingstar.web.controller.login.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtils {

    public static void refreshMember(Member loginMember, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
    }
}