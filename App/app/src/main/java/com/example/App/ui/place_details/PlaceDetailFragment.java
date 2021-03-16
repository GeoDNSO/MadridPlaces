package com.example.App.ui.place_details;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.App.R;
import com.example.App.models.transfer.TPlace;
import com.example.App.ui.comments.CommentsFragment;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.TextViewExpandableUtil;
import com.example.App.utilities.ViewListenerUtilities;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class PlaceDetailFragment extends Fragment {


    private View root;

    private PlaceDetailViewModel mViewModel;
    private TPlace place;

    private NestedScrollView nestedScrollView;

    private SliderView sliderView;
    private SliderAdp sliderAdp;

    private boolean isDescCollapsed;
    private TextView tvPlaceName;
    private ImageView ivMapIcon;
    private ImageView favIcon;
    private TextView tvPlaceDescription;
    private TextView tvPlaceRating;
    private RatingBar ratingBar;
    private TextView tvAddress;
    private TextView tvNumberOfRatings;

    private Fragment childFragment;


    public static PlaceDetailFragment newInstance() {
        return new PlaceDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.place_detail_fragment_prueba, container, false);
        
        initUI();

        place = (TPlace) getArguments().getParcelable(AppConstants.BUNDLE_PLACE_DETAILS);

        fillFields();

        listeners();

        //Poner el nombre del lugar en la toolbar
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        actionBar.setTitle(place.getName());

        //Prueba de carga de comentarios
        childFragment = new CommentsFragment(place.getName());
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.comments_placeholder, childFragment).commit();


        //Gestion del Slider View
        sliderAdp = new SliderAdp(place, getActivity());
        sliderView.setSliderAdapter(sliderAdp);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();

        return root;
    }

    private void listeners() {

        favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                place.setUserFav(!place.isUserFav());

                int favTint = ContextCompat.getColor(getActivity(), R.color.grey);
                if(place.isUserFav()){
                    favTint = ContextCompat.getColor(getActivity(), R.color.colorFavRed);
                }

                ImageViewCompat.setImageTintList(favIcon, ColorStateList.valueOf(favTint));
            }
        });

        ivMapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(AppConstants.BUNDLE_PLACE_DETAILS, place);

                //Le pasamos el bundle
                Navigation.findNavController(root).navigate(R.id.mapFragment, bundle);
            }
        });

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                    ((CommentsFragment) childFragment).onScrollViewAtBottom();
                }
            }
        });

    }

    private void fillFields() {

        tvPlaceName.setText(place.getName());
        tvPlaceDescription.setText(place.getName() + "desc :" + place.getDescription());

        tvAddress.setText(place.getAddress());

        tvNumberOfRatings.setText(500 + " Calificaciones");

        ratingBar.setRating((float) place.getRating());

        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
        tvPlaceRating.setText(df.format(place.getRating()));

        int favTint = ContextCompat.getColor(getActivity(), R.color.grey);
        if(place.isUserFav()){
            favTint = ContextCompat.getColor(getActivity(), R.color.colorFavRed);
        }

        ImageViewCompat.setImageTintList(favIcon, ColorStateList.valueOf(favTint));

        ViewListenerUtilities.makeTextViewExpandable(tvPlaceDescription, true);

    }



    private void initUI() {
        nestedScrollView = root.findViewById(R.id.placeDetail_ScrollView);
        //placeImage = root.findViewById(R.id.placeDetailsPicture);
        tvPlaceName = root.findViewById(R.id.tvPlaceDetailsName);
        ivMapIcon = root.findViewById(R.id.placeDetailsMapIcon);
        favIcon = root.findViewById(R.id.favDetailsImage);
        tvPlaceDescription = root.findViewById(R.id.placeDetailsDescription);
        tvPlaceRating = root.findViewById(R.id.tvPlaceDetailsRating);

        tvNumberOfRatings = root.findViewById(R.id.tvPlaceDetailsNumberOfRatings);

        tvAddress = root.findViewById(R.id.tvPlaceDetailAddress);
        ratingBar = root.findViewById(R.id.placeDetailRatingBar);


        sliderView = root.findViewById(R.id.placeDetail_slide_view);

        isDescCollapsed = true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PlaceDetailViewModel.class);
        // TODO: Use the ViewModel
    }

}