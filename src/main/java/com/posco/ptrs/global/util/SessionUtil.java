package com.posco.ptrs.global.util;

import com.posco.ptrs.domain.user.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SessionUtil {
    
    private static final String USER_SESSION_KEY = "loginUser";
    
    public static void setLoginUser(HttpSession session, User user) {
        session.setAttribute(USER_SESSION_KEY, user);
    }
    
    public static Optional<User> getLoginUser(HttpSession session) {
        return Optional.ofNullable((User) session.getAttribute(USER_SESSION_KEY));
    }
    
    public static boolean isLoggedIn(HttpSession session) {
        return session.getAttribute(USER_SESSION_KEY) != null;
    }
    
    public static void logout(HttpSession session) {
        session.removeAttribute(USER_SESSION_KEY);
        session.invalidate();
    }
}