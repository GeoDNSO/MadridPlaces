package com.example.App.ui.add_place;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.App.App;
import com.example.App.R;
import com.example.App.ui.browser.BrowserViewModel;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.Validator;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;

public class AddPlaceFragment extends Fragment {

    private AddPlaceViewModel mViewModel;
    private View root;
    private LinearLayout linearLayout;
    private ImageButton imageButton;
    private Button button;
    private int numberOfImages;
    private List<String> listTypesPlaces;
    private String finalTypePlace;
    private ChipGroup chipGroupView;
    private EditText et_placeName;
    private TextInputEditText tiet_placeDescription;

    private List<Uri> uriList;
    List<Bitmap> bitmapList;
    private List<String> imageStringBase64;

    public static AddPlaceFragment newInstance() {
        return new AddPlaceFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(AddPlaceViewModel.class);
        root = inflater.inflate(R.layout.add_place_fragment, container, false);
        init();
        initListeners();

        mViewModel.init();

        mViewModel.getmAddPlaceSuccess().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(getActivity(), "Se ha creado el lugar", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(root).navigate(R.id.homeFragment);
                }
                else {
                    Toast.makeText(getActivity(),  "No se ha podido crear el lugar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mViewModel.getmCategoriesSuccess().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> categoriesList) {
                listTypesPlaces = new ArrayList<>();
                listTypesPlaces = categoriesList;
                addChips();
            }
        });



        return root;
    }

    public void init(){
        linearLayout = root.findViewById(R.id.linearLayout_images_from_gallery);
        imageButton = root.findViewById(R.id.image_button_add_place);
        chipGroupView = root.findViewById(R.id.type_of_place_list_add_place);
        button = root.findViewById(R.id.button_add_place);
        et_placeName = root.findViewById(R.id.name_place_add_place);
        tiet_placeDescription = root.findViewById(R.id.description_place_add_place);
        uriList = new ArrayList<>();
    }

    public void initListeners(){
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, AppConstants.REQUEST_STORAGE);
                }else {
                    insertImagesFromGallery();
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlaceOnClickAction(v);
                //Toast.makeText(getActivity(), finalTypePlace, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel.getTypesOfPlaces();
    }

    private void addChips(){
        for(String typePlace : listTypesPlaces) {
            Chip chip = (Chip) LayoutInflater.from(getContext()).inflate(R.layout.type_place_list_fragment,null, false);
            chip.setText(typePlace);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(chip.isChecked()){
                        chipGroupView.clearCheck();
                        chip.setChecked(true);
                        finalTypePlace = chip.getText().toString();
                    }
                }
            });
            chipGroupView.addView(chip);
        }
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
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        for (int i = 0; i < uriList.size(); ++i){
            View view = layoutInflater.inflate(R.layout.image_item_fragment, linearLayout, false);
            ImageView imageView = view.findViewById(R.id.imageViewUser);
            imageView.setImageURI(uriList.get(i));
            imageView.setPadding(10,0,10,0);
            linearLayout.addView(view);
        }
    }

    public void removeImages(){
        linearLayout.removeAllViewsInLayout();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            if(resultCode == Activity.RESULT_OK){
                uriList = new ArrayList<>();
                bitmapList = new ArrayList<>();
                removeImages();
                if(data.getClipData() != null) {
                    numberOfImages = data.getClipData().getItemCount(); //devuelve el numero de imagenes seleccionadas
                    for (int i = 0; i < numberOfImages; ++i) {
                        try {
                            Uri uri = data.getClipData().getItemAt(i).getUri();
                            bitmapList.add(MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri));
                            uriList.add(uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    try {
                        Uri uri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                        bitmapList.add(bitmap);
                        uriList.add(uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                showImages();
            }
        }
    }

    private String convertUriToPath (Uri uri){
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String cursorString = cursor.getString(0);
        cursorString = cursorString.substring(cursorString.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Images.Media._ID + " = ? ", new String[]{cursorString}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void addPlaceOnClickAction(View v){
        imageStringBase64 = new ArrayList<>();
        if(uriList.size() > 0) {
            for (int i = 0; i < uriList.size(); ++i) {
                imageStringBase64.add(bitmapToBase64(bitmapList.get(i)));
                //imageUri.add(convertUriToPath(uriList.get(i)));
            }
        }
        String placeName = et_placeName.getText().toString();
        String placeDescription = tiet_placeDescription.getText().toString();

        if (Validator.argumentsEmpty(placeName, placeDescription, finalTypePlace)) {
            Toast.makeText(getActivity(), getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
        }
        mViewModel.addPlace(placeName, placeDescription, finalTypePlace, imageStringBase64);
    }
}
