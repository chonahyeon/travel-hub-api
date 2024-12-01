package com.travelhub.travelhub_api.common.resource;

public class TravelHubResource {

    /*
     * 공통
     */
    public static final String REDIRECT_PREFIX = "redirect:";
    public static final String HOME = "/home";
    public static final String ALL_PATH_PATTERN = "/**";

    /*
     * 인증
     */
    public static final String API_V1_AUTH = "/travel/v1/auth";
    public static final String AUTH_RENEW_TOKEN = "/renew";
    public static final String AUTH_LOGOUT = "/logout";
    public static final String AUTH_SIGNUP = "/signup";
    public static final String AUTH_LOGIN = "/login";

    public static final String AUTH_ACCESS_TOKEN = "accessToken";
    public static final String AUTH_REFRESH_TOKEN = "refreshToken";
}
