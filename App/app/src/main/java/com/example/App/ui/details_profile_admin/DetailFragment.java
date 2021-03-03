package com.example.App.ui.details_profile_admin;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.App.App;
import com.example.App.R;
import com.example.App.SessionManager;
import com.example.App.models.transfer.TUser;
import com.example.App.utilities.AppConstants;

public class DetailFragment extends Fragment{
    private View root;
    private DetailViewModel mViewModel;
    private TUser user;
    private TextView tv_Username;
    private TextView tv_FullName;
    private TextView tv_Email;

    private TextView tv_Comments;
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

        deleteAccountButton = root.findViewById(R.id.deleteButton2);

        tv_Username = root.findViewById(R.id.tv_username2);
        tv_FullName = root.findViewById(R.id.tv_full_name2);
        tv_Email = root.findViewById(R.id.tv_email2);


        //Maybe used in the future
        tv_Comments = root.findViewById(R.id.tv_n_comments2);
        tv_VisitedPlaces  = root.findViewById(R.id.tv_visited_places2);

        fillProfileFields();
        initializeListeners();
        initializeObservers();

        return root;
    }

    private void initializeObservers() {

        mViewModel.getDetailProfileActionInProgress().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                /*if (aBoolean) {
                    showProgressBar();
                }
                else {
                    hideProgressBar();
                }*/
            }
        });

        mViewModel.getActionDetailProfileSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer aInteger) {
                if (aInteger.equals(AppConstants.DELETE_PROFILE)) {
                    Toast.makeText(getActivity(), "Se ha eliminado el perfil seleccionado", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(root).navigate(R.id.adminFragment);
                }
                else {
                    Toast.makeText(getActivity(), "Algo ha funcionado mal", Toast.LENGTH_SHORT).show();
                }
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

        app = App.getInstance(getActivity());
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
