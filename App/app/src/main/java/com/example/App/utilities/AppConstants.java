package com.example.App.utilities;

public class AppConstants {

    public static final String BUNDLE_PLACE_DETAILS = "PLACE_DETAIL_BUNDLE";
    public static final String BUNDLE_PLACE_NAME_PLACE_DETAILS = "PLACE_DETAIL_BUNDLE_PLACE_NAME";

    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_REQUEST = "REQUEST";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_DELETE = "DELETE";

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String BIRTH_DATE = "birth";
    public static final String CITY = "city";
    public static final String EMAIL = "email";
    public static final String GENDER = "gender";
    public static final String PROFILE_PICTURE = "profile_picture";


    public static final String LOGGED = "login";
    public static final String ADMIN = "admin";
    public static final String USER_ROL_ADMIN = "admin";
    public static final String USER_ROL_USER = "user";


    public static final String RETURN_OK = "ok";

    public static final Integer ERROR_PROFILE = -1;
    public static final Integer DELETE_PROFILE = 1;
    public static final Integer MODIFY_PROFILE = 2;
    public static final Integer LIST_USERS = 3;
    public static final Integer ERROR_LIST_USERS = -3;

    public static final Integer ERROR_LIST_PLACES = -4;
    public static final Integer LIST_PLACES = 4;

    public static final Integer ERROR_LIST_COMMENTS = -5;
    public static final Integer LIST_COMMENTS = 5;
    public static final Integer ERROR_NEW_COMMENT = -6;
    public static final Integer NEW_COMMENT = 6;

    public static final Integer ERROR_ADD_PLACE = -7;
    public static final Integer ADD_PLACE = 7;

    public static final Integer ERROR_DETAIL_PLACE = -8;
    public static final Integer DELETE_PLACE = 8;

    public static final Integer ERROR_MODIFY_PLACE = -9;
    public static final Integer MODIFY_PLACE = 9;

    public static final Integer ERROR_GET_CATEGORIES = -10;
    public static final Integer GET_CATEGORIES = 10;

    public static final Integer REQUEST_STORAGE = 1;

    public static final String BUNDLE_PROFILE_LIST_DETAILS = "DETAILS_LIST_PROFILE_BUNDLE";

    public static final String BROWSER_LIST_TYPE_PLACES = "TYPE_PLACES_BROWSER_LIST";
    public static final String BROWSER_SEARCH_PLACE_NAME = "SEARCH_PLACE_NAME_BROWSER";

    public static final int PLACE_NEAREST = 1;
    public static final int PLACE_RATING = 2;
    public static final int PLACE_POPULAR = 3;
    public static final int PLACE_CATEGORIES = 4;

    public static final String BUNDLE_CATEGORY_TYPE = "CATEGORIA";
    public static final Integer DELETE_COMMENT_OK = 340;
    public static final Integer ERROR_DELETE_COMMENT = -340;
    public static final Integer VISITED_POST_FAIL = -341;
    public static final Integer VISITED_POST_OK = 341;



    public static String TAB_RATING = "W";
    public static String TAB_NEAREST = "W";
    public static String TAB_TWITTER = "W";
    public static String TAB_CATEGORY = "W";

    public static final int NO_SORT = 0;
    public static final int SORT_UP = 1;
    public static final int SORT_DOWN = 2;

    public static final int FINAL_NO_SORT = 0;
    public static final int NICKNAME_SORT_UP = 1;
    public static final int NICKNAME_SORT_DOWN = 2;
    public static final int NAME_SORT_UP = 3;
    public static final int NAME_SORT_DOWN = 4;

    
    public static final Integer FAV_POST_OK = 1;
    public static final Integer FAV_POST_FAIL = 0;


    public static final Integer LONGITUDE = 0;
    public static final Integer LATITUDE = 1;


    public static final Double DEFAULT_RADIUS = 2000.0;
    public static final Integer DEFAULT_NPLACES = 20;

    public static final Integer SEND_REC_OK = 0;
    public static final Integer SEND_REC_FAIL = 1;

    public static final Integer LIST_REC_OK = 0;
    public static final Integer LIST_REC_FAIL = 1;

    public static final Integer PENDING_REC_FAIL = 0;
    public static final Integer ACCEPT_REC_OK = 1;
    public static final Integer DENY_REC_OK = 2;

    public static final int RESULT_SPEECH = 1;

    public static final int STATIC_INTEGER_MAPBOX_ADD = 5;
    public static final String STATIC_STRING_MAPBOX_ADD_DATA = "10";
    public static final String STATE_PENDING = "P";
    public static final String STATE_ACCEPTED = "A";

    public static final String STATE_VISITED = "V";
}