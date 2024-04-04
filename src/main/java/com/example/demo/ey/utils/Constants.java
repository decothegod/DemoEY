package com.example.demo.ey.utils;

public class Constants {
    public static final String USER_EXIST_MSG = "The user already registered.";
    public static final String USER_NOT_FOUND_MSG = "User not Found.";
    public static final String USER_TOKEN_NOT_FOUND_MSG = "User associated with token not found.";
    public static final String BAD_CREDENTIALS_MSG = "Password Incorrect.";
    public static final String TOKEN_EXPIRED_MSG = "Token expired. Please log in again.";
    public static final String TOKEN_MALFORMED_MSG = "Token Malformed. Invalid compact JWT string.";
    public static final String TOKEN_INVALID_MSG = "Token Invalid. JWT signature does not match locally computed signature.";
    public static final String INVALID_FORMAT_EMAIL_MSG = "The email format invalid.";
    public static final String INVALID_FORMAT_PASSWORD_MSG = "The password format invalid.";
    public static final String INVALID_PHONE_NUMBER_FORMAT_MSG = "The phone number should be just numbers. trying to parse: ";
    public static final String INVALID_CITY_CODE_FORMAT_MSG = "The city code should be just numbers. trying to parse: ";
    public static final String INTERNAL_SERVER_ERROR_MSG = "Internal Server Error, Please review the console log";

    private Constants() {
    }
}