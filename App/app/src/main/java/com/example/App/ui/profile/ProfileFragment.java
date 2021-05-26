package com.example.App.ui.profile;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.App.App;
import com.example.App.R;
import com.example.App.SessionManager;
import com.example.App.models.TUser;
import com.example.App.utilities.ControlValues;
import com.example.App.utilities.OnResultAction;
import com.example.App.utilities.Validator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class ProfileFragment extends Fragment {

    private View root;
    private ProfileViewModel mViewModel;
    private HashMap<Integer, OnResultAction> actionHashMap;

    private TextView tv_Username;
    private TextView tv_FullName;
    private TextView tv_Email;
    private EditText et_Email;


    private TextView tv_Password;
    private EditText et_Password;
    private TextView tv_NewPassword;
    private EditText et_NewPassword;
    private TextView tv_RepeatPassword;
    private EditText et_RepeatPassword;

    private ImageButton ib_profile_image;

    private TextView tv_Favourites;
    private TextView tv_VisitedPlaces;

    private Bitmap bitmap;
    private Uri uri;

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

        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        mViewModel.init();

        init();
        initializeUI();
        initializeListeners();
        initializeObservers();
        configOnResultActions();

        //tiene que estar despues de los observadores
        ib_profile_image.setClickable(false);

        return root;
    }

    private void configOnResultActions() {
        actionHashMap = new HashMap<>();
        actionHashMap.put(ControlValues.DELETE_PROFILE_OK, () -> {
            Toast.makeText(getActivity(), getString(R.string.user_profile_deleted), Toast.LENGTH_SHORT).show();
            app.logout();
            Navigation.findNavController(root).navigate(R.id.homeFragment);
        });

        actionHashMap.put(ControlValues.DELETE_PROFILE_FAILED, () -> {
            Toast.makeText(getActivity(), getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
        });

        actionHashMap.put(ControlValues.MODIFY_USER_OK, () -> {
            Toast.makeText(getActivity(), getString(R.string.user_profile_modified), Toast.LENGTH_SHORT).show();
            Navigation.findNavController(root).navigate(R.id.profileFragment);
        });
        actionHashMap.put(ControlValues.MODIFY_USER_FAIL, () -> {
            Toast.makeText(getActivity(), getString(R.string.modify_user_failed), Toast.LENGTH_SHORT).show();
            Navigation.findNavController(root).navigate(R.id.profileFragment);
        });

    }

    private void init(){
        deleteAccountButton = root.findViewById(R.id.deleteButton);
        ib_editProfile = root.findViewById(R.id.edit_button);
        cancelChangesButton = root.findViewById(R.id.bt_cancelChanges);
        confirmChangesButton = root.findViewById(R.id.bt_acepptChanges);

        tv_Username = root.findViewById(R.id.tv_username);
        tv_FullName = root.findViewById(R.id.tv_full_name);
        tv_Email = root.findViewById(R.id.tv_email);

        tv_Password = root.findViewById(R.id.tv_profile_password_editable);
        et_Password = root.findViewById(R.id.profile_password_editable);
        tv_NewPassword = root.findViewById(R.id.tv_profile_new_password_editable);
        et_NewPassword = root.findViewById(R.id.profile_new_password_editable);
        tv_RepeatPassword = root.findViewById(R.id.tv_profile_repeat_password_editable);
        et_RepeatPassword = root.findViewById(R.id.profile_repeat_password_editable);

        ib_profile_image = root.findViewById(R.id.iv_imgUser);

        //editar perfil
        et_Email = root.findViewById(R.id.tv_email_editable);

        //Maybe used in the future
        tv_Favourites = root.findViewById(R.id.tv_n_favourites);
        tv_VisitedPlaces  = root.findViewById(R.id.tv_visited_places);
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

        ib_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertImagesFromGallery();
            }
        });
    }

    private void initializeUI() {
        app = App.getInstance();
        if(!app.isLogged()){
            return ;
        }

        fillProfileFields();
    }
    //Rellena los datos del usuario segun la informacion de la sesion
    private void fillProfileFields(){
        SessionManager sm = app.getSessionManager();

        mViewModel.countCommentsAndHistoryUser(sm.getUsername());

        tv_Username.setText(sm.getUsername());
        tv_FullName.setText((sm.getFirstName() + " " + sm.getSurname()));
        //tv_Password.setText(sm.getPassword());
        tv_Email.setText(sm.getEmail());

        String image_profile = sm.getImageProfile();
        if(image_profile == ""){
            ib_profile_image.setImageResource(R.drawable.ic_username);
        }
        else {
            //byte[] decodedString = Base64.decode(image_profile, Base64.DEFAULT);
            //Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Glide.with(getActivity()).load(image_profile)
                    .circleCrop()
                    .into(ib_profile_image);
        }
    }

    public Bitmap ConvertToImage(String image){
        try{
            InputStream stream = new ByteArrayInputStream(image.getBytes());
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            return bitmap;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

        app = App.getInstance();
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
        Toast.makeText(getContext(), getString(R.string.modify_user_text), Toast.LENGTH_SHORT).show();
        tv_Email.setVisibility(View.GONE);
        et_Email.setVisibility(View.VISIBLE);
        et_Email.setText(tv_Email.getText().toString());

        //Para habilitar la edicion de la imagen
        ib_profile_image.setClickable(true);

        //Antigua Contraseña
        tv_Password.setVisibility(View.VISIBLE);
        et_Password.setVisibility(View.VISIBLE);
        //Nueva Contraseña
        tv_NewPassword.setVisibility(View.VISIBLE);
        et_NewPassword.setVisibility(View.VISIBLE);
        //Repite Nueva Contraseña
        tv_RepeatPassword.setVisibility(View.VISIBLE);
        et_RepeatPassword.setVisibility(View.VISIBLE);

        deleteAccountButton.setVisibility(View.GONE);
        confirmChangesButton.setVisibility(View.VISIBLE);
        cancelChangesButton.setVisibility(View.VISIBLE);
    }

    private void cancelChangesAction(View v){
        tv_Email.setVisibility(View.VISIBLE);
        et_Email.setVisibility(View.GONE);

        //Para deshabilitar la edicion de la imagen
        ib_profile_image.setClickable(false);

        //Antigua Contraseña
        tv_Password.setVisibility(View.GONE);
        et_Password.setVisibility(View.GONE);
        //Nueva Contraseña
        tv_NewPassword.setVisibility(View.GONE);
        et_NewPassword.setVisibility(View.GONE);
        //Repite Nueva Contraseña
        tv_RepeatPassword.setVisibility(View.GONE);
        et_RepeatPassword.setVisibility(View.GONE);

        deleteAccountButton.setVisibility(View.VISIBLE);
        confirmChangesButton.setVisibility(View.GONE);
        cancelChangesButton.setVisibility(View.GONE);
    }

    private void confirmChangesAction(View v){
        //Conseguir los datos del usuario para despues modificarlos
        app = App.getInstance();
        SessionManager sm = app.getSessionManager();
        TUser u = sm.getSessionUser();

        if(Validator.argumentsEmpty(et_Email.getText().toString(), et_Password.getText().toString())){
            Toast.makeText(getActivity(), getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
        }
        else if(!Validator.validEmail(et_Email.getText().toString())){
            et_Email.setError(getString(R.string.email_not_valid));
        }
        else if(!et_Password.getText().toString().equals(sm.getPassword())){
            et_Password.setError(getString(R.string.password_not_equal));
        }
        else if (!et_NewPassword.getText().toString().equals(et_RepeatPassword.getText().toString())) {
            et_NewPassword.setError(getString(R.string.password_not_equal));
            et_RepeatPassword.setError(getString(R.string.password_not_equal));
        }
        else {
            et_Email.setVisibility(View.GONE);
            tv_Email.setVisibility(View.VISIBLE);

            //Para deshabilitar la edicion de la imagen
            ib_profile_image.setClickable(false);

            //Antigua Contraseña
            tv_Password.setVisibility(View.GONE);
            et_Password.setVisibility(View.GONE);
            //Nueva Contraseña
            tv_NewPassword.setVisibility(View.GONE);
            et_NewPassword.setVisibility(View.GONE);
            //Repite Nueva Contraseña
            tv_RepeatPassword.setVisibility(View.GONE);
            et_RepeatPassword.setVisibility(View.GONE);

            //Modificar datos del usuario según lo modificado
            u.setEmail(et_Email.getText().toString());
            if(et_NewPassword.getText().toString().isEmpty() || et_NewPassword.getText().toString() == null) {
                u.setPassword(et_Password.getText().toString());
            }
            else{
                u.setPassword(et_NewPassword.getText().toString());
            }
            if (bitmap != null) {
                u.setImage_profile(bitmapToBase64(bitmap));
            }
            mViewModel.modifyUser(u);

            deleteAccountButton.setVisibility(View.VISIBLE);
            confirmChangesButton.setVisibility(View.GONE);
            cancelChangesButton.setVisibility(View.GONE);
        }
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

    }

    //función para seleccionar imagenes de la galeria
    private void insertImagesFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagenes"), 0);
    }

    public void showImages(){
        Glide.with(getActivity()).load(bitmap)
                .circleCrop()
                .into(ib_profile_image);
        //ib_profileImage.setImageURI(uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            if(resultCode == Activity.RESULT_OK){
                if(data.getClipData() != null) {
                    try {
                        uri = data.getClipData().getItemAt(0).getUri();
                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        uri = data.getData();
                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                showImages();
            }
        }
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


}