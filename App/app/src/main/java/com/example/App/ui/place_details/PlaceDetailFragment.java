package com.example.App.ui.place_details;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.App.App;
import com.example.App.R;
import com.example.App.models.TPlace;
import com.example.App.components.LogoutObserver;
import com.example.App.ui.comments.CommentsFragment;
import com.example.App.ui.map.MapboxActivity;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.ControlValues;
import com.example.App.utilities.OnResultAction;
import com.example.App.utilities.UserInterfaceUtils;
import com.example.App.utilities.ViewListenerUtilities;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class PlaceDetailFragment extends Fragment implements LogoutObserver {


    private View root;
    private HashMap<Integer, OnResultAction> actionHashMap;

    private PlaceDetailViewModel mViewModel;
    private TPlace place;

    private NestedScrollView nestedScrollView;

    private SliderView sliderView;
    private SliderAdapter sliderAdp;

    private boolean isDescCollapsed;
    private TextView tvPlaceName;
    private ImageView ivMapIcon;
    private ImageView favIcon;
    private TextView tvPlaceDescription;
    private TextView tvPlaceRating;
    private TextView tvDistance2Place;

    private RatingBar ratingBar;
    private TextView tvAddress;
    private TextView tvNumberOfRatings;

    private ImageView ivVisited;

    private MenuItem deletePlace;
    private MenuItem modifyPlace;
    private MenuItem toPendingVisited;

    private Fragment childFragment;


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
        listeners();
        observers();
        configOnResultActions();

        App.getInstance().addLogoutObserver(this);

        String bundlePlaceName = getArguments().getString(AppConstants.BUNDLE_PLACE_NAME_PLACE_DETAILS);
        if(bundlePlaceName != null){
            mViewModel.getPlaceByName(bundlePlaceName);
            return root;
        }

        place = (TPlace) getArguments().getParcelable(AppConstants.BUNDLE_PLACE_DETAILS);

        initConfig();

        return root;
    }

    private void configOnResultActions() {
        actionHashMap = new HashMap<>();
        actionHashMap.put(ControlValues.DELETE_PLACE_OK, () -> {
            Toast.makeText(getActivity(), getString(R.string.place_deleted_msg), Toast.LENGTH_SHORT).show();
            Navigation.findNavController(root).navigate(R.id.homeFragment);
        });
        actionHashMap.put(ControlValues.DELETE_PLACE_FAIL, () -> {
            Toast.makeText(getActivity(), getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
        });

        actionHashMap.put(ControlValues.FAV_POST_OK, () -> {
            place.setUserFav(!place.isUserFav());

            int favTint = ContextCompat.getColor(getActivity(), R.color.grey);
            if(place.isUserFav()){
                favTint = ContextCompat.getColor(getActivity(), R.color.colorFavRed);
            }

            ImageViewCompat.setImageTintList(favIcon, ColorStateList.valueOf(favTint));
        });
        actionHashMap.put(ControlValues.FAV_POST_FAIL, () -> {
            Toast.makeText(getActivity(), getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
        });

        actionHashMap.put(ControlValues.VISITED_POST_OK, () -> {
            Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_flag_grey);
            ivVisited.setImageDrawable(drawable);

            if(place.getTimeVisited() != null && !place.getTimeVisited().equals("")){
                ivVisited.setImageDrawable(drawable);
                return;
            }
            drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_flag);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date date = new Date();
            place.setTimeVisited(formatter.format(date));
            ivVisited.setImageDrawable(drawable);
        });
        actionHashMap.put(ControlValues.VISITED_POST_FAIL, () -> {
            Toast.makeText(getActivity(), getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
        });

        actionHashMap.put(ControlValues.PLACE_TO_PENDING_VISITED_OK, () -> {
            Toast.makeText(getActivity(), getString(R.string.saved_on_pending_visited_list), Toast.LENGTH_SHORT).show();
        });
        actionHashMap.put(ControlValues.PLACE_TO_PENDING_VISITED_FAIL, () -> {
            Toast.makeText(getActivity(), getString(R.string.saved_on_pending_visited_list_error), Toast.LENGTH_SHORT).show();
        });


    }

    private void initConfig(){

        fillFields();

        //Poner el nombre del lugar en la toolbar
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        actionBar.setTitle(place.getName());

        //Prueba de carga de comentarios
        childFragment = new CommentsFragment(place.getName());
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.comments_placeholder, childFragment).commit();


        //Gestion del Slider View
        sliderAdp = new SliderAdapter(place, getActivity());
        sliderView.setSliderAdapter(sliderAdp);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();
    }

    private void listeners() {

        favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onFavClick(0, lastFavImage);
            }
        });

        ivMapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapboxIntent = new Intent(getActivity(), MapboxActivity.class);
                mapboxIntent.putExtra("placeMapbox", place); //Optional parameters
                getActivity().startActivity(mapboxIntent);
            }
        });

        ivVisited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.setVisitedOnPlace(place, App.getInstance().getUsername());
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

        mViewModel.getmPlace().observe(getViewLifecycleOwner(), new Observer<TPlace>() {
            @Override
            public void onChanged(TPlace place) {
                PlaceDetailFragment .this.place = place;
                initConfig();
            }
        });

        mViewModel.getSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(actionHashMap.containsKey(integer))
                    actionHashMap.get(integer).execute();
            }
        });
    }

    private void fillFields() {

        tvPlaceName.setText(place.getName());
        tvPlaceDescription.setText(place.getName() + "desc :" + place.getDescription());

        tvAddress.setText(place.getAddress());

        String numberOfRatings = place.getNumberOfRatings() + " " + App.getInstance().getAppString(R.string.ratings_text);
        tvNumberOfRatings.setText(numberOfRatings);

        ratingBar.setRating((float) place.getRating());

        String rating = UserInterfaceUtils.rating2UIString(place.getRating());
        tvPlaceRating.setText(rating);

        int favTint = ContextCompat.getColor(getActivity(), R.color.grey);
        if(place.isUserFav()){
            favTint = ContextCompat.getColor(getActivity(), R.color.colorFavRed);
        }

        ImageViewCompat.setImageTintList(favIcon, ColorStateList.valueOf(favTint));

        ViewListenerUtilities.makeTextViewExpandable(tvPlaceDescription, true);

        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_flag_grey);
        if(place.getTimeVisited() != null && !place.getTimeVisited().equals(""))
            drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_flag);

        ivVisited.setImageDrawable(drawable);

        String distance = UserInterfaceUtils.formatDistance(place.getDistanceToUser());
        tvDistance2Place.setText(distance);
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

        ivVisited = root.findViewById(R.id.ivVisitedFlag);

        tvDistance2Place = root.findViewById(R.id.tvPlaceDetailDistance);

        isDescCollapsed = true;

        TooltipCompat.setTooltipText(ivVisited, getString(R.string.mark_as_visited));
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
        modifyPlace.setOnMenuItemClickListener(item -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable(AppConstants.BUNDLE_PLACE_DETAILS, place);
            Navigation.findNavController(root).navigate(R.id.modifyPlaceFragment, bundle);
            return true;
        });
        deletePlace = menu.findItem(R.id.delete_place_menu_button);
        deletePlace.setOnMenuItemClickListener(item -> {
            deleteDialog();
            return true;
        });
        toPendingVisited = menu.findItem(R.id.pending_visited_menu_item);
        toPendingVisited.setOnMenuItemClickListener(item -> {
            mViewModel.placeToPendingVisited(place, App.getInstance().getUsername());
            return true;
        });
        MenuItem recommendPlace = menu.findItem(R.id.recommend_place_menu_item);
        recommendPlace.setOnMenuItemClickListener(item -> {
            onRecomClick();
            return false;
        });

        //Gestión de Botones si eres admin o no
        if(App.getInstance().isLogged()){
            modifyPlace.setVisible(true);
            if(App.getInstance().isAdmin()){
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

        if(App.getInstance().getSessionManager().isLogged() == false){
            Toast.makeText(getActivity(), "Tienes que estar logueado para poder tener favoritos", Toast.LENGTH_SHORT).show();
            return;
        }

        lastFavImage = favImage;

        String username = App.getInstance().getUsername();

        mViewModel.setFavOnPlace(place, username);
    }

    public void onRecomClick() {
        Toast.makeText(getActivity(), "Recom listener", Toast.LENGTH_SHORT).show();

        if(App.getInstance().getSessionManager().isLogged() == false) {
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