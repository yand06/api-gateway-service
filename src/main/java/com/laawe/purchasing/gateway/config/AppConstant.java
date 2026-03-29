package com.laawe.purchasing.gateway.config;

public class AppConstant {

    public static final String LOGIN_API = "/api/v1/auth/login";
    public static final String REGISTER_API = "/api/v1/auth/register";
    public static final String API_VENDOR_ALL = "/api/vendor/**";
    public static final String API_PURCHASING_ALL = "/api/purchasing/**";
    public static final String ROUTE_AUTH_SERVICE = "auth-service";
    public static final String ROUTE_VENDOR_SERVICE = "vendor-service";
    public static final String ROUTE_PURCHASING_SERVICE = "purchasing-service";
    public static final String URI_AUTH_SERVICE = "http://localhost:8081";
    public static final String URI_VENDOR_SERVICE = "http://localhost:8082";
    public static final String URI_PURCHASING_SERVICE = "http://localhost:8083";

    public static final String X_USER_ID = "X-User-ID";
    public static final String X_USER_NAME = "X-User-Name";
    public static final String X_USER_ROLES = "X-User-Roles";

    public static final String EMPTY = "";
    public static final String COMMA = ",";
    public static final String USERNAME = "username";
    public static final String USER_ID = "user_id";

    public static final String ALGORITHM = "HmacSHA256";
}
