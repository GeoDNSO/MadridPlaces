package com.example.App.ui.home;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentTransaction;
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
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.App.App;
import com.example.App.R;
import com.example.App.models.transfer.TPlace;
import com.example.App.ui.places_list.PlaceListAdapter;
import com.example.App.ui.places_list.PlacesListFragment;
import com.example.App.utilities.AppConstants;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment{

    private View root;
    private HomeViewModel mViewModel;

    private Button btn_register;
    private Button btn_login;
    private Button btn_logout;

    private App app; //global variable

    private Fragment placeListFragment;


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.home_fragment, container, false);
        app = App.getInstance(getActivity());

        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        initializeUI();
        initializeListeners();

        viewAccToUser();

        //Para no estar todo el rato recargando lugares
        // ,es decir, generando nuevos lugares cada dez que volvemos al fragmento home
        if(placeListFragment == null){
            placeListFragment = new PlacesListFragment();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.place_list_container, placeListFragment).commit();
        }


        return root;
    }

    //View according to the user's status (logged or not)
    private void viewAccToUser() {

        if (app.isLogged()) {
            btn_register.setVisibility(View.GONE);
            btn_login.setVisibility(View.GONE);
            btn_logout.setVisibility(View.VISIBLE);
            app.menuOptions(app.isLogged(), app.isAdmin());
        }
        else {
            btn_register.setVisibility(View.VISIBLE);
            btn_login.setVisibility(View.VISIBLE);
            btn_logout.setVisibility(View.GONE);
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
        //Insertar fragment de lista de lugares a home
    }

    private void initializeListeners() {
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_registerFragment);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_loginFragment);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.logout();
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_loginFragment);
            }
        });
    }

    private void initializeUI() {
        btn_register = root.findViewById(R.id.home_register_button);
        btn_login = root.findViewById(R.id.home_login_button);
        btn_logout = root.findViewById(R.id.home_logout_button);
    }
}