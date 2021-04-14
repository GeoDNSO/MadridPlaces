package com.example.App.ui.places_list.subclasses;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.App.App;
import com.example.App.R;
import com.example.App.models.dao.SimpleRequest;
import com.example.App.models.transfer.TPlace;
import com.example.App.ui.LogoutObserver;
import com.example.App.ui.places_list.PlaceListAdapter;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.ViewListenerUtilities;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BasePlaces extends Fragment implements PlaceListAdapter.OnPlaceListener, LogoutObserver {

    protected BaseViewModel mViewModel;
    protected View root;

    private MenuItem search_place;
    private MenuItem mic_search_place;
    private MenuItem addPlace;

    private SearchView searchView;

    //UI Elements
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected NestedScrollView nestedScrollView;
    protected RecyclerView recyclerView;
    protected ProgressBar progressBar;
    protected ShimmerFrameLayout shimmerFrameLayout;

    protected List<TPlace> placeList = new ArrayList<>();
    protected PlaceListAdapter placeListAdapter;

    protected int page = 1, limit = 3, quantum = 3;

    protected String search_text = "";


    //Funciones a implementar en los hijos según el tipo de lugares a mostrar
    public abstract void listPlaces();
    public abstract BaseViewModel getViewModelToParent();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mViewModel = new ViewModelProvider(this).get(PlacesListViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.places_list_fragment, container, false);
        //mViewModel = new ViewModelProvider(this).get(BaseViewModel.class);
        setHasOptionsMenu(true);
        App.getInstance(getContext()).addLogoutObserver(this);

        mViewModel = getViewModelToParent();
        mViewModel.init();

        page = 1;

        initUI();
        initListener();
        initObservers();

        placeListManagement();

        return root;
    }

    private void initObservers() {

        mViewModel.getPlacesList().observe(getViewLifecycleOwner(), new Observer<List<TPlace>>() {
            @Override
            public void onChanged(List<TPlace> tPlaces) {
                if(tPlaces == null){
                    Log.d("ERROR_NULO", "tPLaces nulo");
                    return;
                }

                placeList = tPlaces; //TODO Aquí hay un bug que hay que arreglar

                placeListAdapter = new PlaceListAdapter(getActivity(), placeList, BasePlaces.this);

                recyclerView.setAdapter(placeListAdapter);
            }
        });

        mViewModel.getSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                //Mostrar el recyclerView
                recyclerView.setVisibility(View.VISIBLE);
                //Parar el efecto shimmer
                shimmerFrameLayout.stopShimmer();
                //Esconder al frameLayout de shimmer
                shimmerFrameLayout.setVisibility(View.GONE);

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        mViewModel.getFavSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                if (integer.equals(AppConstants.FAV_POST_OK)){
                    TPlace place = placeList.get(lastFavPlacePos);
                    place.setUserFav(!place.isUserFav());

                    int favTint = ContextCompat.getColor(getActivity(), R.color.grey);
                    if(place.isUserFav()){
                        favTint = ContextCompat.getColor(getActivity(), R.color.colorFavRed);
                    }

                    ImageViewCompat.setImageTintList(lastFavImage, ColorStateList.valueOf(favTint));
                    return ;
                }

                Toast.makeText(getActivity(), "Error al hacer favorito", Toast.LENGTH_SHORT);
            }
        });

        mViewModel.getProgressBar().observe(getViewLifecycleOwner(), aBoolean ->
                ViewListenerUtilities.setVisibility(progressBar, aBoolean));
    }

    private void initListener() {
        Activity activity = getActivity();
        String s = getString(R.string.no_internet);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Runnable task = ()->{
                    boolean hostReachable =  SimpleRequest.isHostReachable();

                    if(!hostReachable){
                        swipeRefreshLayout.setRefreshing(false);
                        Log.i("IN_SER", "Server no alcanzable");
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, s, Toast.LENGTH_LONG).show();
                            }
                        });
                        return ; //IMP
                    }
                    //Reseteamos la pagina para ver cambios y borramos la lista...
                    page = 1;
                    placeList.clear();
                    //mViewModel.listPlaces(page, quantum, App.getInstance(getContext()).getUsername());
                    search_text = "";
                    listPlaces();
                };

                ExecutorService executorService = Executors.newFixedThreadPool(1);
                executorService.submit(task);

            }
        });

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                    //Cuando alacance al ultimo item de la lista
                    //Incrementea el numero de la pagina
                    page++;
                    //Mostrar progress bar
                    progressBar.setVisibility(View.VISIBLE);

                    shimmerFrameLayout.startShimmer();

                    shimmerFrameLayout.setVisibility(View.VISIBLE);

                    //Pedimos más datos
                    listPlaces();
                }
            }
        });
    }

    private void placeListManagement(){
        if(placeList == null){
            placeList = new ArrayList<>();
        }
        placeListAdapter = new PlaceListAdapter(getActivity(), placeList, this); //getActivity = MainActivity.this
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(placeListAdapter);

        listPlaces();

        //Empezar el efecto de shimmer
        shimmerFrameLayout.startShimmer();
    }


    private void initUI(){
        nestedScrollView = root.findViewById(R.id.placesList_ScrollView);
        recyclerView = root.findViewById(R.id.PlaceList_RecyclerView);
        progressBar = root.findViewById(R.id.placeList_ProgressBar);
        shimmerFrameLayout = root.findViewById(R.id.placeList_ShimmerLayout);
        swipeRefreshLayout = root.findViewById(R.id.placesList_SwipeRefreshLayout);
    }

    @Override
    public void onPlaceClick(int position) {
        //Enviar datos del objeto con posicion position de la lista al otro fragment
        TPlace place = placeList.get(position);

        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.BUNDLE_PLACE_DETAILS, place);

        //Le pasamos el bundle
        Navigation.findNavController(root).navigate(R.id.placeDetailFragment, bundle);
    }

    @Override
    public void onLogout(){
        addPlace.setVisible(false);
        placeList.clear();
        page=1;
        listPlaces();
    }


    protected Integer lastFavPlacePos;
    protected ImageView lastFavImage;
    @Override
    public void onFavClick(int position, ImageView favImage) {
        Toast.makeText(getActivity(), "fav listener", Toast.LENGTH_SHORT).show();

        if(App.getInstance(getActivity()).getSessionManager().isLogged() == false){
            Toast.makeText(getActivity(), "Tienes que estar logueado para poder tener favoritos", Toast.LENGTH_SHORT).show();
            return;
        }

        lastFavPlacePos = position;
        lastFavImage = favImage;


        TPlace place = placeList.get(position);
        String username = App.getInstance(getActivity()).getUsername();

        mViewModel.setFavOnPlace(place, username);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_icon_menu, menu);

        MenuItem azIcon = menu.findItem(R.id.sortListUsers);
        azIcon.setVisible(false);

        search_place = menu.findItem(R.id.search_button);
        mic_search_place = menu.findItem(R.id.microphone_button);

        searchView = (SearchView) search_place.getActionView();

        searchView.setMaxWidth(600);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                page = 1;
                progressBar.setVisibility(View.VISIBLE);
                placeList.clear();
                mViewModel.listPlaces(page, quantum, App.getInstance(getContext()).getUsername(), newText);
                search_text = newText;
                //adapter.getFilter().filter(newText);
                return false;
            }
        });

        mic_search_place.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");
                try {
                    startActivityForResult(intent, AppConstants.RESULT_SPEECH);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(getContext(), "Error: No se ha podido conectar con el micrófono", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                return true;
            }
        });

        inflater.inflate(R.menu.add_place_menu, menu);

        addPlace = menu.findItem(R.id.add_place_menu_item);
        if(App.getInstance(getActivity()).isLogged()){
            addPlace.setVisible(true);
        }
        else {
            addPlace.setVisible(false);
        }

        addPlace.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Navigation.findNavController(root).navigate(R.id.addPlaceFragment);
                return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case AppConstants.RESULT_SPEECH:
                if (resultCode == Activity.RESULT_OK && data != null){
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    page = 1;
                    progressBar.setVisibility(View.VISIBLE);
                    placeList.clear();
                    mViewModel.listPlaces(page, quantum, App.getInstance(getContext()).getUsername(), text.get(0));
                    search_text = text.get(0);
                }
                break;
        }
    }

}
