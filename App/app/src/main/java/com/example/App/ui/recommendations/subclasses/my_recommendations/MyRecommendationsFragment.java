package com.example.App.ui.recommendations.subclasses.my_recommendations;

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
import com.example.App.models.TRecommendation;
import com.example.App.ui.recommendations.RecommendationsViewModel;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.ControlValues;
import com.example.App.utilities.OnResultAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyRecommendationsFragment extends Fragment implements MyRecommendationsAdapter.RecommendationAdapterListener{

    private View root;
    private RecommendationsViewModel mViewModel;
    private HashMap<Integer, OnResultAction> actionHashMap;

    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private List<TRecommendation> recommendationList;

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
        configOnResultActions();

        recommendationList = new ArrayList<>();
        myRecommendationsAdapter = new MyRecommendationsAdapter(getActivity(), recommendationList, this);

        //Set layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(myRecommendationsAdapter);

        progressBar.setVisibility(View.VISIBLE);

        mViewModel.listUserRecommendations(page, quantum, App.getInstance().getUsername());

        return root;
    }

    private void configOnResultActions() {
        actionHashMap = new HashMap<>();
        actionHashMap.put(ControlValues.LIST_REC_OK, () -> {
            Toast.makeText(getActivity(), getString(R.string.recommendation_list_loaded_success), Toast.LENGTH_SHORT).show();
        });

        actionHashMap.put(ControlValues.LIST_REC_FAIL, () -> {
            Toast.makeText(getActivity(), getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
        });
    }

    private void initObservers() {
        mViewModel.getSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(actionHashMap.containsKey(integer))
                    actionHashMap.get(integer).execute();
                progressBar.setVisibility(View.GONE);
            }
        });

        mViewModel.getmListRecom().observe(getViewLifecycleOwner(), new Observer<List<TRecommendation>>() {
            @Override
            public void onChanged(List<TRecommendation> tRecommendations) {
                if(tRecommendations == null){
                    Log.d("MY_RECO", "Lista de recomendaciones nula");
                    return;
                }
                recommendationList = tRecommendations;
                myRecommendationsAdapter = new MyRecommendationsAdapter(getActivity(), recommendationList, MyRecommendationsFragment.this);
                recyclerView.setAdapter(myRecommendationsAdapter);
                progressBar.setVisibility(View.GONE);
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

                    //Pedimos más datos
                    mViewModel.listUserRecommendations(page, quantum, App.getInstance().getUsername());
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


    @Override
    public void onSpanClick(String placeName) {
        Toast.makeText(getContext(), "Funciona", Toast.LENGTH_SHORT).show();
        //Enviar datos del objeto con posicion position de la lista al otro fragment

        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.BUNDLE_PLACE_NAME_PLACE_DETAILS, placeName);

        //Le pasamos el bundle
        Navigation.findNavController(root).navigate(R.id.placeDetailFragment, bundle);
    }
}