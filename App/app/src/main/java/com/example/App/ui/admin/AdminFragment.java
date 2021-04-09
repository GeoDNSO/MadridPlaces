package com.example.App.ui.admin;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.App.App;
import com.example.App.MainActivity;
import com.example.App.MainActivityInterface;
import com.example.App.R;
import com.example.App.models.transfer.TUser;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.ViewListenerUtilities;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdminFragment extends Fragment implements UserListAdapter.OnListListener {


    private View root;
    private AdminViewModel mViewModel;
    private NestedScrollView nestedScrollView;
    private App app;
    private List<TUser> listUser;
    private ProgressBar progressBar;
    private UserListAdapter adapter;
    private RecyclerView recyclerView;
    private UserListAdapter.OnListListener onListListener;
    private boolean sortUsernameboolean;
    private boolean sortNameboolean;
    private int page = 1, quantum = 8;

    public static AdminFragment newInstance() {
        return new AdminFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.admin_fragment, container, false);
        setHasOptionsMenu(true);

        mViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        mViewModel.init();

        init();

        page = 1;
        progressBar.setVisibility(View.VISIBLE); //progress bar visible
        mViewModel.listUsers(page, quantum);

        listeners();

        mViewModel.getListUsers().observe(getViewLifecycleOwner(), new Observer<List<TUser>>() {
            @Override
            public void onChanged(List<TUser> tUsers) {
                if (tUsers == null){
                    tUsers = new ArrayList<>();
                }
                listUser = tUsers;
                adapter = new UserListAdapter(getActivity(), listUser, onListListener);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }
        });

        mViewModel.getListSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

            }
        });

        mViewModel.getProgressBar().observe(getViewLifecycleOwner(), aBoolean ->
                ViewListenerUtilities.setVisibility(progressBar, aBoolean));

        adminManagement();

        return root;
    }

    private void adminManagement() {
        ((MainActivity)getActivity()).setDrawerUnlock();
        recyclerView = root.findViewById(R.id.recycle_view);
        this.onListListener = this;
        if (listUser == null){
            listUser = new ArrayList<>();
        }
        adapter = new UserListAdapter(getActivity(), listUser, onListListener);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void init(){
        nestedScrollView = root.findViewById(R.id.list_user_nestedScrollView);
        progressBar = root.findViewById(R.id.user_list_progressBar);
        sortNameboolean = true;
        sortUsernameboolean = true;
    }

    private void listeners(){
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY <= v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                    //Cuando alacance al ultimo item de la lista
                    //Incrementea el numero de la pagina
                    page++;

                    progressBar.setVisibility(View.VISIBLE); //progress bar visible
                    //Pedimos más datos
                    mViewModel.listUsers(page, quantum);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        //progressBar.setVisibility(View.VISIBLE); //progress bar visible
    }

    @Override
    public void OnListClick(int position) {
        Bundle bundle = new Bundle();

        TUser user = listUser.get(position);

        bundle.putParcelable(AppConstants.BUNDLE_PROFILE_LIST_DETAILS, user);

        //Le pasamos el bundle
        Navigation.findNavController(root).navigate(R.id.detailFragment, bundle);
    }

    @Override
    public void onDestroyView(){
        ((MainActivity)getActivity()).setDrawerLock();
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.search_icon_menu, menu);

        MenuItem azIcon = menu.findItem(R.id.sortListUsers);
        azIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.isVisible()){
                    ((MainActivity)getActivity()).openDrawer();
                }
                else{
                    ((MainActivity)getActivity()).closeDrawer();
                }
                return true;
            }
        });

        MenuItem searchIcon = menu.findItem(R.id.search_button);
        SearchView searchView = (SearchView) searchIcon.getActionView();

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                azIcon.setVisible(false);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                azIcon.setVisible(true);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        Menu menu1 = ((MainActivity) getActivity()).sortUserNameMenuItem();
        Menu menu2 =  ((MainActivity) getActivity()).sortNameMenuItem();
        MenuItem sortUsernameIcon = menu1.findItem(R.id.sortUsernameUser);//menu.findItem(R.id.sortUsernameUser);
        sortUsernameIcon.setIcon(new ColorDrawable(Color.TRANSPARENT));

        MenuItem sortNameIcon = menu2.findItem(R.id.sortNameUser);//menu.findItem(R.id.sortNameUser);
        sortNameIcon.setIcon(new ColorDrawable(Color.TRANSPARENT));

        sortUsernameIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (sortUsernameboolean == true) {
                    sortUsernameIcon.setIcon(R.drawable.ic_baseline_keyboard_arrow_up_24);
                    sortNameIcon.setIcon(new ColorDrawable(Color.TRANSPARENT));
                    sortUsernameboolean = false;
                    sortNameboolean = true;
                    Collections.sort(listUser, TUser.comparatorUsernameAZusers);
                    adapter.notifyDataSetChanged();
                } else {
                    sortUsernameIcon.setIcon(R.drawable.ic_baseline_keyboard_arrow_down_24);
                    sortNameIcon.setIcon(new ColorDrawable(Color.TRANSPARENT));
                    sortUsernameboolean = true;
                    sortNameboolean = true;
                    Collections.sort(listUser, TUser.comparatorUsernameZAusers);
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });
        sortNameIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (sortNameboolean == true) {
                    sortNameIcon.setIcon(R.drawable.ic_baseline_keyboard_arrow_up_24);
                    sortUsernameIcon.setIcon(new ColorDrawable(Color.TRANSPARENT));
                    sortNameboolean = false;
                    sortUsernameboolean = true;
                    Collections.sort(listUser, TUser.comparatorRealnameAZusers);
                    adapter.notifyDataSetChanged();
                } else {
                    sortNameIcon.setIcon(R.drawable.ic_baseline_keyboard_arrow_down_24);
                    sortUsernameIcon.setIcon(new ColorDrawable(Color.TRANSPARENT));
                    sortNameboolean = true;
                    sortUsernameboolean = true;
                    Collections.sort(listUser, TUser.comparatorRealnameZAusers);
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });

    }



}
