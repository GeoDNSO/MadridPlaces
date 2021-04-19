package com.example.App.ui.recommendations.subclasses.my_recommendations;

import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.App.App;
import com.example.App.R;
import com.example.App.models.transfer.TRecomendation;
import com.example.App.ui.place_details.PlaceDetailViewModel;
import com.example.App.ui.recommendations.RecommendationsViewModel;
import com.example.App.utilities.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class MyRecommendationsFragment extends Fragment {

    private View root;
    private RecommendationsViewModel mViewModel;

    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private List<TRecomendation> recommendationList;

    private MyRecommendationsAdapter myRecommendationsAdapter;

    protected int page = 1, limit = 3, quantum = 8;


    public static MyRecommendationsFragment newInstance() {
        return new MyRecommendationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.my_recommendations_fragment, container, false);

        mViewModel = new ViewModelProvider(this).get(RecommendationsViewModel.class);
        mViewModel.init();

        initUI();
        initListeners();
        initObservers();

        recommendationList = new ArrayList<>();
        myRecommendationsAdapter = new MyRecommendationsAdapter(getActivity(), recommendationList);

        //Set layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(myRecommendationsAdapter);

        progressBar.setVisibility(View.VISIBLE);

        return root;
    }

    private void initObservers() {
        mViewModel.getSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(AppConstants.LIST_REC_OK == integer){

                }
                progressBar.setVisibility(View.GONE);
            }
        });
        mViewModel.getmListRecom().observe(getViewLifecycleOwner(), new Observer<List<TRecomendation>>() {
            @Override
            public void onChanged(List<TRecomendation> tRecomendations) {
                if(tRecomendations == null){
                    Log.d("MY_RECO", "Lista de recomendaciones nula");
                    return;
                }
                recommendationList = tRecomendations;
                myRecommendationsAdapter = new MyRecommendationsAdapter(getActivity(), recommendationList);
                recyclerView.setAdapter(myRecommendationsAdapter);
            }
        });
    }

    private void initListeners() {

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (v.getChildAt(0).getBottom() <= (v.getHeight() + v.getScrollY())) {
                    page++;
                    //Mostrar progress bar
                    progressBar.setVisibility(View.VISIBLE);

                    /*
                    shimmerFrameLayout.startShimmer();

                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                     */

                    //Pedimos más datos
                    mViewModel.getmListRecom();
                }
            }
        });
    }

    private void initUI() {
        nestedScrollView = root.findViewById(R.id.my_recommendations_NestedScroll);
        progressBar = root.findViewById(R.id.my_recommendations_progressBar);
        recyclerView = root.findViewById(R.id.my_recommendations_RecyclerView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RecommendationsViewModel.class);
        // TODO: Use the ViewModel
    }



}