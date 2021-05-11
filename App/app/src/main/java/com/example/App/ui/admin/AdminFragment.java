package com.example.App.ui.admin;

import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
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
import com.example.App.R;
import com.example.App.models.TUser;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.ViewListenerUtilities;

import java.util.ArrayList;
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
    private Integer sortUsernameboolean; /*0 == no sort, 1 == sort up (A-Z), 2 == sort down (Z-A)*/
    private Integer sortNameboolean; /*0 == no sort, 1 == sort up (A-Z), 2 == sort down (Z-A)*/
    private Integer finalsort;
    private int page = 1, quantum = 8;
    private SearchView searchView;

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
        sortNameboolean = AppConstants.NO_SORT;
        sortUsernameboolean = AppConstants.NO_SORT;
        finalsort = AppConstants.NO_SORT;
    }

    private void listeners(){
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                    //Cuando alacance al ultimo item de la lista
                    //Incrementea el numero de la pagina
                    page++;

                    progressBar.setVisibility(View.VISIBLE); //progress bar visible
                    //Pedimos más datos
                    mViewModel.listUsers(page, quantum, searchView.getQuery().toString(), AppConstants.FINAL_NO_SORT);
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

        searchView = (SearchView) searchIcon.getActionView();

        searchView.setMaxWidth(600);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                page = 1;
                progressBar.setVisibility(View.VISIBLE);
                mViewModel.clearList();
                mViewModel.listUsers(page, quantum, newText, finalsort);

                //adapter.getFilter().filter(newText);
                return false;
            }
        });

        MenuItem micIcon = menu.findItem(R.id.microphone_button);

        micIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");
                try {
                    startActivityForResult(intent, AppConstants.RESULT_SPEECH);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(getContext(), "Error: No se ha podido conectar con el micrófono", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                return true;
            }
        });


        page = 1;
        progressBar.setVisibility(View.VISIBLE); //progress bar visible
        mViewModel.listUsers(page, quantum, searchView.getQuery().toString(), AppConstants.FINAL_NO_SORT);

        Menu menu1 = ((MainActivity) getActivity()).sortUserNameMenuItem();
        Menu menu2 =  ((MainActivity) getActivity()).sortNameMenuItem();
        MenuItem sortUsernameIcon = menu1.findItem(R.id.sortUsernameUser);//menu.findItem(R.id.sortUsernameUser);
        sortUsernameIcon.setIcon(new ColorDrawable(Color.TRANSPARENT));

        MenuItem sortNameIcon = menu2.findItem(R.id.sortNameUser);//menu.findItem(R.id.sortNameUser);
        sortNameIcon.setIcon(new ColorDrawable(Color.TRANSPARENT));

        sortUsernameIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mViewModel.clearList();
                if (sortUsernameboolean == AppConstants.NO_SORT || sortUsernameboolean == AppConstants.SORT_UP) {
                    sortUsernameIcon.setIcon(R.drawable.ic_baseline_keyboard_arrow_up_24);
                    sortNameIcon.setIcon(new ColorDrawable(Color.TRANSPARENT));
                    sortUsernameboolean = AppConstants.SORT_DOWN;
                    sortNameboolean = AppConstants.NO_SORT;
                    finalsort = AppConstants.NICKNAME_SORT_UP;

                    page = 1;
                    progressBar.setVisibility(View.VISIBLE); //progress bar visible
                    mViewModel.listUsers(page, quantum, searchView.getQuery().toString(), AppConstants.NICKNAME_SORT_UP);

                    //Collections.sort(listUser, TUser.comparatorUsernameAZusers);
                    adapter.notifyDataSetChanged();
                } else {
                    sortUsernameIcon.setIcon(R.drawable.ic_baseline_keyboard_arrow_down_24);
                    sortNameIcon.setIcon(new ColorDrawable(Color.TRANSPARENT));
                    sortUsernameboolean = AppConstants.SORT_UP;
                    sortNameboolean = AppConstants.NO_SORT;
                    finalsort = AppConstants.NICKNAME_SORT_DOWN;

                    page = 1;
                    progressBar.setVisibility(View.VISIBLE); //progress bar visible
                    mViewModel.listUsers(page, quantum, searchView.getQuery().toString(), AppConstants.NICKNAME_SORT_DOWN);

                    //Collections.sort(listUser, TUser.comparatorUsernameZAusers);
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });
        sortNameIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mViewModel.clearList();
                if (sortNameboolean == AppConstants.NO_SORT || sortNameboolean == AppConstants.SORT_UP) {
                    sortNameIcon.setIcon(R.drawable.ic_baseline_keyboard_arrow_up_24);
                    sortUsernameIcon.setIcon(new ColorDrawable(Color.TRANSPARENT));
                    sortNameboolean = AppConstants.SORT_DOWN;
                    sortUsernameboolean = AppConstants.NO_SORT;
                    finalsort = AppConstants.NAME_SORT_UP;

                    page = 1;
                    progressBar.setVisibility(View.VISIBLE); //progress bar visible
                    mViewModel.listUsers(page, quantum, searchView.getQuery().toString(), AppConstants.NAME_SORT_UP);

                    //Collections.sort(listUser, TUser.comparatorRealnameAZusers);
                    adapter.notifyDataSetChanged();
                } else {
                    sortNameIcon.setIcon(R.drawable.ic_baseline_keyboard_arrow_down_24);
                    sortUsernameIcon.setIcon(new ColorDrawable(Color.TRANSPARENT));
                    sortNameboolean = AppConstants.SORT_UP;
                    sortUsernameboolean = AppConstants.NO_SORT;
                    finalsort = AppConstants.NAME_SORT_DOWN;

                    page = 1;
                    progressBar.setVisibility(View.VISIBLE); //progress bar visible
                    mViewModel.listUsers(page, quantum, searchView.getQuery().toString(), AppConstants.NAME_SORT_DOWN);

                    //Collections.sort(listUser, TUser.comparatorRealnameZAusers);
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case AppConstants.RESULT_SPEECH:
                if (resultCode == Activity.RESULT_OK && data != null){
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    page = 1;
                    progressBar.setVisibility(View.VISIBLE);
                    mViewModel.clearList();
                    mViewModel.listUsers(page, quantum, text.get(0), finalsort);
                }
                break;
        }
    }



}
