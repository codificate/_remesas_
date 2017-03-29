package net.iantech.android.remitee.enums;

import com.android.volley.Request;

/**
 * Created by andres on 27/03/17.
 */

public enum ApiUrl {
    LOGIN("/login", Request.Method.POST),
    FORGOT_PASSWORD("/user/send-password-token",Request.Method.POST),
    REGISTER("/register", Request.Method.POST),
    UPLOAD_PROFILE_PICTURE("/user/uploadpicture",Request.Method.POST),
    PROFILE_PICTURE("/userProfile/",Request.Method.POST),
    LICENSE_PICTURE("/licenseProfile/",Request.Method.POST),
    LOGOUT("/logout", Request.Method.POST),
    EDIT_PROFILE("/user/",Request.Method.PUT),
    GET_USER("/user/",Request.Method.GET),
    GET_CITY("/city",Request.Method.GET),
    UPLOAD_REQUEST_PICTURE("/upload-license",Request.Method.POST),
    GET_SUMMARY("/summary",Request.Method.POST),
    CREATE_CARD("/stripe/create-card",Request.Method.POST),
    GET_ALL_CARD("/stripe/customer",Request.Method.GET),
    SENT_RENTAL("/rental-request",Request.Method.POST),
    GET_WEATHER_STORY("http://api.openweathermap.org/data/2.5/forecast/daily", Request.Method.GET ),
    GET_CURRENT_REQUEST("/current-request",Request.Method.GET),
    GET_LIST_REQUEST("/rental-requests",Request.Method.GET),
    GET_ACCEPTED_REQUEST("/accepted-request",Request.Method.GET),
    CHANGE_STATUS_REQUEST("/change-request-status/",Request.Method.POST),
    DELETE_CARD_1("/stripe/customer/",Request.Method.DELETE),
    DELETE_CARD_2("/delete-card/",Request.Method.DELETE),
    VALIDATE_PHONE_NUMBER("http://apilayer.net/api/validate", Request.Method.GET);

    public String url;

    public int method;

    ApiUrl(String inUrl, int inMethod){
        url = inUrl;
        method = inMethod;
    }
}