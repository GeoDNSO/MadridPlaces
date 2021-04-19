package com.example.App.ui.recommendations.subclasses.my_recommendations;

import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.App.App;
import com.example.App.R;
import com.example.App.models.transfer.TRecomendation;
import com.example.App.utilities.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class MyRecommendationsFragment extends Fragment {

    private View root;
    private MyRecommendationsViewModel mViewModel;

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

        initUI();
        initListeners();

        recommendationList = new ArrayList<>();
        myRecommendationsAdapter = new MyRecommendationsAdapter(getActivity(), recommendationList);

        //Set layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(myRecommendationsAdapter);

        progressBar.setVisibility(View.VISIBLE);
        generateExamples();

        return root;
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
                    generateExamples();
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
        mViewModel = new ViewModelProvider(this).get(MyRecommendationsViewModel.class);
        // TODO: Use the ViewModel
    }

    private void generateExamples(){

        for (int i = 0; i < quantum; ++i){
            String suffix = "P"+page +"I"+ i;
            String userOrigin = "userOrigin" + suffix, userDest = "userDest"+suffix, place = "placeNum"+suffix;
            String state = "";

            if(i%2 == 0)
                state = AppConstants.STATE_ACCEPTED;
            else if(i%3 == 0)
                state = AppConstants.STATE_PENDING;
            else if(i > 8)
                state = "asdasd"; //Da igual el valor, saldrá rechazado

            TRecomendation recomendation = new TRecomendation(userOrigin, userDest, place, state);
            recommendationList.add(recomendation);
        }
        myRecommendationsAdapter = new MyRecommendationsAdapter(getActivity(), recommendationList);
        recyclerView.setAdapter(myRecommendationsAdapter);

        progressBar.setVisibility(View.GONE);
    }

}