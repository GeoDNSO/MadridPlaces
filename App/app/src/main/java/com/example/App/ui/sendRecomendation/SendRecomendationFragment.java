package com.example.App.ui.sendRecomendation;

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

import java.util.ArrayList;
import java.util.List;

public class SendRecomendationFragment extends Fragment implements SendRecomendationAdapter.SendRecomendationActionListener {

    private View root;
    private SendRecomendationViewModel mViewModel;
    private TPlace place;

    private SendRecomendationAdapter sendRecomendationAdapter;

    //Elementos visuales
    private TextView no_results;
    private RecyclerView sendRecomendationRecycleView;
    private MenuItem sendRecommendation;

    private List<TRequestFriend> friendList;

    private App app;

    public static SendRecomendationFragment newInstance() {
        return new SendRecomendationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.send_recomendation_fragment, container, false);

        setHasOptionsMenu(true);

        mViewModel = new ViewModelProvider(this).get(SendRecomendationViewModel.class);
        mViewModel.init();

        viewModelListeners();

        initializeUI();


        place = (TPlace) getArguments().getParcelable(AppConstants.BUNDLE_PLACE_DETAILS);

        friendList = new ArrayList<>();
        sendRecomendationAdapter = new SendRecomendationAdapter(getActivity(), friendList, this, sendRecommendation); //getActivity = MainActivity.this
        sendRecomendationRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sendRecomendationRecycleView.setAdapter(sendRecomendationAdapter);

        mViewModel.friendList(App.getInstance().getUsername());

        return root;
    }

    private void viewModelListeners() {
        mViewModel.getSendingSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer.equals(AppConstants.SEND_REC_OK)){
                    Toast.makeText(getActivity(), "ENVIADA!", Toast.LENGTH_SHORT).show();
                }
                //TODO Si pones un else, no entra
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
                sendRecomendationAdapter = new SendRecomendationAdapter(getActivity(), tRequestFriends, SendRecomendationFragment.this, sendRecommendation);
                sendRecomendationRecycleView.setAdapter(sendRecomendationAdapter);
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SendRecomendationViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initializeUI() {
        no_results = root.findViewById(R.id.tv_send_recomendation_no_results);
        sendRecomendationRecycleView = root.findViewById(R.id.recyclerView_send_recomendation);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.recommendations_menu, menu);

        sendRecommendation = menu.findItem(R.id.add_recommendation_menu_item);


        sendRecommendation.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sendRecomendationAction();
                return true;
            }
        });
    }

    private void sendRecomendationAction(){
        app = App.getInstance(getActivity());
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