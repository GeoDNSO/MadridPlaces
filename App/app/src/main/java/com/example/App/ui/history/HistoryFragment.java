package com.example.App.ui.history;

import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.App.MainActivity;
import com.example.App.R;
import com.example.App.models.transfer.TPlace;
import com.example.App.ui.admin.AdminViewModel;
import com.example.App.ui.places_list.PlaceListAdapter;
import com.example.App.ui.places_list.PlacesListFragment;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.ViewListenerUtilities;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment implements HistoryAdapter.OnHistoryListListener{

    private HistoryViewModel mViewModel;
    private View root;

    //UI Elements
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ShimmerFrameLayout shimmerFrameLayout;

    private List<TPlace> historyplaceList = new ArrayList<>();
    private HistoryAdapter historyplaceListAdapter;

    private int page = 1, quantum = 3;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        root = inflater.inflate(R.layout.history_fragment, container, false);

        mViewModel.init();

        initUI();

        //((MainActivity)getActivity()).setDrawerUnlock();

        mViewModel.getHistoryPlacesList().observe(getViewLifecycleOwner(), new Observer<List<TPlace>>() {
            @Override
            public void onChanged(List<TPlace> tPlaces) {
                historyplaceList = tPlaces;
                historyplaceListAdapter = new HistoryAdapter(getActivity(), historyplaceList, HistoryFragment.this);
                recyclerView.setAdapter(historyplaceListAdapter);
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
            }
        });

        mViewModel.getProgressBar().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                ViewListenerUtilities.setVisibility(progressBar, aBoolean);
            }
        });

        placeListManagement();

        return root;
    }

    private void placeListManagement(){
        if(historyplaceList == null){
            historyplaceList = new ArrayList<>();
        }
        historyplaceListAdapter = new HistoryAdapter(getActivity(), historyplaceList, this); //getActivity = MainActivity.this

        //Set layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(historyplaceListAdapter);

        //getData();

        mViewModel.historyListPlaces(page, quantum);

        //Empezar el efecto de shimmer
        shimmerFrameLayout.startShimmer();

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                    //Cuando alacance al ultimo item de la lista
                    //Incrementea el numero de la pagina
                    page++;
                    //Mostrar progress bar
                    progressBar.setVisibility(View.VISIBLE);

                    //Pedimos más datos
                    mViewModel.appendPlaces(page, quantum);
                }
            }
        });
    }

    //@TODO Ver video para enseñar a Jin lo de las peticiones por paginas...
    //Simula llamada al servidor
    static int numLugar = 0;
    private void getData() {

        //Si la respuesta no es nula, es decir, recibimos mensaje del servidor
        if(true){
            historyplaceListAdapter = new HistoryAdapter(getActivity(), historyplaceList, this);

            recyclerView.setAdapter(historyplaceListAdapter);
        }else{
            //Mostrar mensaje de error o trasladar mensaje de error a la vista
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initUI(){
        nestedScrollView = root.findViewById(R.id.placesList_ScrollView);
        recyclerView = root.findViewById(R.id.History_RecyclerView);
        progressBar = root.findViewById(R.id.placeList_ProgressBar);
        shimmerFrameLayout = root.findViewById(R.id.placeList_ShimmerLayout);
    }

    @Override
    public void OnHistoryListClick(int position) {
        Bundle bundle = new Bundle();

        TPlace place = historyplaceList.get(position);
        bundle.putParcelable(AppConstants.BUNDLE_PLACE_DETAILS, place);

        //Le pasamos el bundle
        Navigation.findNavController(root).navigate(R.id.placeDetailFragment, bundle);
    }
}