package com.travelhub.travelhub_api.data.dto.auth;

public class LoginUser {
    private static final ThreadLocal<String> userThreadLocal = new ThreadLocal<>();

    public static void set(String usId) {
        userThreadLocal.set(usId);
    }

    public static String get() {
        return userThreadLocal.get();
    }

    public static void remove() {
        userThreadLocal.remove();
    }
}
