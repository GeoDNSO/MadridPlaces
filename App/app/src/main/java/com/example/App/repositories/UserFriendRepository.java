package com.example.App.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.App.App;
import com.example.App.networking.SimpleRequest;
import com.example.App.models.TRequestFriend;
import com.example.App.models.TUser;
import com.example.App.repositories.helpers.UserFriendRepositoryHelper;
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
    private MutableLiveData<Integer> mFriendRequest = new MutableLiveData<>();
    private MutableLiveData<Integer> mDeleteFriend = new MutableLiveData<>();
    private MutableLiveData<List<TRequestFriend>> mFriendRequestList = new MutableLiveData<>();
    private MutableLiveData<List<TRequestFriend>> mFriendList = new MutableLiveData<>();

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
            List<TRequestFriend> listaFromResponse = UserFriendRepositoryHelper.getListFromResponse(res);
            if(listaFromResponse == null){
                Log.d("PLACE_REPO", "La lista JSON convertida es NULO, MIRAR...");
                return;
            }
            if(listaFromResponse.isEmpty()){
                return;
            }
            if (listaAux == null){
                friendList.postValue(UserFriendRepositoryHelper.getListFromResponse(res));
            }
            else{
                listaAux.addAll(listaFromResponse);
                friendList.postValue(listaAux);
            }
            mSuccess.postValue(AppConstants.LIST_REQ_FRIEND_OK);//Importante que este despues del postValue de friendList
        }
    }

    public void deleteFriend(String userToDelete, String currentUser) {
        String postBodyString = UserFriendRepositoryHelper.jsonInfoForDeleteFriend(userToDelete, currentUser);

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest(
                postBodyString,
                AppConstants.METHOD_DELETE, "/friends/deleteFriend"
        );

        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mDeleteFriend.postValue(AppConstants.DELETE_FRIEND_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(!response.isSuccessful()) {
                    mDeleteFriend.postValue(AppConstants.DELETE_FRIEND_FAIL);
                    throw new IOException("Unexpected code " + response);
                }

                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if(success) {
                    mDeleteFriend.postValue(AppConstants.DELETE_FRIEND_SUCCESS);
                }
                else {
                    mDeleteFriend.postValue(AppConstants.DELETE_FRIEND_FAIL);
                }
            }
        });
    }

    public void friendList(String username) {
        String postBodyString = UserFriendRepositoryHelper.jsonInfoSendFriendList(username);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/friends/listFriends");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new UserFriendRepository.FriendListCallBack(simpleRequest, mFriendList));
    }

    public void declineFriendRequest(String userOrigin, String userDest) {
        String postBodyString = UserFriendRepositoryHelper.jsonInfoForSendFriend(userOrigin, userDest);

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
        String postBodyString = UserFriendRepositoryHelper.jsonInfoForSendFriend(userOrigin, userDest);

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
        String postBodyString = UserFriendRepositoryHelper.jsonInfoSendFriendList(username);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/friends/listFriendRequests");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new UserFriendRepository.FriendListCallBack(simpleRequest, mFriendRequestList));
    }

    public void sendFriendRequest(String username){
        String postBodyString = UserFriendRepositoryHelper.jsonInfoSendRequest(username);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/friends/sendRequest");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mFriendRequest.postValue(AppConstants.REQ_FRIEND_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(!response.isSuccessful()) {
                    mFriendRequest.postValue(AppConstants.REQ_FRIEND_FAIL);
                    throw new IOException("Unexpected code " + response);
                }

                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if(success) {
                    mFriendRequest.postValue(AppConstants.REQ_FRIEND_OK);
                }
                else {
                    mFriendRequest.postValue(AppConstants.REQ_FRIEND_FAIL);
                }
            }
        });
    }

    //Mutables getter
    public MutableLiveData<Integer> getmAcceptFriend() {
        return mAcceptFriend;
    }

    public MutableLiveData<Integer> getmDeclineFriend() {
        return mDeclineFriend;
    }

    public MutableLiveData<List<TRequestFriend>> getmFriendRequestList() {
        return mFriendRequestList;
    }

    public MutableLiveData<List<TRequestFriend>> getmFriendList() {
        return mFriendList;
    }

    public MutableLiveData<Integer> getmFriendRequest() {
        return mFriendRequest;
    }

    public MutableLiveData<Integer> getmDeleteFriend() {
        return mDeleteFriend;
    }
}
