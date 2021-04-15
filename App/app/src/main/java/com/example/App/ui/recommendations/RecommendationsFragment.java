package com.example.App.ui.recommendations;

import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.App.R;
import com.example.App.models.dao.SimpleRequest;
import com.example.App.models.transfer.TPlace;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecommendationsFragment extends Fragment {

    private RecommendationsViewModel mViewModel;
    private View root;
    protected List<TPlace> placeList = new ArrayList<>();

    //UI elements
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected NestedScrollView nestedScrollView;
    protected RecyclerView recyclerView;
    protected ProgressBar progressBar;
    protected ShimmerFrameLayout shimmerFrameLayout;

    protected int page = 1, limit = 3, quantum = 3;

    public static RecommendationsFragment newInstance() {
        return new RecommendationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(RecommendationsViewModel.class);
        root = inflater.inflate(R.layout.places_list_fragment, container, false);
        setHasOptionsMenu(true);

        initUI();
        initializeListeners();

        //Para no estar todo el rato recargando lugares
        // ,es decir, generando nuevos lugares cada dez que volvemos al fragmento home

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RecommendationsViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_icon_menu, menu);

        //Ocultar el boton de AZ
        MenuItem azIcon = menu.findItem(R.id.sortListUsers);
        azIcon.setVisible(false);

        //Boton de busqueda
        MenuItem searchIcon = menu.findItem(R.id.search_button);
        SearchView searchView = (SearchView) searchIcon.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initializeListeners() {
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
                    //mViewModel.listPlaces(page, quantum, App.getInstance(getContext()).getUsername());
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
                    //mViewModel.listPlaces(page, quantum, App.getInstance(getContext()).getUsername());
                }
            }
        });
    }

    private void initUI(){
        nestedScrollView = root.findViewById(R.id.placesList_ScrollView);
        recyclerView = root.findViewById(R.id.PlaceList_RecyclerView);
        progressBar = root.findViewById(R.id.placeList_ProgressBar);
        shimmerFrameLayout = root.findViewById(R.id.placeList_ShimmerLayout);
        swipeRefreshLayout = root.findViewById(R.id.placesList_SwipeRefreshLayout);
    }
}