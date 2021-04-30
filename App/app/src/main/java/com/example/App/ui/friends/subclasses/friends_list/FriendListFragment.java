package com.example.App.ui.friends.subclasses.friends_list;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.App.R;

public class FriendListFragment extends Fragment implements FriendListAdapter.FriendActionListener{

    private FriendListViewModel mViewModel;

    public static FriendListFragment newInstance() {
        return new FriendListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.friend_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FriendListViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onListClick(int position) {
        //TODO --> vacio si no se va redirigir al perfil de un amigo
    }

    @Override
    public void onDeleteFriend(int position) {
        //TODO
    }
}