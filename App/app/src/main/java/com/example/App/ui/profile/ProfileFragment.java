package com.example.App.ui.profile;

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
import com.example.App.ui.home.HomeViewModel;

public class ProfileFragment extends Fragment {

    private View root;
    private ProfileViewModel mViewModel;


    private TextView tv_Username;
    private TextView tv_FullName;
    private TextView tv_Email;
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


        deleteAccountButton = root.findViewById(R.id.deleteButton);
        ib_editProfile = root.findViewById(R.id.edit_button);
        cancelChangesButton = root.findViewById(R.id.bt_cancelChanges);
        confirmChangesButton = root.findViewById(R.id.bt_acepptChanges);

        tv_Username = root.findViewById(R.id.tv_username);
        tv_FullName = root.findViewById(R.id.tv_full_name);
        tv_Email = root.findViewById(R.id.tv_email);
        et_Password = root.findViewById(R.id.profile_password);
        //editar perfil


        //Maybe used in the future
        tv_Comments = root.findViewById(R.id.tv_n_comments);;
        tv_VisitedPlaces  = root.findViewById(R.id.tv_visited_places);

        initializeUI();
        initializeListeners();

        return root;
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
        app = App.getInstance(getActivity());
        SessionManager sm = app.getSessionManager();

        tv_Username.setText(sm.getUsername());
        tv_FullName.setText((sm.getFirstName() + " " + sm.getSurname()));
        et_Password.setText(sm.getPassword());
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

        deleteAccountDialog.setPositiveButton(getString(R.string.alert_yes), (dialog, which) -> {
            //Borrar cuenta desde DAO
            app.logout();
            Navigation.findNavController(v).navigate(R.id.homeFragment);
        });
         deleteAccountDialog.setNegativeButton(getString(R.string.alert_no), (dialog, which) -> {
             //Close
         });
         deleteAccountDialog.create().show();
    }

    private void editProfileAction(View v){
        deleteAccountButton.setVisibility(View.GONE);
        confirmChangesButton.setVisibility(View.VISIBLE);
        cancelChangesButton.setVisibility(View.VISIBLE);
    }

    private void cancelChangesAction(View v){
        deleteAccountButton.setVisibility(View.VISIBLE);
        confirmChangesButton.setVisibility(View.GONE);
        cancelChangesButton.setVisibility(View.GONE);
    }

    private void confirmChangesAction(View v){
        deleteAccountButton.setVisibility(View.VISIBLE);
        confirmChangesButton.setVisibility(View.GONE);
        cancelChangesButton.setVisibility(View.GONE);
    }


}