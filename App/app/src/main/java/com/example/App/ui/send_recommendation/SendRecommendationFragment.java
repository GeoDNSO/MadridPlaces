package com.example.App.ui.send_recommendation;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.App.App;
import com.example.App.R;
import com.example.App.models.TPlace;
import com.example.App.models.TRequestFriend;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.ControlValues;
import com.example.App.utilities.OnResultAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SendRecommendationFragment extends Fragment implements SendRecommendationAdapter.SendRecomendationActionListener {

    private View root;
    private SendRecommendationViewModel mViewModel;
    private TPlace place;
    private HashMap<Integer, OnResultAction> actionHashMap;

    private SendRecommendationAdapter sendRecomendationAdapter;

    //Elementos visuales
    private TextView no_results;
    private RecyclerView sendRecommendationRecycleView;
    private MenuItem sendRecommendation;

    private List<TRequestFriend> friendList;

    private App app;

    public static SendRecommendationFragment newInstance() {
        return new SendRecommendationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.send_recomendation_fragment, container, false);

        setHasOptionsMenu(true);

        mViewModel = new ViewModelProvider(this).get(SendRecommendationViewModel.class);
        mViewModel.init();

        initializeUI();
        observers();
        configOnResultActions();

        place = (TPlace) getArguments().getParcelable(AppConstants.BUNDLE_PLACE_DETAILS);

        friendList = new ArrayList<>();
        sendRecomendationAdapter = new SendRecommendationAdapter(getActivity(), friendList, this, sendRecommendation); //getActivity = MainActivity.this
        sendRecommendationRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sendRecommendationRecycleView.setAdapter(sendRecomendationAdapter);

        mViewModel.friendList(App.getInstance().getUsername());

        return root;
    }

    private void configOnResultActions() {
        actionHashMap = new HashMap<>();
        actionHashMap.put(ControlValues.SEND_REC_OK, () -> {
            Toast.makeText(getActivity(), getString(R.string.recommendation_sent), Toast.LENGTH_SHORT).show();
        });

        actionHashMap.put(ControlValues.SEND_REC_FAIL, () -> {
            Toast.makeText(getActivity(), getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
        });
    }

    private void observers() {
        mViewModel.getSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(actionHashMap.containsKey(integer))
                    actionHashMap.get(integer).execute();
            }
        });


        mViewModel.getmFriendList().observe(getViewLifecycleOwner(), new Observer<List<TRequestFriend>>() {
            @Override
            public void onChanged(List<TRequestFriend> tRequestFriends) {
                friendList = tRequestFriends;
                if(friendList.size() == 0){
                    no_results.setVisibility(View.VISIBLE);
                }
                else{
                    no_results.setVisibility(View.GONE);
                }
                sendRecomendationAdapter = new SendRecommendationAdapter(getActivity(), tRequestFriends, SendRecommendationFragment.this, sendRecommendation);
                sendRecommendationRecycleView.setAdapter(sendRecomendationAdapter);
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SendRecommendationViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initializeUI() {
        no_results = root.findViewById(R.id.tv_send_recomendation_no_results);
        sendRecommendationRecycleView = root.findViewById(R.id.recyclerView_send_recomendation);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.recommendations_menu, menu);

        sendRecommendation = menu.findItem(R.id.add_recommendation_menu_item);

        sendRecommendation.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sendRecommendationAction();
                return true;
            }
        });
    }

    private void sendRecommendationAction(){
        app = App.getInstance();
        List<String> userList = sendRecomendationAdapter.getListSelected();
        String userOrigin = app.getUsername();
        String placeName = place.getName();
        for(String userDest: userList) {
            mViewModel.sendRecomendation(userOrigin, userDest, placeName);
        }
    }

    @Override
    public void onListClick(int position) {

    }
}