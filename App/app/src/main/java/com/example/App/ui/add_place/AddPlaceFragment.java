package com.example.App.ui.add_place;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.App.R;
import com.example.App.ui.browser.BrowserViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class AddPlaceFragment extends Fragment {

    private AddPlaceViewModel mViewModel;
    private View root;
    private LinearLayout linearLayout;
    private ImageButton imageButton;
    private Button button;
    private int numberOfImages;
    private List<String> listTypesPlaces;
    private List<String> listTypePlaces;
    private ChipGroup chipGroupView;

    private List<Uri> uriList;

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

        listTypesPlaces = mViewModel.getTypesOfPlaces();

        addChips();

        return root;
    }

    public void init(){
        linearLayout = root.findViewById(R.id.linearLayout_images_from_gallery);
        imageButton = root.findViewById(R.id.image_button_add_place);
        chipGroupView = root.findViewById(R.id.type_of_place_list_add_place);
        button = root.findViewById(R.id.button_add_place);
    }

    public void initListeners(){
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertImagesFromGallery();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), listTypePlaces.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addChips(){
        for(String typePlace : listTypesPlaces) {
            listTypePlaces = new ArrayList<>();
            Chip chip = (Chip) LayoutInflater.from(getContext()).inflate(R.layout.type_place_list_fragment,null);
            chip.setText(typePlace);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(chip.isChecked()){
                        listTypePlaces.add(chip.getText().toString());
                        /*adapter.getFilter().filter(chip.getText().toString());
                        adapter.notifyDataSetChanged();*/
                    }
                    else {
                        listTypePlaces.remove(chip.getText().toString());
                    }
                }
            });
            chipGroupView.addView(chip);
        }
    }

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
                removeImages();
                if(data.getClipData() != null) {
                    numberOfImages = data.getClipData().getItemCount(); //devuelve el numero de imagenes seleccionadas
                    for (int i = 0; i < numberOfImages; ++i) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        uriList.add(uri);
                    }
                }
                else{
                    Uri uri = data.getData();
                    uriList.add(uri);
                }
                showImages();
            }

        }
    }
}
