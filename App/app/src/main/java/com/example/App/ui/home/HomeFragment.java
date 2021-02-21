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

import android.util.AttributeSet;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.App.App;
import com.example.App.R;
import com.example.App.SessionManager;
import com.example.App.models.transfer.TPlace;
import com.example.App.ui.places_list.PlaceListAdapter;
import com.example.App.ui.places_list.PlacesList;
import com.example.App.utilities.AppConstants;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements PlaceListAdapter.OnPlaceListener {

    private View root;
    private HomeViewModel mViewModel;

    private Button btn_register;
    private Button btn_login;
    private Button btn_logout;

    private App app; //global variable

    //Parte de la lista
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ShimmerFrameLayout shimmerFrameLayout;

    private List<TPlace> placeList = new ArrayList<>();
    private PlaceListAdapter placeListAdapter;

    private int page = 1, limit = 3;

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

        placeListManagement();
        /*
        Fragment placeListFragment = new PlacesList();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.home_NestedScrollView, placeListFragment).commit();
        */
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

        //parte de la lista
        nestedScrollView = root.findViewById(R.id.placesList_ScrollView);
        recyclerView = root.findViewById(R.id.PlaceList_RecyclerView);
        progressBar = root.findViewById(R.id.placeList_ProgressBar);
        shimmerFrameLayout = root.findViewById(R.id.placeList_ShimmerLayout);
    }

    private void placeListManagement(){
        placeListAdapter = new PlaceListAdapter(getActivity(), placeList, this); //getActivity = MainActivity.this

        //Set layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));//getActivity() en vez de this
        recyclerView.setAdapter(placeListAdapter);

        getData();

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
                    getData();
                }
            }
        });
    }

    //Simula llamada al servidor
    private void getData() {

        //NO recogemos mas datos al llegar a la pagina 5
        if(page >= 5){
            return ;
        }
        //Si la respuesta no es nula, es decir, recibimos mensaje del servidor
        if(true){

            //Esconder la barra de carga
            progressBar.setVisibility(View.GONE);
            //Mostrar el recyclerView
            recyclerView.setVisibility(View.VISIBLE);
            //Parar el efecto shimmer
            shimmerFrameLayout.stopShimmer();
            //Esconder al frameLayout de shimmer
            shimmerFrameLayout.setVisibility(View.GONE);

            //Usar la respuesta del servidor para ir creando los lugares de la lista
            for(int i = 0; i < limit; ++i){
                float rate = (float) Math.random()*6 + 1;
                TPlace place = new TPlace("Lugar", "Desc", "IMAGEN DEFAULT", rate);
                //getString(R.drawable.imagen_lugar_default);
                //Añadir lugar a la lista
                placeList.add(place);
            }
            placeListAdapter = new PlaceListAdapter(getActivity(), placeList, this);

            recyclerView.setAdapter(placeListAdapter);
        }else{
            //Mostrar mensaje de error o trasladar mensaje de error a la vista
        }
    }


    @Override
    public void onPlaceClick(int position) {
        //Enviar datos del objeto con posicion position de la lista al otro fragment
        //Toast.makeText(getActivity(), "Listener del item " + position, Toast.LENGTH_LONG).show();
        Bundle bundle = new Bundle();
        TPlace place = new TPlace("Lugar en Posicion "+position, "descripcion", "direccion",
                3.0f, 3.0f, "/imagen", "tipodelugar", "Madrid",
                "Localidad", "Afluencia", 4.0f);

        bundle.putParcelable(AppConstants.BUNDLE_PLACE_DETAILS, place);

        //Le pasamos el bundle
        Navigation.findNavController(root).navigate(R.id.placeDetailFragment, bundle);
    }
}