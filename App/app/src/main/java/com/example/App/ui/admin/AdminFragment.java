package com.example.App.ui.admin;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.App.UserListAdapter;
import com.example.App.App;
import com.example.App.R;
import com.example.App.transfer.TUser;

import java.util.List;

public class AdminFragment extends Fragment {


    private View root;
    private AdminViewModel mViewModel;
    private ListView listViewUser;
    private List<TUser> listUser;
    private App app;

    public static AdminFragment newInstance() {
        return new AdminFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.admin_fragment, container, false);
        app = App.getInstance(getActivity());
        listViewUser = root.findViewById(R.id.lvUserList);
        UserListAdapter adapter = new UserListAdapter(getActivity(), app.getUsersList());
        listViewUser.setAdapter(adapter);
        /*listViewUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TUser user = listUser.get(position);
                Toast.makeText(getActivity(), user.getName(), Toast.LENGTH_SHORT).show();
            }
        });*/

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        // TODO: Use the ViewModel
    }

}