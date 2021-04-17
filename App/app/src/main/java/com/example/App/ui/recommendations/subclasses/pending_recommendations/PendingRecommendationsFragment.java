package com.example.App.ui.recommendations.subclasses.pending_recommendations;

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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.App.R;
import com.example.App.models.transfer.TPlace;
import com.example.App.models.transfer.TRecomendation;
import com.example.App.ui.places_list.PlaceListAdapter;
import com.example.App.utilities.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class PendingRecommendationsFragment extends Fragment implements PendingRecommendationsListAdapter.OnPendingRecommendationsListener{

    private PendingRecommendationsViewModel mViewModel;
    private View root;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private PendingRecommendationsListAdapter pendingRecommendationsListAdapter;
    private List<TPlace> placeList;
    private List<TRecomendation> recommendationsList;


    public static PendingRecommendationsFragment newInstance() {
        return new PendingRecommendationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.pending_recommendations_fragment, container, false);

        initUI();
        initListeners();
        initObservers();
        loadView();

        return root;
    }

    private void initUI(){
        recyclerView = root.findViewById(R.id.recommendations_pending_list_recycle_view);
        progressBar = root.findViewById(R.id.recommendations_pending_list_progressBar);
    }

    private void initListeners(){

    }

    private void initObservers(){

    }

    private void loadView(){
        if(placeList == null){
            placeList = new ArrayList<>();
            placeList.add(new TPlace("a", "a", 2.6, 4.5, new ArrayList<>(), "", "", "", "", "", "", "", 2.2, true, 2.2, 2, ""));
        }
        if(recommendationsList == null){
            recommendationsList = new ArrayList<>();
            recommendationsList.add(new TRecomendation("ad", "sds", "a", "pending"));
        }
        pendingRecommendationsListAdapter = new PendingRecommendationsListAdapter(placeList, recommendationsList, this); //getActivity = MainActivity.this
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(pendingRecommendationsListAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PendingRecommendationsViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onPendingRecommendationsClick(int position) {
        Bundle bundle = new Bundle();
        TPlace place = placeList.get(position);

        bundle.putParcelable(AppConstants.BUNDLE_PLACE_DETAILS, place);

        Navigation.findNavController(root).navigate(R.id.placeDetailFragment, bundle);
    }
}