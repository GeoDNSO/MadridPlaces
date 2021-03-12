package com.example.App.ui.categories;

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
import android.widget.Toast;

import com.example.App.R;
import com.example.App.models.transfer.TCategory;
import com.example.App.utilities.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment implements CategoriesAdapter.CategoryListener {


    private List<String> categoriesTitles;
    private List<TCategory> categoryList;
    private CategoriesAdapter categoriesAdapter;


    private View root;

    private CategoriesViewModel mViewModel;

    private RecyclerView recyclerView;

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.categories_fragment, container, false);

        Log.i("CAT_FRAG", "llega a oncreate");

        initCategories();
        getData();

        recyclerView = root.findViewById(R.id.category_RecyclerView);

        categoriesAdapter = new CategoriesAdapter(getActivity(), categoryList, this);

        //Set layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(categoriesAdapter);


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);
        // TODO: Use the ViewModel
    }

    public void getData(){
        categoryList = new ArrayList<>();
        for(int i = 0; i < categoriesTitles.size(); ++i){
            categoryList.add(new TCategory(categoriesTitles.get(i), R.drawable.ic_launcher_img));
        }
    }

    public void initCategories(){
        categoriesTitles = new ArrayList<>();
        categoriesTitles.add("Alojamientos");
        categoriesTitles.add("Clubs");
        categoriesTitles.add("Edificación singular");
        categoriesTitles.add("Elemento conmemorativo, Lápida");
        categoriesTitles.add("Elemento de ornamentación");
        categoriesTitles.add("Escultura conceptual o abstracta");
        categoriesTitles.add("Estatua");
        categoriesTitles.add("Fuente, Estanque, Lámina de agua");
        categoriesTitles.add("Grupo Escultórico");
        categoriesTitles.add("Monumentos, Edificios Artísticos");
        categoriesTitles.add("Museo");
        categoriesTitles.add("Oficinas Turismo");
        categoriesTitles.add("Puente, Construcción civil");
        categoriesTitles.add("Puerta, Arco triunfal");
        categoriesTitles.add("Restaurantes");
        categoriesTitles.add("Templos, Iglesias Católicas");
        categoriesTitles.add("Tiendas");
    }

    @Override
    public void onClicListener(int position) {
        //Enviar datos del objeto con posicion position de la lista al otro fragment
        Toast.makeText(getActivity(), "Touch Listener Category", Toast.LENGTH_SHORT);
        TCategory category = categoryList.get(position);

        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.BUNDLE_PLACE_DETAILS, category);

        //Le pasamos el bundle
        Navigation.findNavController(root).navigate(R.id.placesList, bundle);
    }
}