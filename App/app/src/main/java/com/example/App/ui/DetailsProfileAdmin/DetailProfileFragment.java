package com.example.App.ui.DetailsProfileAdmin;

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
import com.example.App.ui.profile.ProfileViewModel;
import com.example.App.utilities.AppConstants;

public class DetailProfileFragment extends Fragment{
    private View root;
    private ProfileViewModel mViewModel;

    private TextView tv_Username;
    private TextView tv_FullName;
    private TextView tv_Email;

    private TextView tv_Comments;
    private TextView tv_VisitedPlaces;

    private Button deleteAccountButton;

    private App app;

    public static DetailProfileFragment newInstance() {
        return new DetailProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.profile_fragment, container, false);
        app = App.getInstance(getActivity());
        app.setBottomMenuVisible(View.GONE);

        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
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
    }

    //Rellena los datos del usuario segun la informacion de la sesion
    private void fillProfileFields(){
        SessionManager sm = app.getSessionManager();

        tv_Username.setText(sm.getUsername());
        tv_FullName.setText((sm.getFirstName() + " " + sm.getSurname()));
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

}
