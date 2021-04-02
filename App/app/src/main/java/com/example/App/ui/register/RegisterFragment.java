package com.example.App.ui.register;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.App.App;
import com.example.App.R;
import com.example.App.utilities.Validator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class RegisterFragment extends Fragment {

    private View root;
    private RegisterViewModel mRegisterViewModel;
    private App app;
    private Bitmap bitmap;
    private Uri uri;

    /*MVVM*/
    private EditText et_Name;
    private EditText et_Surname;
    private EditText et_Username;
    private ImageButton ib_profileImage;
    private EditText et_Email;
    private EditText et_Password, et_RepeatPassword;
    private Button registerButton;
    private TextView tv_ToLogin;
    private ProgressBar progressBar;


    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.register_fragment, container, false);

        mRegisterViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        mRegisterViewModel.init();

        mRegisterViewModel.getRegisterInProcess().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    showProgressBar();
                }
                else {
                    hideProgressBar();
                }
            }
        });

        mRegisterViewModel.getIsDoneRegistration().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(getActivity(), "Registrado con exito", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(root).navigate(R.id.action_registerFragment_to_homeFragment);
                }
                else {
                    hideProgressBar();
                    Toast.makeText(getActivity(), "Error al registrar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        initializeUI();
        initializeListeners();

        return root;
    }



    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void initializeUI() {
        et_Name = (EditText) root.findViewById(R.id.register_completename);
        et_Username = (EditText) root.findViewById(R.id.username);
        et_Surname = (EditText) root.findViewById(R.id.register_surname);
        et_Email = (EditText) root.findViewById(R.id.register_email);
        et_Password = (EditText) root.findViewById(R.id.password);
        et_RepeatPassword = (EditText) root.findViewById(R.id.register_repeat_password);
        registerButton = (Button) root.findViewById(R.id.button);
        tv_ToLogin = (TextView) root.findViewById(R.id.to_login);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBarRegister);
        ib_profileImage = root.findViewById(R.id.register_imageButton);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mRegisterViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initializeListeners() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //root = v;
                registerOnClickAction(v);
            }
        });

        tv_ToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_loginFragment);
            }
        });

        ib_profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertImagesFromGallery();
            }
        });

    }


    //Validate form
    private void registerOnClickAction(View v) {

        String username = et_Username.getText().toString();
        String name = et_Name.getText().toString();
        String email = et_Email.getText().toString();
        String surname = et_Surname.getText().toString();
        String pass = et_Password.getText().toString();
        String pass2 = et_RepeatPassword.getText().toString();
        String profile_image = bitmapToBase64(bitmap);

        if (Validator.argumentsEmpty(username, name, email, surname, pass, pass2)) {
            Toast.makeText(getActivity(), getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
        }
        if (!Validator.validEmail(email)) {
            et_Email.setError(getString(R.string.email_not_valid));
        }
        if (!pass.equals(pass2)) {
            et_Password.setError(getString(R.string.password_not_equal));
            et_RepeatPassword.setError(getString(R.string.password_not_equal));
        }
        if (Validator.usernameAlredyExists(username)) { //TODO HAY QUE LLAMAR a app EN LA FUNCION
            et_Username.setError(getString(R.string.username_exists));
        }

        mRegisterViewModel.registerUser(username, pass, name, surname, email, "H", "1990-01-01", "Madrid", "user", profile_image);
    }


    private void resetErrors(){
        et_Username.setError(null);
        et_Name.setError(null);
        et_Email.setError(null);
        et_Surname.setError(null);
        et_Password.setError(null);
        et_RepeatPassword.setError(null);
    }

    private boolean errorsInForm(){
        return !( et_Username.getError() == null && et_Name.getError() == null &&
                et_Email.getError() == null && et_Surname.getError() == null &&
                et_Password.getError() == null && et_RepeatPassword.getError() == null);
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
        ib_profileImage.setImageURI(uri);
        ib_profileImage.setBackgroundColor(getResources().getColor(R.color.white));
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