package com.example.App.ui.profile;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.App.App;
import com.example.App.R;
import com.example.App.SessionManager;
import com.example.App.models.transfer.TUser;
import com.example.App.utilities.AppConstants;

public class ProfileFragment extends Fragment {

    private View root;
    private ProfileViewModel mViewModel;

    private TextView tv_Username;
    private TextView tv_FullName;
    private TextView tv_Email;
    private TextView tv_Password;
    private EditText et_Email;
    private EditText et_Password;

    private TextView tv_Comments;
    private TextView tv_VisitedPlaces;

    private ImageButton ib_editProfile;
    private Button deleteAccountButton;
    private Button confirmChangesButton;
    private Button cancelChangesButton;

    private App app;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.profile_fragment, container, false);
        app = App.getInstance(getActivity());
        app.setBottomMenuVisible(View.GONE);

        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        mViewModel.init();

        deleteAccountButton = root.findViewById(R.id.deleteButton);
        ib_editProfile = root.findViewById(R.id.edit_button);
        cancelChangesButton = root.findViewById(R.id.bt_cancelChanges);
        confirmChangesButton = root.findViewById(R.id.bt_acepptChanges);

        tv_Username = root.findViewById(R.id.tv_username);
        tv_FullName = root.findViewById(R.id.tv_full_name);
        tv_Email = root.findViewById(R.id.tv_email);
        tv_Password = root.findViewById(R.id.profile_password);

        //editar perfil
        et_Email = root.findViewById(R.id.tv_email_editable);
        et_Password = root.findViewById(R.id.profile_password_editable);

        //Maybe used in the future
        tv_Comments = root.findViewById(R.id.tv_n_comments);;
        tv_VisitedPlaces  = root.findViewById(R.id.tv_visited_places);

        initializeUI();
        initializeListeners();
        initializeObservers();

        return root;
    }

    private void initializeObservers() {

        mViewModel.getProfileActionInProgress().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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

        mViewModel.getActionProfileSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer aInteger) {
                if (aInteger.equals(AppConstants.DELETE_PROFILE)) {
                    Toast.makeText(getActivity(), "Se ha eliminado el perfil", Toast.LENGTH_SHORT).show();
                    app.logout();
                    Navigation.findNavController(root).navigate(R.id.homeFragment);
                }
                else if(aInteger.equals(AppConstants.MODIFY_PROFILE)){
                    Toast.makeText(getActivity(), "Se ha modificado el perfil", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(root).navigate(R.id.profileFragment);
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


    @Override
    public void onDestroyView(){
        app = App.getInstance(getActivity());
        app.setBottomMenuVisible(View.VISIBLE);
        super.onDestroyView();
    }

    private void initializeListeners() {
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccountAction(v);
        }});

        ib_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileAction(v);
            }
        });

        confirmChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmChangesAction(v);
            }
        });

        cancelChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelChangesAction(v);
            }
        });
    }

    private void initializeUI() {
        if(!app.isLogged()){
            return ;
        }

        //app = App.getInstance(getActivity());
        fillProfileFields();
    }
    //Rellena los datos del usuario segun la informacion de la sesion
    private void fillProfileFields(){
        SessionManager sm = app.getSessionManager();

        tv_Username.setText(sm.getUsername());
        tv_FullName.setText((sm.getFirstName() + " " + sm.getSurname()));
        tv_Password.setText(sm.getPassword());
        tv_Email.setText(sm.getEmail());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }


    private void deleteAccountAction(View v){
        final AlertDialog.Builder deleteAccountDialog = new AlertDialog.Builder(root.getContext());
        deleteAccountDialog.setTitle(getString(R.string.profile_delete_account_title));
        deleteAccountDialog.setMessage(getString(R.string.profile_delete_account_message));

        app = App.getInstance(getActivity());
        SessionManager sm = app.getSessionManager();
        deleteAccountDialog.setPositiveButton(getString(R.string.alert_yes), (dialog, which) -> {
            mViewModel.deleteUser(sm.getUsername()); //Llamar al viewmodel para borrar usuario

        });
        deleteAccountDialog.setNegativeButton(getString(R.string.alert_no), (dialog, which) -> {
            //Close
        });
        deleteAccountDialog.create().show();
    }

    private void editProfileAction(View v){
        tv_Email.setVisibility(View.GONE);
        et_Email.setVisibility(View.VISIBLE);
        et_Email.setText(tv_Email.getText().toString());
        tv_Password.setVisibility(View.GONE);
        et_Password.setVisibility(View.VISIBLE);
        et_Password.setText(tv_Password.getText().toString());
        deleteAccountButton.setVisibility(View.GONE);
        confirmChangesButton.setVisibility(View.VISIBLE);
        cancelChangesButton.setVisibility(View.VISIBLE);
    }

    private void cancelChangesAction(View v){
        tv_Email.setVisibility(View.VISIBLE);
        et_Email.setVisibility(View.GONE);

        tv_Password.setVisibility(View.VISIBLE);
        et_Password.setVisibility(View.GONE);

        deleteAccountButton.setVisibility(View.VISIBLE);
        confirmChangesButton.setVisibility(View.GONE);
        cancelChangesButton.setVisibility(View.GONE);
    }

    private void confirmChangesAction(View v){
        et_Email.setVisibility(View.GONE);
        tv_Email.setVisibility(View.VISIBLE);

        et_Password.setVisibility(View.GONE);
        tv_Password.setVisibility(View.VISIBLE);

        //Conseguir los datos del usuario para despues modificarlos
        app = App.getInstance(getActivity());
        SessionManager sm = app.getSessionManager();
        TUser u = sm.getSesionUser();

        //Modificar datos del usuario según lo modificado
        u.setEmail(et_Email.getText().toString());
        u.setPassword(et_Password.getText().toString());

        mViewModel.modifyUser(u);

        /*
        if(app.modifyUser(u)){
            u = app.getUser(u.getUsername());
            app.setUserSession(u);
            Toast.makeText(getActivity(), "Se ha modificado el perfil", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.profileFragment);
        }
        else{
            Toast.makeText(getActivity(), "Algo ha funcionado mal", Toast.LENGTH_SHORT).show();
        }
        */
        deleteAccountButton.setVisibility(View.VISIBLE);
        confirmChangesButton.setVisibility(View.GONE);
        cancelChangesButton.setVisibility(View.GONE);
    }


}