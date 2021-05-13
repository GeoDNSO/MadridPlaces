package com.example.App.ui.friends;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.App.R;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.ControlValues;
import com.example.App.utilities.OnResultAction;

import java.util.HashMap;


public class AddFriendFragment extends Fragment {

    private View root;
    private FriendsViewModel mViewModel;
    private HashMap<Integer, OnResultAction> actionHashMap;
    
    private EditText editText;
    private Button addButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_add_friend, container, false);
        mViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        mViewModel.init();

        initUI();
        listeners();
        observers();

        configOnResultActions();

        return root;
    }

    private void configOnResultActions() {
        actionHashMap = new HashMap<>();
        actionHashMap.put(ControlValues.REQ_FRIEND_OK, () -> {
            Toast.makeText(getContext(), getString(R.string.friend_request_sent_success), Toast.LENGTH_SHORT).show();
        });
        actionHashMap.put(ControlValues.REQ_FRIEND_FAIL, () -> {
            Toast.makeText(getContext(), getString(R.string.friend_request_sent_fail), Toast.LENGTH_SHORT).show();
        });
    }

    private void listeners() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editText.getText().toString();
                if(mViewModel == null)
                    Log.d("TAG", "onClick: "+ "Null");
                if(username == null)
                    Log.d("TAG", "onClick: "+ "String Null");
                mViewModel.sendFriendRequest(username);
            }
        });
    }

    private void initUI() {
        editText = root.findViewById(R.id.add_friend_input);
        addButton = root.findViewById(R.id.addFriendButton);
    }

    private void observers() {
        mViewModel.getSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(actionHashMap.containsKey(integer))
                    actionHashMap.get(integer).execute();
            }
        });
    }


}