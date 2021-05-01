package com.example.App.ui.friends.subclasses.friend_resquest_list;

import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.App.App;
import com.example.App.R;
import com.example.App.models.transfer.TRequestFriend;
import com.example.App.ui.friends.FriendsViewModel;
import com.example.App.utilities.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestListFragment extends Fragment implements FriendRequestListAdapter.OnFriendRequestListener{

    private FriendsViewModel mViewModel;

    private View root;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;
    private FriendRequestListAdapter friendRequestListAdapter;
    private List<TRequestFriend> friendsList;
    int listPosition = -1;

    public static FriendRequestListFragment newInstance() {
        return new FriendRequestListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.friend_request_list_fragment, container, false);

        mViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        mViewModel.init();

        initUI();
        initObservers();

        friendsList = new ArrayList<>();
        friendRequestListAdapter = new FriendRequestListAdapter(friendsList, this); //getActivity = MainActivity.this
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(friendRequestListAdapter);

        mViewModel.friendRequestList(App.getInstance().getUsername());

        return root;
    }

    private void initObservers() {

        mViewModel.getSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(AppConstants.LIST_REQ_FRIEND_OK == integer){

                }
                progressBar.setVisibility(View.GONE);
            }
        });

        mViewModel.getmAcceptFriend().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(AppConstants.ACCEPT_REQ_FRIEND_OK.equals(integer) && listPosition != -1){
                    Toast.makeText(getContext(), "Se ha aceptado la recomendación", Toast.LENGTH_SHORT).show();
                    TRequestFriend friends = friendsList.get(listPosition);
                    friendsList.remove(friends);
                    friendRequestListAdapter = new FriendRequestListAdapter(friendsList, FriendRequestListFragment.this);
                    recyclerView.setAdapter(friendRequestListAdapter);
                    progressBar.setVisibility(View.GONE);
                    listPosition = -1;
                }
            }
        });

        mViewModel.getmDeclineFriend().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(AppConstants.DECLINE_REQ_FRIEND_OK.equals(integer) && listPosition != -1){
                    Toast.makeText(getContext(), "Se ha rechazado la recomendación", Toast.LENGTH_SHORT).show();
                    TRequestFriend friends = friendsList.get(listPosition);
                    friendsList.remove(friends);
                    friendRequestListAdapter = new FriendRequestListAdapter(friendsList, FriendRequestListFragment.this);
                    recyclerView.setAdapter(friendRequestListAdapter);
                    progressBar.setVisibility(View.GONE);
                    listPosition = -1;
                }
            }
        });

        mViewModel.getmFriendRequestList().observe(getViewLifecycleOwner(), new Observer<List<TRequestFriend>>() {
            @Override
            public void onChanged(List<TRequestFriend> tRequestFriends) {
                friendsList = tRequestFriends;
                friendRequestListAdapter = new FriendRequestListAdapter(tRequestFriends, FriendRequestListFragment.this);
                recyclerView.setAdapter(friendRequestListAdapter);
            }
        });

    }

    private void initUI() {
        recyclerView = root.findViewById(R.id.friends_request_list_recycle_view);
        progressBar = root.findViewById(R.id.friends_request_list_progressBar);
        nestedScrollView = root.findViewById(R.id.friend_request_list_nestedScrollView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onFriendRequestDeclineClick(int position) {
        String userOrigin = friendsList.get(position).getUserOrigin().getUsername();
        String userDest = friendsList.get(position).getUserDest().getUsername();
        listPosition = position;
        progressBar.setVisibility(View.VISIBLE);
        mViewModel.declineFriendRequest(userOrigin, userDest);
    }

    @Override
    public void onFriendRequestAcceptClick(int position) {
        String userOrigin = friendsList.get(position).getUserOrigin().getUsername();
        String userDest = friendsList.get(position).getUserDest().getUsername();
        listPosition = position;
        progressBar.setVisibility(View.VISIBLE);
        mViewModel.acceptFriendRequest(userOrigin, userDest);
    }
}