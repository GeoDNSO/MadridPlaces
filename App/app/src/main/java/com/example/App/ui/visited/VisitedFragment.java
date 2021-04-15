package com.example.App.ui.visited;

import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.App.App;
import com.example.App.R;
import com.example.App.models.transfer.TPlace;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.ViewListenerUtilities;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class VisitedFragment extends Fragment implements VisitedAdapter.OnHistoryListListener{

    private VisitedViewModel mViewModel;
    private View root;

    //UI Elements
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ShimmerFrameLayout shimmerFrameLayout;

    private TextView tvNoLogued;

    private List<TPlace> visitedPlacesList = new ArrayList<>();
    private VisitedAdapter visitedAdapter;

    private int page = 1, quantum = 3;

    public static VisitedFragment newInstance() {
        return new VisitedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(VisitedViewModel.class);
        root = inflater.inflate(R.layout.visited_fragment, container, false);

        mViewModel.init();

        initUI();

        //((MainActivity)getActivity()).setDrawerUnlock();

        if(!App.getInstance().isLogged()){
            tvNoLogued.setVisibility(View.VISIBLE);
            shimmerFrameLayout.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Tienes que estar logueado para poder usar está función", Toast.LENGTH_SHORT).show();
            return root;
        }


        mViewModel.getHistoryPlacesList().observe(getViewLifecycleOwner(), new Observer<List<TPlace>>() {
            @Override
            public void onChanged(List<TPlace> tPlaces) {
                visitedPlacesList = tPlaces;
                visitedAdapter = new VisitedAdapter(getActivity(), visitedPlacesList, VisitedFragment.this);
                recyclerView.setAdapter(visitedAdapter);
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
        if(visitedPlacesList == null){
            visitedPlacesList = new ArrayList<>();
        }
        visitedAdapter = new VisitedAdapter(getActivity(), visitedPlacesList, this); //getActivity = MainActivity.this

        //Set layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(visitedAdapter);

        //getData();

        mViewModel.historyListPlaces(page, quantum, App.getInstance(getContext()).getUsername());

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
                    mViewModel.historyListPlaces(page, quantum, App.getInstance(getContext()).getUsername());
                }
            }
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VisitedViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initUI(){
        nestedScrollView = root.findViewById(R.id.placesList_ScrollView);
        recyclerView = root.findViewById(R.id.History_RecyclerView);
        progressBar = root.findViewById(R.id.placeList_ProgressBar);
        shimmerFrameLayout = root.findViewById(R.id.placeList_ShimmerLayout);

        tvNoLogued = root.findViewById(R.id.tvNoLogued);
    }

    @Override
    public void OnHistoryListClick(int position) {
        Bundle bundle = new Bundle();

        TPlace place = visitedPlacesList.get(position);
        bundle.putParcelable(AppConstants.BUNDLE_PLACE_DETAILS, place);

        //Le pasamos el bundle
        Navigation.findNavController(root).navigate(R.id.placeDetailFragment, bundle);
    }
}