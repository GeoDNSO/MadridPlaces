package com.example.App.ui.places_list;

import androidx.core.widget.NestedScrollView;
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
import android.widget.Toast;

import com.example.App.R;
import com.example.App.models.transfer.TPlace;
import com.example.App.utilities.AppConstants;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class PlacesListFragment extends Fragment implements PlaceListAdapter.OnPlaceListener {

    private PlacesListViewModel mViewModel;
    private View root;

    //UI Elements
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ShimmerFrameLayout shimmerFrameLayout;

    private List<TPlace> placeList = new ArrayList<>();
    private PlaceListAdapter placeListAdapter;

    private int page = 1, limit = 6;

    public static PlacesListFragment newInstance() {
        return new PlacesListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.places_list_fragment, container, false);

        initUI();

        placeListManagement();

        return root;
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

    //@TODO Ver video para enseñar a Jin lo de las peticiones por paginas...
    //Simula llamada al servidor
    static int numLugar = 0;
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
                float rate = (float) Math.random()*5 + 1;
                //TPlace place = new TPlace("Lugar " + numLugar++, getString(R.string.lorem_ipsu), "IMAGEN DEFAULT", rate);
                TPlace place = new TPlace("Lugar Pos " + numLugar++, getString(R.string.lorem_ipsu), "direccion",
                        3.0f, 3.0f, "/imagen", "tipodelugar", "Madrid",
                        "Localidad", "Afluencia", rate, false);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PlacesListViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initUI(){
        nestedScrollView = root.findViewById(R.id.placesList_ScrollView);
        recyclerView = root.findViewById(R.id.PlaceList_RecyclerView);
        progressBar = root.findViewById(R.id.placeList_ProgressBar);
        shimmerFrameLayout = root.findViewById(R.id.placeList_ShimmerLayout);

    }

    @Override
    public void onPlaceClick(int position) {
        //Enviar datos del objeto con posicion position de la lista al otro fragment
        //Toast.makeText(getActivity(), "Listener del item " + position, Toast.LENGTH_LONG).show();
        Bundle bundle = new Bundle();
        /*
        TPlace place = new TPlace("Lugar en Posicion "+position, getString(R.string.lorem_ipsu), "direccion",
                3.0f, 3.0f, "/imagen", "tipodelugar", "Madrid",
                "Localidad", "Afluencia", 4.0f, false);
        */
        TPlace place = placeList.get(position);

        bundle.putParcelable(AppConstants.BUNDLE_PLACE_DETAILS, place);

        //Le pasamos el bundle
        Navigation.findNavController(root).navigate(R.id.placeDetailFragment, bundle);
    }
}