package com.webshopping.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;

public final class WebSecurityUtil {
    private static final String CSRF_TOKEN_KEY = "CSRF_TOKEN";

    private WebSecurityUtil() {
    }

    public static String csrfToken(HttpSession session) {
        Object tokenObj = session.getAttribute(CSRF_TOKEN_KEY);
        if (tokenObj instanceof String) {
            String token = (String) tokenObj;
            if (!token.trim().isEmpty()) {
                return token;
            }
        }
        String generated = UUID.randomUUID().toString();
        session.setAttribute(CSRF_TOKEN_KEY, generated);
        return generated;
    }

    public static boolean verifyCsrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        String sessionToken = (String) session.getAttribute(CSRF_TOKEN_KEY);
        String requestToken = request.getParameter("csrfToken");
        return sessionToken != null && !sessionToken.isEmpty() && sessionToken.equals(requestToken);
    }
}
