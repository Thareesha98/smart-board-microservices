package com.sbms.boarding_service.util;

import jakarta.servlet.http.HttpServletRequest;

public class HeaderUtil {

    public static Long userId(HttpServletRequest request) {
        return Long.parseLong(request.getHeader("X-User-Id"));
    }

    public static String role(HttpServletRequest request) {
        return request.getHeader("X-User-Role");
    }
}
