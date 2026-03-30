package com.laawe.purchasing.gateway.config.constant;

public class AppConstant {

    public static final String BASE_API_URL = "/api/v1/auth";

    public static final String LOGIN_API = "/user/login";
    public static final String REGISTER_API = "/user/register";
    public static final String REFRESH_TOKEN_API = "/user/refresh-token";

    public static final String X_USER_ID = "X-User-ID";
    public static final String X_USER_NAME = "X-User-Name";
    public static final String X_USER_ROLES = "X-User-Roles";

    public static final String EMPTY = "";
    public static final String COMMA = ",";
    public static final String USERNAME = "username";
    public static final String USER_ID = "user_id";

    public static final String ALGORITHM = "HmacSHA256";
    public static final String BL_PREFIX = "BLACKLIST:";
    public static final String HEADER_BEARER = "Bearer ";
    public static final String SUCCESS_STATUS = "SUCCESS";
    public static final String ERROR_STATUS = "ERROR";
}
