package com.example.App.ui.add_place;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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

import com.example.App.R;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.ControlValues;
import com.example.App.utilities.OnResultAction;
import com.example.App.utilities.Validator;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddPlaceFragment extends Fragment {

    private AddPlaceViewModel mViewModel;
    private View root;
    private HashMap<Integer, OnResultAction> actionHashMap;

    private LinearLayout linearLayout;

    private Button imageButton;
    private Button button;

    private List<String> listTypesPlaces;
    private int numberOfImages;
    private String finalTypePlace;
    private ChipGroup chipGroupView;

    private TextInputEditText et_placeName;
    private TextInputEditText tiet_placeDescription;

    private Double latitude;
    private Double longitude;
    private String r_number;
    private String r_class;
    private String r_name;
    private String zipcode;

    private List<Uri> uriList;
    private List<Bitmap> bitmapList;
    private List<String> imageStringBase64;
    private ImageButton mapboxAddPlace;
    private TextView tv_road_entire_name;

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
        configOnResultActions();
        observers();

        mViewModel.init();

        return root;
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

    public void configOnResultActions(){
        actionHashMap.put(ControlValues.ADD_PLACE, () -> {
            Toast.makeText(getActivity(), getString(R.string.place_created_msg), Toast.LENGTH_SHORT).show();
            Navigation.findNavController(root).navigate(R.id.homeFragment);
        });
        actionHashMap.put(ControlValues.ADD_PLACE_FAILED, () -> {
            Toast.makeText(getActivity(),  getString(R.string.couldnt_create_place_msg), Toast.LENGTH_SHORT).show();
        });
    }

    public void init(){
        linearLayout = root.findViewById(R.id.linearLayout_images_from_gallery);
        imageButton = root.findViewById(R.id.image_button_add_place);
        chipGroupView = root.findViewById(R.id.type_of_place_list_add_place);
        button = root.findViewById(R.id.button_add_place);
        et_placeName = root.findViewById(R.id.name_place_add_place);
        tiet_placeDescription = root.findViewById(R.id.description_place_add_place);
        mapboxAddPlace = root.findViewById(R.id.image_button_add_place_map);
        tv_road_entire_name = root.findViewById(R.id.add_place_entire_place_name_text);
        r_number = "";
        r_name = "";
        r_class= "";
        zipcode = "";
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

        mapboxAddPlace.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /*Bundle bundle = new Bundle();
                bundle.putParcelable(AppConstants.BUNDLE_PLACE_DETAILS, place);*/

                //Le pasamos el bundle
                //Navigation.findNavController(root).navigate(R.id.mapFragment, bundle);
                Intent i = new Intent(getActivity(), AddPlaceMapboxActivity.class);
                startActivityForResult(i, AppConstants.STATIC_INTEGER_MAPBOX_ADD);


                /*Intent mapboxIntent = new Intent(getActivity(), AddPlaceMapboxActivity.class);
                // mapboxIntent.putExtra("key", "value"); //Optional parameters
                getActivity().startActivity(mapboxIntent);*/
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
            case (AppConstants.STATIC_INTEGER_MAPBOX_ADD) : {
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
            case(0) : {
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

        if (!validatePlaceName(placeName) || !validatePlaceDescription(placeDescription) ||
                Validator.argumentsEmpty(finalTypePlace, r_class, r_name, r_number, zipcode)) {
            Toast.makeText(getActivity(), getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
        }
        else if(Validator.placeAlredyExists(placeName)){
            et_placeName.setError(getString(R.string.place_exists));
        }
        else {
            mViewModel.addPlace(placeName, placeDescription, finalTypePlace, imageStringBase64, latitude, longitude, r_class, r_name, r_number, zipcode);
        }
    }

    private boolean validatePlaceName(String placeName){
        if(placeName.isEmpty()){
            et_placeName.setError("Campo obligatorio");
            return false;
        }
        else{
            et_placeName.setError(null);
            return true;
        }
    }

    private boolean validatePlaceDescription(String placeDescription){
        if(placeDescription.isEmpty()){
            tiet_placeDescription.setError("Campo obligatorio");
            return false;
        }
        else{
            tiet_placeDescription.setError(null);
            return true;
        }
    }


}
