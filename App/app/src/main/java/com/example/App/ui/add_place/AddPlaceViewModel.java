package com.example.App.ui.add_place;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.models.repositories.PlaceRepository;
import com.example.App.models.repositories.UserRepository;
import com.example.App.models.transfer.TPlace;
import com.example.App.models.transfer.TUser;
import com.example.App.ui.ViewModelParent;

import java.util.ArrayList;
import java.util.List;

public class AddPlaceViewModel extends ViewModelParent {

    private PlaceRepository placeRepository;
    List<String> mListTypesOfPlace;
    private LiveData<Boolean> mAddPlaceSuccess = new MutableLiveData<>();

    //observamos los objetos del repositorio, en este caso, el success devuelto por la llamada okhttp
    public void init(){
        placeRepository = new PlaceRepository();
        mListTypesOfPlace = new ArrayList<>();
        mSuccess = Transformations.switchMap(
                placeRepository.getSuccess(),
                success -> setSuccess(success)
        );
        mAddPlaceSuccess = Transformations.switchMap(
                placeRepository.getBooleanPlace(),
                success -> setAndGetAddPlace(success));
    }

    private LiveData<Boolean> setAndGetAddPlace(Boolean success) {
        MutableLiveData<Boolean> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }

    private LiveData<Integer> setSuccess(Integer success) {
        mProgressBar.setValue(false); //progress bar visible
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }

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

    public void addPlace(String placeName, String placeDescription, String typePlace, List<String> listImages){
        mProgressBar.setValue(true); //progress bar visible
        TPlace place = new TPlace(placeName, placeDescription, 0.0, 0.0, listImages, "3", "Madrid",
                "", "", "10", "", "", 0.0, false);
        //TODO en type of place no devolvemos elnombre del lugar sino el numero asignado en la base de datos
        placeRepository.addPlace(place);
        //placeRepository.addPlace(placeName, placeDescription, typePlace);
        //placeRepository.addPlaceImages(placeName, listImages);

    }

    public LiveData<Boolean> getmAddPlaceSuccess(){
        return mAddPlaceSuccess;
    }
}
