package com.example.App.repositories.helpers;

import com.example.App.App;
import com.example.App.models.TRequestFriend;
import com.example.App.models.TUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserFriendRepositoryHelper {

    //Helpers
    public static String jsonInfoSendFriendList(String username) {
        JSONObject json = new JSONObject();
        String infoString;
        try {
            json.put("user", username);
        }catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = json.toString();

        return infoString;
    }


    public static String jsonInfoSendRequest(String username) {
        JSONObject json = new JSONObject();
        String infoString;
        try {
            json.put("userSrc", App.getInstance().getUsername());
            json.put("userDst", username);
        }catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = json.toString();

        return infoString;
    }

    public static String jsonInfoForSendFriend(String userOrigin, String userDest) {
        JSONObject json = new JSONObject();
        String infoString;
        try {
            json.put("userSrc", userOrigin);
            json.put("userDst", userDest);
        }catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = json.toString();

        return infoString;
    }

    public static String jsonInfoForDeleteFriend(String userOrigin, String userDest) {
        JSONObject json = new JSONObject();
        String infoString;
        try {
            json.put("user", userOrigin);
            json.put("friend", userDest);
        }catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = json.toString();

        return infoString;
    }

    public static List<TRequestFriend> getListFromResponse(String res) {
        JSONObject jresponse = null;
        try {
            jresponse = new JSONObject(res);

            List<TRequestFriend> requestFriendList = new ArrayList<>();
            JSONArray arrayPlaces = jresponse.getJSONArray("list");
            for (int i = 0; i < arrayPlaces.length(); i++) {
                TRequestFriend requestFriend = jsonStringToRequestFriend(arrayPlaces.getString(i));
                requestFriendList.add(requestFriend);
            }
            return requestFriendList;
        } catch (JSONException e) {
            e.printStackTrace();
            return  null;
        }
    }

    public static TRequestFriend jsonStringToRequestFriend(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            TUser userSrc = UserRepositoryHelper.jsonStringToUser(jsonObject.getJSONObject("user").toString());
            TUser userDst = App.getInstance().getSessionUser();
            return new TRequestFriend(
                    userSrc,
                    userDst,
                    jsonObject.getString("state"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
