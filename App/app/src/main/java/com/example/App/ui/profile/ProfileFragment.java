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
import com.example.App.models.transfer.TUser;
import com.example.App.utilities.AppConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProfileFragment extends Fragment {

    private View root;
    private ProfileViewModel mViewModel;

    private TextView tv_Username;
    private TextView tv_FullName;
    private TextView tv_Email;
    private TextView tv_Password;
    private EditText et_Email;
    private EditText et_Password;
    private ImageButton ib_profile_image;

    private TextView tv_Comments;
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

        //tiene que estar despues de los observadores
        ib_profile_image.setClickable(false);

        return root;
    }

    private void init(){
        deleteAccountButton = root.findViewById(R.id.deleteButton);
        ib_editProfile = root.findViewById(R.id.edit_button);
        cancelChangesButton = root.findViewById(R.id.bt_cancelChanges);
        confirmChangesButton = root.findViewById(R.id.bt_acepptChanges);

        tv_Username = root.findViewById(R.id.tv_username);
        tv_FullName = root.findViewById(R.id.tv_full_name);
        tv_Email = root.findViewById(R.id.tv_email);
        tv_Password = root.findViewById(R.id.profile_password);

        ib_profile_image = root.findViewById(R.id.iv_imgUser);

        //editar perfil
        et_Email = root.findViewById(R.id.tv_email_editable);
        et_Password = root.findViewById(R.id.profile_password_editable);

        //Maybe used in the future
        tv_Comments = root.findViewById(R.id.tv_n_comments);
        tv_VisitedPlaces  = root.findViewById(R.id.tv_visited_places);
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
                    Toast.makeText(getActivity(), getString(R.string.modify_user_failed), Toast.LENGTH_SHORT).show();
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
        app = App.getInstance(getActivity());
        if(!app.isLogged()){
            return ;
        }

        fillProfileFields();
    }
    //Rellena los datos del usuario segun la informacion de la sesion
    private void fillProfileFields(){
        SessionManager sm = app.getSessionManager();

        tv_Username.setText(sm.getUsername());
        tv_FullName.setText((sm.getFirstName() + " " + sm.getSurname()));
        tv_Password.setText(sm.getPassword());
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
        Toast.makeText(getContext(), getString(R.string.modify_user_text), Toast.LENGTH_SHORT).show();
        tv_Email.setVisibility(View.GONE);
        et_Email.setVisibility(View.VISIBLE);
        et_Email.setText(tv_Email.getText().toString());

        //Para habilitar la edicion de la imagen
        ib_profile_image.setClickable(true);

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

        //Para deshabilitar la edicion de la imagen
        ib_profile_image.setClickable(false);

        tv_Password.setVisibility(View.VISIBLE);
        et_Password.setVisibility(View.GONE);

        deleteAccountButton.setVisibility(View.VISIBLE);
        confirmChangesButton.setVisibility(View.GONE);
        cancelChangesButton.setVisibility(View.GONE);
    }

    private void confirmChangesAction(View v){
        et_Email.setVisibility(View.GONE);
        tv_Email.setVisibility(View.VISIBLE);

        //Para deshabilitar la edicion de la imagen
        ib_profile_image.setClickable(false);

        et_Password.setVisibility(View.GONE);
        tv_Password.setVisibility(View.VISIBLE);

        //Conseguir los datos del usuario para despues modificarlos
        app = App.getInstance(getActivity());
        SessionManager sm = app.getSessionManager();
        TUser u = sm.getSesionUser();

        //Modificar datos del usuario según lo modificado
        u.setEmail(et_Email.getText().toString());
        u.setPassword(et_Password.getText().toString());
        if(bitmap != null) {
            u.setImage_profile(bitmapToBase64(bitmap));
        }
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