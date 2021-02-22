package com.example.App.ui.place_details;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.App.R;
import com.example.App.models.transfer.TPlace;
import com.example.App.utilities.AppConstants;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class PlaceDetailFragment extends Fragment {


    private View root;

    private PlaceDetailViewModel mViewModel;
    private TPlace place;

    private NestedScrollView nestedScrollView;
    private ImageView placeImage;
    private TextView tvPlaceName;
    private ImageView ivMapIcon;
    private ImageView favIcon;
    private TextView tvPlaceDescription;
    private TextView tvPlaceRating;

    private RatingBar ratingBar;
    private Button sendRateButton;


    public static PlaceDetailFragment newInstance() {
        return new PlaceDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.place_detail_fragment, container, false);
        
        initUI();

        place = (TPlace) getArguments().getParcelable(AppConstants.BUNDLE_PLACE_DETAILS);
        
        fillFields();

        listeners();

        Toast.makeText(getActivity(), "Listener del item: " + place.getName(), Toast.LENGTH_SHORT).show();

        //Poner el nombre del lugar en la toolbar
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        actionBar.setTitle(place.getName());

        return root;
    }

    private void listeners() {
        sendRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Rating de " + ratingBar.getRating(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillFields() {
        //TODO CAMBIAR EN EL FUTURO
        //placeImage.setLoQueSea(place.getImage());
        placeImage.setImageResource(R.drawable.imagen_lugar_default);
        tvPlaceName.setText(place.getName());
        tvPlaceDescription.setText(place.getName() + "desc :" + place.getDescription());

        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
        tvPlaceRating.setText(df.format(place.getRating()));
    }

    private void initUI() {
        nestedScrollView = root.findViewById(R.id.placeDetail_ScrollView);
        placeImage = root.findViewById(R.id.placeDetailsPicture);
        tvPlaceName = root.findViewById(R.id.tvPlaceDetailsName);
        ivMapIcon = root.findViewById(R.id.placeDetailsMapIcon);
        favIcon = root.findViewById(R.id.favDetailsImage);
        tvPlaceDescription = root.findViewById(R.id.placeDetailsDescription);
        tvPlaceRating = root.findViewById(R.id.tvPlaceDetailsRating);
        ratingBar = root.findViewById(R.id.placeDetailsRatingBar);
        sendRateButton = root.findViewById(R.id.placeDetailSendRating);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PlaceDetailViewModel.class);
        // TODO: Use the ViewModel
    }

}