package com.example.App.repositories.helpers;

import com.example.App.models.TComment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommentRepositoryHelper {

    public static String rateToString(String user, String placeName, float rate) {
        double rateDouble = rate;
        JSONObject jsonName = new JSONObject();
        String infoString;
        try {
            jsonName.put("user", user);
            jsonName.put("location", placeName);
            jsonName.put("rate", rateDouble);
        } catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = jsonName.toString();
        return infoString;
    }

    public static String commentToString(String userName, String content, String placeName, float rate){
        double rateDouble = rate;
        JSONObject jsonName = new JSONObject();
        String infoString;
        try {
            jsonName.put("location", placeName);
            jsonName.put("user", userName);
            jsonName.put("comment", content);
            jsonName.put("rate", rateDouble);
        } catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = jsonName.toString();
        return infoString;
    }
    public static String dataToString(String placeName, int page, int quant) {
        JSONObject jsonName = new JSONObject();
        String infoString;
        try {
            jsonName.put("location", placeName);
            jsonName.put("page", page);
            jsonName.put("quant", quant);
        } catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = jsonName.toString();
        return infoString;
    }

    public static List<TComment> getListFromResponse(String res) {
        JSONObject jresponse = null;
        try {
            jresponse = new JSONObject(res);

            List<TComment> listOfComments = new ArrayList<>();
            JSONArray arrayComments = jresponse.getJSONArray("listComments");
            for (int i = 0; i < arrayComments.length(); i++) {
                TComment tComment = jsonStringToComment(arrayComments.getString(i));
                String content = tComment.getContent();
                if(content != null && content != "null" && content != "")//Para no añadir comentarios nulos
                    listOfComments.add(tComment);
            }
            return listOfComments;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static TComment jsonStringToComment(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            String image_profile = jsonObject.getString("profile_image");
            if(image_profile.equals("null")){
                image_profile = null;
            }
            String comment = jsonObject.getString("comment");
            if(comment.equals("null")){
                comment = "";
            }
            return new TComment(
                    jsonObject.getInt("id_comment"),
                    image_profile,
                    jsonObject.getString("user"),
                    comment,
                    jsonObject.getString("created"),
                    jsonObject.getDouble("rate"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void searchAndDeleteList(List<TComment> lista, TComment newComment){
        boolean ok = false;

        int i = 0;

        for (TComment comment : lista) {

            if(comment.getUsername().equals(newComment.getUsername())){
                ok = true;
                break;
            }
            i++;
        }
        if(ok) { //Si no existe el comentario, se agrega
            lista.remove(i);
        }
    }

    public static String deleteToString(int id_comment) {
        JSONObject jsonName = new JSONObject();
        String infoString;
        try {
            jsonName.put("id_comment", id_comment);
        } catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = jsonName.toString();
        return infoString;
    }
}
