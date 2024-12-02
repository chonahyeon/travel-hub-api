package com.travelhub.travelhub_api.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

public class CookieUtil {

    public static void setCookie(HttpServletResponse response, Duration maxAge, String key, String value) {
        ResponseCookie responseCookie =  ResponseCookie.from(key, value)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(maxAge)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    public static Optional<String> findCookie(Cookie[] cookies, String key) {
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(key))
                .map(Cookie::getValue)
                .findAny();
    }
}
