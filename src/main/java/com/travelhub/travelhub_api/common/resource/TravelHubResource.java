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
    public static final String API_V1_REVIEW = "/travel/v1/review";
    public static final String API_V1_IMAGE = "/travel/v1/image";

    public static final String AUTH_RENEW_TOKEN = "/renew";
    public static final String AUTH_LOGOUT = "/logout";
    public static final String AUTH_SIGNUP = "/signup";
    public static final String AUTH_LOGIN = "/login";

    public static final String AUTH_ACCESS_TOKEN = "accessToken";
    public static final String AUTH_REFRESH_TOKEN = "refreshToken";

    public static final String LIST = "/list";
    public static final String LIST_USER = "/list-user";

    public static final String UPLOAD = "/upload";

    /*
     * 스토리지
     */
    public static String STORAGE_CONFIG;
    public static final String STORAGE_DOMAIN_KEY = "domain";

    /*
     * 장소
     */
    public static final String API_V1_PLACES = "/travel/v1/places";

    /*
     * 게시물
     */
    public static final String API_V1_CONTENTS = "/travel/v1/contents";
}
