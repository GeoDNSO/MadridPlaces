package com.example.App.ui.place_details;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.App.App;
import com.example.App.R;
import com.example.App.models.transfer.TPlace;
import com.example.App.ui.LogoutObserver;
import com.example.App.ui.comments.CommentsFragment;
import com.example.App.ui.map.MapboxActivity;
import com.example.App.ui.profile.ProfileViewModel;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.TextViewExpandableUtil;
import com.example.App.utilities.ViewListenerUtilities;
import com.google.android.material.navigation.NavigationView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class PlaceDetailFragment extends Fragment implements LogoutObserver {


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

    private ImageView ivVisited;

    private MenuItem deletePlace;
    private MenuItem modifyPlace;
    private MenuItem toPendingVisited;

    private Fragment childFragment;
    private Button recomButton;


    public static PlaceDetailFragment newInstance() {
        return new PlaceDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.place_detail_fragment, container, false);
        setHasOptionsMenu(true);

        mViewModel = new ViewModelProvider(this).get(PlaceDetailViewModel.class);
        mViewModel.init();

        initUI();

        place = (TPlace) getArguments().getParcelable(AppConstants.BUNDLE_PLACE_DETAILS);

        fillFields();

        listeners();

        observers();

        App.getInstance(getActivity()).addLogoutObserver(this);

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

        recomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecomClick();
            }
        });

        favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onFavClick(0, lastFavImage);
            }
        });

        ivMapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bundle bundle = new Bundle();
                //bundle.putParcelable(AppConstants.BUNDLE_PLACE_DETAILS, place);

                //Le pasamos el bundle
                //Navigation.findNavController(root).navigate(R.id.mapFragment, bundle);
                Intent mapboxIntent = new Intent(getActivity(), MapboxActivity.class);
                mapboxIntent.putExtra("placeMapbox", place); //Optional parameters
                getActivity().startActivity(mapboxIntent);
            }
        });

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                //Info para debug

                Log.i("SCROLL_PLACE_LIST", "X:" + scrollX + " Y:" + scrollY);
                int diff = v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight();
                Log.i("SCROLL_PLACE_LIST", "Diff:" + diff);

                if(scrollY >= diff){
                    ((CommentsFragment) childFragment).onScrollViewAtBottom();
                }
            }
        });

    }

    private void observers(){
        mViewModel.getPlaceDetailProfileSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer aInteger) {
                if (aInteger.equals(AppConstants.DELETE_PLACE)) {
                    Toast.makeText(getActivity(), "Se ha eliminado el lugar", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(root).navigate(R.id.homeFragment);
                }
                else {
                    Toast.makeText(getActivity(), "Algo ha funcionado mal", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mViewModel.getFavSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                if (integer.equals(AppConstants.FAV_POST_OK)){
                    place.setUserFav(!place.isUserFav());

                    int favTint = ContextCompat.getColor(getActivity(), R.color.grey);
                    if(place.isUserFav()){
                        favTint = ContextCompat.getColor(getActivity(), R.color.colorFavRed);
                    }

                    ImageViewCompat.setImageTintList(favIcon, ColorStateList.valueOf(favTint));
                    return ;
                }

                Toast.makeText(getActivity(), "Error al hacer favorito", Toast.LENGTH_SHORT);
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

        if(place.getTimeVisited() != null && !place.getTimeVisited().equals(""))
            ivVisited.setImageResource(R.drawable.ic_flag);

    }

    private void initUI() {
        nestedScrollView = root.findViewById(R.id.placeDetail_ScrollView);
        //placeImage = root.findViewById(R.id.placeDetailsPicture);
        tvPlaceName = root.findViewById(R.id.tvPlaceDetailsName);
        ivMapIcon = root.findViewById(R.id.placeDetailsMapIcon);
        favIcon = root.findViewById(R.id.favDetailsImage);
        recomButton = root.findViewById(R.id.go_to_rec_form);
        tvPlaceDescription = root.findViewById(R.id.placeDetailsDescription);
        tvPlaceRating = root.findViewById(R.id.tvPlaceDetailsRating);

        tvNumberOfRatings = root.findViewById(R.id.tvPlaceDetailsNumberOfRatings);

        tvAddress = root.findViewById(R.id.tvPlaceDetailAddress);
        ratingBar = root.findViewById(R.id.placeDetailRatingBar);


        sliderView = root.findViewById(R.id.placeDetail_slide_view);

        ivVisited = root.findViewById(R.id.ivVisitedFlag);

        isDescCollapsed = true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PlaceDetailViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.option_for_place_navigation_menu, menu);

        modifyPlace = menu.findItem(R.id.modify_place_menu_button);

        modifyPlace.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Bundle bundle = new Bundle();

                bundle.putParcelable(AppConstants.BUNDLE_PLACE_DETAILS, place);

                //Le pasamos el bundle
                Navigation.findNavController(root).navigate(R.id.modifyPlaceFragment, bundle);
                return true;
            }
        });
        deletePlace = menu.findItem(R.id.delete_place_menu_button);
        deletePlace.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                deleteDialog();
                return true;
            }
        });

        toPendingVisited = menu.findItem(R.id.pending_visited_menu_item);
        toPendingVisited.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //TODO
                return true;
            }
        });

        if(App.getInstance(getActivity()).isLogged()){
            modifyPlace.setVisible(true);
            if(App.getInstance(getActivity()).isAdmin()){
                deletePlace.setVisible(true);
            }
            else{
                deletePlace.setVisible(false);
            }
        }
        else {
            modifyPlace.setVisible(false);
            deletePlace.setVisible(false);

        }


    }

    public void deleteDialog(){
        final AlertDialog.Builder deletePlaceDialog = new AlertDialog.Builder(root.getContext());
        deletePlaceDialog.setTitle(getString(R.string.profile_delete_place));
        deletePlaceDialog.setMessage(getString(R.string.profile_delete_place_message));

        deletePlaceDialog.setPositiveButton(getString(R.string.alert_yes), (dialog, which) -> {
            mViewModel.deletePlace(place.getName()); //Llamar al viewmodel para borrar lugar
        });
        deletePlaceDialog.setNegativeButton(getString(R.string.alert_no), (dialog, which) -> {
            //Close
        });
        deletePlaceDialog.show();
    }

    @Override
    public void onLogout() {
        modifyPlace.setVisible(false);
        deletePlace.setVisible(false);


        //TODO probar con un navigate a si mismo
        if(getActivity() == null){
            return ;
        }
        int favTint = ContextCompat.getColor(getActivity(), R.color.grey);
        ImageViewCompat.setImageTintList(favIcon, ColorStateList.valueOf(favTint));
        ratingBar.setRating(0);
    }

    protected ImageView lastFavImage;

    public void onFavClick(int position, ImageView favImage) {
        Toast.makeText(getActivity(), "fav listener", Toast.LENGTH_SHORT).show();

        if(App.getInstance(getActivity()).getSessionManager().isLogged() == false){
            Toast.makeText(getActivity(), "Tienes que estar logueado para poder tener favoritos", Toast.LENGTH_SHORT).show();
            return;
        }

        lastFavImage = favImage;

        String username = App.getInstance(getActivity()).getUsername();

        mViewModel.setFavOnPlace(place, username);
    }

    public void onRecomClick() {
        Toast.makeText(getActivity(), "Recom listener", Toast.LENGTH_SHORT).show();

        if(App.getInstance(getActivity()).getSessionManager().isLogged() == false) {
            Toast.makeText(getActivity(), "Tienes que estar logueado para enviar una recomendacion", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            Bundle bundle = new Bundle();

            bundle.putParcelable(AppConstants.BUNDLE_PLACE_DETAILS, place);

            //Le pasamos el bundle
            Navigation.findNavController(root).navigate(R.id.sendRecomendationFragment, bundle);
        }

    }
}