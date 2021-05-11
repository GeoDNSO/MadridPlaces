package com.example.App.ui.friends.subclasses.friends_list;

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
import com.example.App.models.TRequestFriend;
import com.example.App.ui.friends.FriendsViewModel;
import com.example.App.utilities.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class FriendListFragment extends Fragment implements FriendListAdapter.FriendActionListener{

    private FriendsViewModel mViewModel;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FriendListAdapter friendListAdapter;

    private List<TRequestFriend> friendList;

    private View root;
    private int lastPosition = -1;

    public static FriendListFragment newInstance() {
        return new FriendListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.friend_list_fragment, container, false);
        mViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        mViewModel.init();

        init();
        observers();

        friendList = new ArrayList<>();
        friendListAdapter = new FriendListAdapter(getActivity(), friendList, this); //getActivity = MainActivity.this
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(friendListAdapter);

        mViewModel.friendList(App.getInstance().getUsername());

        return root;
    }

    private void observers() {
        mViewModel.getSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(AppConstants.LIST_REQ_FRIEND_OK == integer){

                }
                progressBar.setVisibility(View.GONE);
            }
        });

        mViewModel.getmFriendList().observe(getViewLifecycleOwner(), new Observer<List<TRequestFriend>>() {
            @Override
            public void onChanged(List<TRequestFriend> tRequestFriends) {
                friendList = tRequestFriends;
                friendListAdapter = new FriendListAdapter(getActivity(), tRequestFriends, FriendListFragment.this);
                recyclerView.setAdapter(friendListAdapter);
            }
        });

        mViewModel.getmDeleteFriend().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Toast.makeText(getContext(), "Se ha aceptado la recomendación", Toast.LENGTH_SHORT).show();
                TRequestFriend friends = friendList.get(lastPosition);
                friendList.remove(friends);
                friendListAdapter = new FriendListAdapter(getActivity(), friendList, FriendListFragment.this);
                recyclerView.setAdapter(friendListAdapter);
                progressBar.setVisibility(View.GONE);
                lastPosition = -1;
            }
        });
    }

    private void init() {
        recyclerView = root.findViewById(R.id.friends_list_recycle_view);
        progressBar = root.findViewById(R.id.friends_list_progressBar);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDeleteFriend(int position) {
        String userOrigin = friendList.get(position).getUserOrigin().getUsername();
        String userDest = friendList.get(position).getUserDest().getUsername();
        lastPosition = position;
        progressBar.setVisibility(View.VISIBLE);
        mViewModel.deleteFriend(userOrigin, userDest);
    }
}