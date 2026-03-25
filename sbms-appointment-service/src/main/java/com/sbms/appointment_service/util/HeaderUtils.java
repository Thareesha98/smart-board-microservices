package com.sbms.appointment_service.util;


import jakarta.servlet.http.HttpServletRequest;

public class HeaderUtils {

    public static Long getUserId(HttpServletRequest request) {
        return Long.valueOf(request.getHeader("X-User-Id"));
    }

    public static String getUserRole(HttpServletRequest request) {
        return request.getHeader("X-User-Role");
    }
}
