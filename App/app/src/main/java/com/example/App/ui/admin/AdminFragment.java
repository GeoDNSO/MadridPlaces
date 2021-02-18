package com.example.App.ui.admin;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.App.App;
import com.example.App.R;
import com.example.App.models.transfer.TUser;

import java.util.List;

public class AdminFragment extends Fragment {


    private View root;
    private AdminViewModel mViewModel;
    private App app;
    private List<TUser> listUser;
    private UserListAdapter adapter;
    private RecyclerView recyclerView;

    public static AdminFragment newInstance() {
        return new AdminFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.admin_fragment, container, false);
        app = App.getInstance(getActivity());
        recyclerView = root.findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));
        adapter = new UserListAdapter(getActivity(), app.getUsersList());
        recyclerView.setAdapter(adapter);

        /*recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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