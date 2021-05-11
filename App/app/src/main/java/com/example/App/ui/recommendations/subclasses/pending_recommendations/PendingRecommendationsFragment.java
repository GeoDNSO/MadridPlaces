package com.example.App.ui.recommendations.subclasses.pending_recommendations;

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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.App.App;
import com.example.App.R;
import com.example.App.models.TPlace;
import com.example.App.models.TRecommendation;
import com.example.App.ui.recommendations.RecommendationsViewModel;
import com.example.App.utilities.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class PendingRecommendationsFragment extends Fragment implements PendingRecommendationsListAdapter.OnPendingRecommendationsListener{

    private RecommendationsViewModel mViewModel;
    private View root;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;
    private PendingRecommendationsListAdapter pendingRecommendationsListAdapter;
    private List<TRecommendation> recommendationsList;
    private int page = 1, quantum = 3;
    private int listPosition = -1;


    public static PendingRecommendationsFragment newInstance() {
        return new PendingRecommendationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.pending_recommendations_fragment, container, false);

        mViewModel = new ViewModelProvider(this).get(RecommendationsViewModel.class);
        mViewModel.init();

        initUI();
        initListeners();
        initObservers();

        recommendationsList = new ArrayList<>();
        pendingRecommendationsListAdapter = new PendingRecommendationsListAdapter(recommendationsList, this); //getActivity = MainActivity.this
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(pendingRecommendationsListAdapter);

        mViewModel.listUserPendingRecommendations(page, quantum, App.getInstance().getUsername());

        return root;
    }

    private void initUI(){
        recyclerView = root.findViewById(R.id.recommendations_pending_list_recycle_view);
        progressBar = root.findViewById(R.id.recommendations_pending_list_progressBar);
        nestedScrollView = root.findViewById(R.id.recommedations_pending_list_user_nestedScrollView);
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
                    mViewModel.listUserPendingRecommendations(page, quantum, App.getInstance().getUsername());
                }
            }
        });
    }


    private void initObservers(){
        mViewModel.getSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(AppConstants.LIST_REC_OK == integer){

                }
                progressBar.setVisibility(View.GONE);
            }
        });

        mViewModel.getmListPendingRecom().observe(getViewLifecycleOwner(), new Observer<List<TRecommendation>>() {
            @Override
            public void onChanged(List<TRecommendation> tRecomendations) {
                if(tRecomendations == null){
                    Log.d("MY_RECO", "Lista de recomendaciones nula");
                    return;
                }
                recommendationsList = tRecomendations;
                pendingRecommendationsListAdapter = new PendingRecommendationsListAdapter(recommendationsList, PendingRecommendationsFragment.this);
                recyclerView.setAdapter(pendingRecommendationsListAdapter);
                progressBar.setVisibility(View.GONE);
            }
        });

        mViewModel.getmAcceptRecom().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(AppConstants.ACCEPT_REC_OK.equals(integer) && listPosition != -1){
                    Toast.makeText(getContext(), "Se ha aceptado la recomendación", Toast.LENGTH_SHORT).show();
                    TRecommendation recomendation = recommendationsList.get(listPosition);
                    recommendationsList.remove(recomendation);
                    pendingRecommendationsListAdapter = new PendingRecommendationsListAdapter(recommendationsList, PendingRecommendationsFragment.this);
                    recyclerView.setAdapter(pendingRecommendationsListAdapter);
                    progressBar.setVisibility(View.GONE);
                    listPosition = -1;
                }
            }
        });

        mViewModel.getmDenyRecom().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(AppConstants.DENY_REC_OK.equals(integer) && listPosition != -1){
                    Toast.makeText(getContext(), "Se ha rechazado la recomendación", Toast.LENGTH_SHORT).show();
                    TRecommendation recomendation = recommendationsList.get(listPosition);
                    recommendationsList.remove(recomendation);
                    pendingRecommendationsListAdapter = new PendingRecommendationsListAdapter(recommendationsList, PendingRecommendationsFragment.this);
                    recyclerView.setAdapter(pendingRecommendationsListAdapter);
                    progressBar.setVisibility(View.GONE);
                    listPosition = -1;
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RecommendationsViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onPendingRecommendationsClick(int position) {
        Bundle bundle = new Bundle();
        TPlace place = recommendationsList.get(position).getPlace();

        bundle.putParcelable(AppConstants.BUNDLE_PLACE_DETAILS, place);

        Navigation.findNavController(root).navigate(R.id.placeDetailFragment, bundle);
    }

    @Override
    public void onPendingRecommendationsAcceptClick(int position) {
        String placeName = recommendationsList.get(position).getPlace().getName();
        String userOrigin = recommendationsList.get(position).getUserOrigin();
        String userDest = recommendationsList.get(position).getUserDest();
        listPosition = position;
        progressBar.setVisibility(View.VISIBLE);
        mViewModel.acceptPendingRecommendation(placeName, userOrigin, userDest);
    }

    @Override
    public void onPendingRecommendationsDenyClick(int position) {
        String placeName = recommendationsList.get(position).getPlace().getName();
        String userOrigin = recommendationsList.get(position).getUserOrigin();
        String userDest = recommendationsList.get(position).getUserDest();
        listPosition = position;
        progressBar.setVisibility(View.VISIBLE);
        mViewModel.denyPendingRecommendation(placeName, userOrigin, userDest);
    }
}