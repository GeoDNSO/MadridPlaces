package com.example.App.ui.friends.subclasses.friend_resquest_list;

import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.App.App;
import com.example.App.R;
import com.example.App.models.TRequestFriend;
import com.example.App.ui.friends.FriendsViewModel;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.ControlValues;
import com.example.App.utilities.OnResultAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendRequestListFragment extends Fragment implements FriendRequestListAdapter.OnFriendRequestListener{

    private FriendsViewModel mViewModel;
    private View root;
    private HashMap<Integer, OnResultAction> actionHashMap;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;
    private FriendRequestListAdapter friendRequestListAdapter;
    private List<TRequestFriend> friendsList;
    int lastPosition = -1;

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
        configOnResultActions();

        friendsList = new ArrayList<>();
        friendRequestListAdapter = new FriendRequestListAdapter(getActivity(), friendsList, this); //getActivity = MainActivity.this
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(friendRequestListAdapter);

        mViewModel.friendRequestList(App.getInstance().getUsername());

        return root;
    }

    private void configOnResultActions() {
        actionHashMap = new HashMap<>();
        actionHashMap.put(ControlValues.LIST_REQ_FRIEND_OK, () -> {
            //Nothing..
        });
        actionHashMap.put(ControlValues.LIST_REQ_FRIEND_FAIL, () -> {
            //Nothing
        });

        actionHashMap.put(ControlValues.ACCEPT_REQ_FRIEND_OK, () -> {
            if(lastPosition != -1)
                return;
            Toast.makeText(getContext(), getString(R.string.friend_request_accepted), Toast.LENGTH_SHORT).show();
            TRequestFriend friends = friendsList.get(lastPosition);
            friendsList.remove(friends);
            friendRequestListAdapter = new FriendRequestListAdapter(friendsList, FriendRequestListFragment.this);
            recyclerView.setAdapter(friendRequestListAdapter);
            progressBar.setVisibility(View.GONE);
            lastPosition = -1;
        });
        actionHashMap.put(ControlValues.ACCEPT_REQ_FRIEND_FAIL, () -> {
            //Nothing
        });

        actionHashMap.put(ControlValues.DECLINE_REQ_FRIEND_OK, () -> {
            if(lastPosition != -1)
                return;

            TRequestFriend friends = friendsList.get(lastPosition);
            Toast.makeText(getContext(), getString(R.string.decline_friend_request) +friends.getUserDest(), Toast.LENGTH_SHORT).show();
            friendsList.remove(friends);
            friendRequestListAdapter = new FriendRequestListAdapter(friendsList, FriendRequestListFragment.this);
            recyclerView.setAdapter(friendRequestListAdapter);
            progressBar.setVisibility(View.GONE);
            lastPosition = -1;
        });
        actionHashMap.put(ControlValues.DECLINE_REQ_FRIEND_FAIL, () -> {
           //Nothing
        });
    }

    private void initObservers() {

        mViewModel.getSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
               if (actionHashMap.containsKey(integer))
                   actionHashMap.get(integer).execute();
                progressBar.setVisibility(View.GONE);
            }
        });

        mViewModel.getmFriendRequestList().observe(getViewLifecycleOwner(), new Observer<List<TRequestFriend>>() {
            @Override
            public void onChanged(List<TRequestFriend> tRequestFriends) {
                friendsList = tRequestFriends;
                friendRequestListAdapter = new FriendRequestListAdapter(getActivity(), tRequestFriends, FriendRequestListFragment.this);
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
        lastPosition = position;
        progressBar.setVisibility(View.VISIBLE);
        mViewModel.declineFriendRequest(userOrigin, userDest);
    }

    @Override
    public void onFriendRequestAcceptClick(int position) {
        String userOrigin = friendsList.get(position).getUserOrigin().getUsername();
        String userDest = friendsList.get(position).getUserDest().getUsername();
        lastPosition = position;
        progressBar.setVisibility(View.VISIBLE);
        mViewModel.acceptFriendRequest(userOrigin, userDest);
    }
}