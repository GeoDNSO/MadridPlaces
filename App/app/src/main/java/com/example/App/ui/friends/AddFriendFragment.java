package com.example.App.ui.friends;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.App.R;
import com.example.App.utilities.AppConstants;


public class AddFriendFragment extends Fragment {

    private View root;
    private FriendsViewModel mViewModel;
    
    private EditText editText;
    private Button addButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_add_friend, container, false);
        mViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);

        initUI();
        listeners();
        observers();

        return root;
    }

    private void listeners() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editText.getText().toString();
                mViewModel.friendRequestList(username);
            }
        });
    }

    private void initUI() {
        editText = root.findViewById(R.id.add_friend_input);
        addButton = root.findViewById(R.id.addFriendButton);
    }

    private void observers() {
        mViewModel.getmFriendRequest().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Context context = AddFriendFragment.this.getContext();
                if(integer.equals(AppConstants.REQ_FRIEND_OK)){
                    Toast.makeText(context, context.getString(R.string.friend_request_sent_success), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(context, context.getString(R.string.friend_request_sent_fail), Toast.LENGTH_SHORT).show();
            }
        });
    }


}