package com.example.App.models.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.App.models.dao.SimpleRequest;
import com.example.App.models.transfer.TRequestFriend;
import com.example.App.models.transfer.TUser;
import com.example.App.utilities.AppConstants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class UserFriendRepository extends Repository{
    private MutableLiveData<Integer> mAcceptFriend = new MutableLiveData<>();
    private MutableLiveData<Integer> mDeclineFriend = new MutableLiveData<>();
    private MutableLiveData<List<TRequestFriend>> mFriendRequestList = new MutableLiveData<>();

    class FriendListCallBack implements Callback {

        private SimpleRequest simpleRequest;
        private MutableLiveData<List<TRequestFriend>> friendList;


        public FriendListCallBack(SimpleRequest simpleRequest, MutableLiveData<List<TRequestFriend>> friendList){
            this.simpleRequest = simpleRequest;
            this.friendList = friendList;
        }

        private void sleep(long milis){
            try {
                Thread.sleep(milis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            e.printStackTrace();
            mSuccess.postValue(AppConstants.LIST_REC_FAIL);
            friendList.postValue(null);
            call.cancel();
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            this.sleep(500);//Para simular la carga...

            if (!response.isSuccessful()) {
                mSuccess.postValue(AppConstants.LIST_REC_FAIL);
                throw new IOException("Unexpected code " + response);
            }
            String res = response.body().string();
            boolean success = simpleRequest.isSuccessful(res);

            if(!success){
                friendList.postValue(null);
                mSuccess.postValue(AppConstants.LIST_REQ_FRIEND_FAIL);//Importante que este despues del postValue de mUser

                return;
            }
            //si no hubo problemas...
            List<TRequestFriend> listaAux = friendList.getValue();
            List<TRequestFriend> listaFromResponse = getListFromResponse(res);
            if(listaFromResponse == null){
                Log.d("PLACE_REPO", "La lista JSON convertida es NULO, MIRAR...");
                return;
            }
            if(listaFromResponse.isEmpty()){
                return;
            }
            if (listaAux == null){
                friendList.postValue(getListFromResponse(res));
            }
            else{
                listaAux.addAll(listaFromResponse);
                friendList.postValue(listaAux);
            }
            mSuccess.postValue(AppConstants.LIST_REQ_FRIEND_OK);//Importante que este despues del postValue de friendList
        }
    }

    public void declineFriendRequest(String userOrigin, String userDest) {
        String postBodyString = jsonInfoForSendFriend(userOrigin, userDest);

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest(
                postBodyString,
                AppConstants.METHOD_DELETE, "/friends/refuseRequest"
        );

        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mDeclineFriend.postValue(AppConstants.PENDING_REQ_FRIEND_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(!response.isSuccessful()) {
                    mDeclineFriend.postValue(AppConstants.PENDING_REQ_FRIEND_FAIL);
                    throw new IOException("Unexpected code " + response);
                }

                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if(success) {
                    mDeclineFriend.postValue(AppConstants.DECLINE_REQ_FRIEND_OK);
                }
                else {
                    mDeclineFriend.postValue(AppConstants.PENDING_REQ_FRIEND_FAIL);
                }
            }
        });
    }

    public void acceptFriendRequest(String userOrigin, String userDest) {
        String postBodyString = jsonInfoForSendFriend(userOrigin, userDest);

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest(
                postBodyString,
                AppConstants.METHOD_POST, "/friends/acceptRequest"
        );

        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mAcceptFriend.postValue(AppConstants.PENDING_REQ_FRIEND_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(!response.isSuccessful()) {
                    mAcceptFriend.postValue(AppConstants.PENDING_REQ_FRIEND_FAIL);
                    throw new IOException("Unexpected code " + response);
                }

                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if(success) {
                    mAcceptFriend.postValue(AppConstants.ACCEPT_REQ_FRIEND_OK);
                }
                else {
                    mAcceptFriend.postValue(AppConstants.PENDING_REQ_FRIEND_FAIL);
                }
            }
        });
    }

    public void friendRequestList(String username) {
        String postBodyString = jsonInfoSendFriendList(username);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/recommendations/listRecommendationsSent");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new UserFriendRepository.FriendListCallBack(simpleRequest, mFriendRequestList));
    }

    private String jsonInfoSendFriendList(String username) {
        JSONObject json = new JSONObject();
        String infoString;
        try {
            json.put("username", username);
        }catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = json.toString();

        return infoString;
    }

    private String jsonInfoForSendFriend(String userOrigin, String userDest) {
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

    private List<TRequestFriend> getListFromResponse(String res) {
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

    private TRequestFriend jsonStringToRequestFriend(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            TUser userSrc = jsonStringToUser(jsonObject.toString());
            TUser userDst = jsonStringToUser(jsonObject.toString());

            return new TRequestFriend(
                    userSrc,
                    userDst,
                    jsonObject.getString("state"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //TODO ESTA REPETIDO
    private TUser jsonStringToUser(String jsonString){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            String image_profile = jsonObject.getString("profile_image");
            if(image_profile.equals("null")){
                image_profile = null;
            }
            return new TUser(jsonObject.getString("nickname"), jsonObject.getString("password")/*antes estaba con ""*/, jsonObject.getString("name"), jsonObject.getString("surname"), jsonObject.getString("email"), jsonObject.getString("gender"), jsonObject.getString("birth_date"), jsonObject.getString("city"), jsonObject.getString("rol"), image_profile);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public MutableLiveData<Integer> getmAcceptFriend() {
        return mAcceptFriend;
    }

    public MutableLiveData<Integer> getmDeclineFriend() {
        return mDeclineFriend;
    }

    public MutableLiveData<List<TRequestFriend>> getmFriendRequestList() {
        return mFriendRequestList;
    }


}
