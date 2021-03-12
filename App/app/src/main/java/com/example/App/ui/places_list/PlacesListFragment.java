package com.example.App.ui.places_list;

import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.App.App;
import com.example.App.R;
import com.example.App.models.dao.SimpleRequest;
import com.example.App.models.transfer.TPlace;
import com.example.App.utilities.AppConstants;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PlacesListFragment extends Fragment implements PlaceListAdapter.OnPlaceListener {

    private PlacesListViewModel mViewModel;
    private View root;

    //UI Elements
    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ShimmerFrameLayout shimmerFrameLayout;

    private List<TPlace> placeList = new ArrayList<>();
    private PlaceListAdapter placeListAdapter;

    private int page = 1, limit = 3, quantum = 3;

    public static PlacesListFragment newInstance() {
        return new PlacesListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.places_list_fragment, container, false);

        mViewModel = new ViewModelProvider(this).get(PlacesListViewModel.class);
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

                placeListAdapter = new PlaceListAdapter(getActivity(), placeList, PlacesListFragment.this);

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

        mViewModel.getProgressBar().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    progressBar.setVisibility(View.VISIBLE);
                }
                else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
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
                    mViewModel.listPlaces(page, quantum);
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
                    mViewModel.appendPlaces(page, quantum);
                }
            }
        });
    }

    private void placeListManagement(){
        if(placeList == null){
            placeList = new ArrayList<>();
        }
        placeListAdapter = new PlaceListAdapter(getActivity(), placeList, this); //getActivity = MainActivity.this

        //Set layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(placeListAdapter);

        mViewModel.listPlaces(page, quantum);

        //Empezar el efecto de shimmer
        shimmerFrameLayout.startShimmer();

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mViewModel = new ViewModelProvider(this).get(PlacesListViewModel.class);
        // TODO: Use the ViewModel
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

}