package com.example.App.ui.add_place;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddPlaceViewModel extends ViewModel {
    List<String> mListTypesOfPlace = new ArrayList<String>();

    public List<String> getTypesOfPlaces(){
        mListTypesOfPlace.add("Alojamientos");
        mListTypesOfPlace.add("Clubs");
        mListTypesOfPlace.add("Edificación singular");
        mListTypesOfPlace.add("Elemento conmemorativo, Lápida");
        mListTypesOfPlace.add("Elemento de ornamentación");
        mListTypesOfPlace.add("Escultura conceptual o abstracta");
        mListTypesOfPlace.add("Estatua");
        mListTypesOfPlace.add("Fuente, Estanque, Lámina de agua");
        mListTypesOfPlace.add("Grupo Escultórico");
        mListTypesOfPlace.add("Monumentos, Edificios Artísticos");
        mListTypesOfPlace.add("Museo");
        mListTypesOfPlace.add("Oficinas Turismo");
        mListTypesOfPlace.add("Puente, Construcción civil");
        mListTypesOfPlace.add("Puerta, Arco triunfal");
        mListTypesOfPlace.add("Restaurantes");
        mListTypesOfPlace.add("Templos, Iglesias Católicas");
        mListTypesOfPlace.add("Tiendas");

        return mListTypesOfPlace;
    }
}
