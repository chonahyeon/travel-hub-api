package com.travelhub.travelhub_api.common.resource;

public class TravelHubResource {

    /*
     * 공통
     */
    public static final String REDIRECT_PREFIX = "redirect:";
    public static final String HOME = "/home";
    public static final String ALL_PATH_PATTERN = "/**";
    public static final String LIST = "/list";

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
    public static final String LIST_USER = "/list-user";

    /*
     * 이미지
     */
    public static final String UPLOAD = "/upload";
    public static final String BEST = "/best";

    /*
     * 스토리지
     */
    public static String STORAGE_CONFIG;
    public static final String STORAGE_DOMAIN_KEY = "storageDomain";

    /*
     * 장소
     */
    public static final String API_V1_PLACES = "/travel/v1/places";
    public static final String MAIN = "/main";

    /*
     * 게시물
     */
    public static final String API_V1_CONTENTS = "/travel/v1/contents";

    /*
     * 태그
     */
    public static final String API_V1_TAGS = "/travel/v1/tags";
}
