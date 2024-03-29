package com.example.App.ui.modify_place;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.App.R;
import com.example.App.models.TPlace;

import com.example.App.utilities.AppConstants;
import com.example.App.utilities.ControlValues;
import com.example.App.utilities.OnResultAction;
import com.example.App.utilities.Validator;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModifyPlaceFragment extends Fragment {
    private View root;
    private ModifyPlaceViewModel mViewModel;
    private HashMap<Integer, OnResultAction> actionHashMap;

    private LinearLayout linearLayout;
    private Button imageButton;
    private Button button;
    private int numberOfImages;
    private List<String> listTypesPlaces;
    private String finalTypePlace;
    private ChipGroup chipGroupView;
    private TextInputEditText et_placeName;
    private TextInputEditText tiet_placeDescription;
    private TPlace place;

    private Double latitude;
    private Double longitude;
    private String r_number;
    private String r_class;
    private String r_name;
    private String zipcode;

    private List<Uri> uriList;
    List<Bitmap> bitmapList;
    private List<String> imageStringBase64;
    private ImageButton mapboxModifyPlace;
    private TextView tv_road_entire_name;

    public static ModifyPlaceFragment newInstance() {
        return new ModifyPlaceFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.modify_place_fragment, container, false);

        mViewModel = new ViewModelProvider(this).get(ModifyPlaceViewModel.class);
        mViewModel.init();

        place = (TPlace) getArguments().getParcelable(AppConstants.BUNDLE_PLACE_DETAILS);
        init();
        setValues();
        initListeners();

        observers();
        configOnResultActions();



        return root;
    }

    private void configOnResultActions() {
        actionHashMap = new HashMap<>();
        actionHashMap.put(ControlValues.MODIFY_PLACE_OK, () -> {
            Toast.makeText(getActivity(), getString(R.string.place_modified), Toast.LENGTH_SHORT).show();
            Navigation.findNavController(root).navigate(R.id.homeFragment);
        });

        actionHashMap.put(ControlValues.MODIFY_PLACE_FAIL, () -> {
            Toast.makeText(getActivity(),  getString(R.string.place_modified_failed), Toast.LENGTH_SHORT).show();
        });
    }

    private void observers() {
        mViewModel.getSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(actionHashMap.containsKey(integer))
                    actionHashMap.get(integer).execute();
            }
        });

        mViewModel.getmCategoriesList().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> categoriesList) {
                listTypesPlaces = new ArrayList<>();
                listTypesPlaces = categoriesList;
                addChips();
            }
        });
    }

    public void init(){
        linearLayout = root.findViewById(R.id.modify_linearLayout_images_from_gallery);
        imageButton = root.findViewById(R.id.image_button_modify_place);
        chipGroupView = root.findViewById(R.id.type_of_place_list_modify_place);
        button = root.findViewById(R.id.button_modify_place);
        et_placeName = root.findViewById(R.id.name_place_modify_place);
        tiet_placeDescription = root.findViewById(R.id.description_place_modify_place);
        mapboxModifyPlace = root.findViewById(R.id.image_button_modify_place_map);
        tv_road_entire_name = root.findViewById(R.id.modify_place_entire_place_name_text);
        uriList = new ArrayList<>();
    }

    private void setValues(){
        et_placeName.setText(place.getName());
        tiet_placeDescription.setText(place.getDescription());
        r_number = place.getRoad_number();
        r_name = place.getRoad_name();
        r_class= place.getRoad_class();
        zipcode = place.getZipcode();
        latitude = place.getLatitude();
        longitude = place.getLongitude();
        tv_road_entire_name.setText(String.format("%s %s %s %s", r_class, r_name, r_number, zipcode));
        //habra que hacer el de tipo de lugar
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        for (int i = 0; i < place.getImagesList().size(); ++i) {
            View view = layoutInflater.inflate(R.layout.image_item_fragment, linearLayout, false);
            ImageView imageView = view.findViewById(R.id.imageViewFriend);
            //imageView.setImage(uriList.get(i));
            try{
                Glide.with(getActivity()).load(place.getImagesList().get(i))
                        .into(imageView);
            }catch (Exception e){
                Log.e("ERR_SLIDER_ADAPTER", "ERROR_CARGA_IMAGEN_SLider_Adapter: Fallo de carga de imagen debido a cierre de socket" +
                        ", fallo de conexión, timeout, etc... )");
            }
            imageView.setPadding(10,0,10,0);
            linearLayout.addView(view);
        }
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

            if(typePlace.equals(place.getTypeOfPlace())){
                chip.setChecked(true);
                finalTypePlace = chip.getText().toString();
            }

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
            ImageView imageView = view.findViewById(R.id.imageViewFriend);
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
        switch(requestCode) {
            case (AppConstants.STATIC_INTEGER_MAPBOX_MODIFY): {
                if (resultCode == Activity.RESULT_OK) {
                    //String newText = data.getStringExtra(AppConstants.STATIC_STRING_MAPBOX_ADD_DATA);
                    Bundle bundle = data.getExtras();
                    String classAndName = bundle.getString("r_classAndName");
                    String[] className = classAndName.split(" ", 2);
                    r_class = className[0];
                    r_name = className[1];
                    r_number = bundle.getString("r_number");
                    zipcode = bundle.getString("zipcode");
                    latitude = bundle.getDouble("latitude");
                    longitude = bundle.getDouble("longitude");
                    tv_road_entire_name.setText(String.format("%s %s %s %s", r_class, r_name, r_number, zipcode));
                }
                break;
            }
            case (0): {
                if (resultCode == Activity.RESULT_OK) {
                    uriList = new ArrayList<>();
                    bitmapList = new ArrayList<>();
                    removeImages();
                    if (data.getClipData() != null) {
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
                    } else {
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
                try {
                    addPlaceOnClickAction(v);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(getActivity(), finalTypePlace, Toast.LENGTH_SHORT).show();
            }
        });
        mapboxModifyPlace.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), ModifyPlaceMapboxActivity.class);
                i.putExtra(AppConstants.BUNDLE_MODIFY_PLACE_DETAILS, place); //Optional parameters
                startActivityForResult(i, AppConstants.STATIC_INTEGER_MAPBOX_MODIFY);
            }
        });
    }


    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void addPlaceOnClickAction(View v) throws JSONException {
        imageStringBase64 = new ArrayList<>();
        if(uriList.size() > 0) {
            for (int i = 0; i < uriList.size(); ++i) {
                imageStringBase64.add(bitmapToBase64(bitmapList.get(i)));
            }
        }
        String placeName = et_placeName.getText().toString();
        String placeDescription = tiet_placeDescription.getText().toString();

        if (Validator.argumentsEmpty(placeName, placeDescription, finalTypePlace, r_class, r_name, r_number, zipcode)) {
            Toast.makeText(getActivity(), getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
        }
        else {
            if(uriList.size() > 0) {
                mViewModel.modifyPlace(placeName, placeDescription, finalTypePlace, imageStringBase64, place, latitude, longitude, r_class, r_name, r_number, zipcode);
            }
            else{
                mViewModel.modifyPlace(placeName, placeDescription, finalTypePlace, place, latitude, longitude, r_class, r_name, r_number, zipcode);
            }
        }
    }
}
