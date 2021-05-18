package com.example.App.ui.details_profile_admin;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.App.App;
import com.example.App.R;
import com.example.App.SessionManager;
import com.example.App.models.TUser;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.ControlValues;
import com.example.App.utilities.OnResultAction;

import java.util.HashMap;

public class DetailFragment extends Fragment{
    private View root;
    private DetailViewModel mViewModel;
    private HashMap<Integer, OnResultAction> actionHashMap;

    private TUser user;
    private TextView tv_Username;
    private TextView tv_FullName;
    private TextView tv_Email;
    private ImageView iv_imageView;

    private TextView tv_Favourites;
    private TextView tv_VisitedPlaces;

    private Button deleteAccountButton;

    private App app;

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.detail_fragment, container, false);

        user = (TUser) getArguments().getParcelable(AppConstants.BUNDLE_PROFILE_LIST_DETAILS);

        mViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        mViewModel.init();
        
        initUI();

        fillProfileFields();
        initializeListeners();
        initializeObservers();

        configOnResultActions();

        return root;
    }

    private void configOnResultActions() {
        actionHashMap = new HashMap<>();
        actionHashMap.put(ControlValues.DELETE_PROFILE_OK, () -> {
            Toast.makeText(getActivity(), getString(R.string.profile_delete_msg), Toast.LENGTH_SHORT).show();
            Navigation.findNavController(root).navigate(R.id.adminFragment);
        });

        actionHashMap.put(ControlValues.DELETE_PROFILE_FAILED, () -> {
            Toast.makeText(getActivity(), getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
        });
    }

    private void initUI() {
        deleteAccountButton = root.findViewById(R.id.deleteButton2);

        tv_Username = root.findViewById(R.id.tv_username2);
        tv_FullName = root.findViewById(R.id.tv_full_name2);
        tv_Email = root.findViewById(R.id.tv_email2);
        iv_imageView = root.findViewById(R.id.profile_view_admin);


        //Maybe used in the future
        tv_Favourites = root.findViewById(R.id.tv_n_favourites_admin);
        tv_VisitedPlaces  = root.findViewById(R.id.tv_visited_places_admin);
    }

    private void initializeObservers() {

        mViewModel.getSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(actionHashMap.containsKey(integer))
                    actionHashMap.get(integer).execute();
            }
        });

        mViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<TUser>() {
            @Override
            public void onChanged(TUser tUser) {
                if(tUser != null){
                    app.setUserSession(tUser);
                    fillProfileFields();
                }
            }
        });

        mViewModel.getProfilePairMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Pair<Integer, Integer>>() {
            @Override
            public void onChanged(Pair<Integer, Integer> pair) {
                tv_Favourites.setText(pair.first + "");
                tv_VisitedPlaces.setText(pair.second + "");
            }
        });
    }

    private void initializeListeners() {
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccountAction(v);
            }});
    }

    //Rellena los datos del usuario segun la informacion de la sesion
    private void fillProfileFields(){
        tv_Username.setText(user.getUsername());
        tv_FullName.setText((user.getName() + " " + user.getSurname()));
        tv_Email.setText(user.getEmail());

        mViewModel.countCommentsAndHistoryUser(user.getUsername());

        if(user.getImage_profile() == null || user.getImage_profile() == ""){
            iv_imageView.setImageResource(R.drawable.ic_username);
        }
        else{
            Glide.with(getActivity()).load(user.getImage_profile())
                    .circleCrop()
                    .into(iv_imageView);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        // TODO: Use the ViewModel
    }

    private void deleteAccountAction(View v){
        final AlertDialog.Builder deleteAccountDialog = new AlertDialog.Builder(root.getContext());
        deleteAccountDialog.setTitle(getString(R.string.profile_delete_account_title));
        deleteAccountDialog.setMessage(getString(R.string.profile_delete_account_message));

        app = App.getInstance();
        SessionManager sm = app.getSessionManager();
        deleteAccountDialog.setPositiveButton(getString(R.string.alert_yes), (dialog, which) -> {
            if(user.getUsername().equals(sm.getUsername())){
                Toast.makeText(getActivity(), "Para borrar tu cuenta navega a tu perfil.", Toast.LENGTH_SHORT).show();
            }
            else {
                mViewModel.deleteUser(user.getUsername()); //Llamar al viewmodel para borrar usuario
            }
        });
        deleteAccountDialog.setNegativeButton(getString(R.string.alert_no), (dialog, which) -> {
            //Close
        });
        deleteAccountDialog.create().show();
    }

}
