package com.example.App.ui.settings;

import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.App.App;
import com.example.App.R;
import com.example.App.utilities.AppLanguages;
import com.example.App.utilities.PermissionsManager;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class SettingsFragment extends Fragment {

    private SettingsViewModel mViewModel;
    private View root;

    private Switch geoSwitch;
    private PermissionsManager permissionsManager;

    private SearchableSpinner langSearchableSpinner;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        root = inflater.inflate(R.layout.settings_fragment, container, false);

        permissionsManager = new PermissionsManager(getActivity());

        initUI();

        initListeners();

        return root;
    }

    private void initUI() {
        geoSwitch = root.findViewById(R.id.settings_switch_geo);
        langSearchableSpinner = root.findViewById(R.id.langSpinner);

        geoSwitch.setChecked(permissionsManager.hasGeolocationPermissions());

        langSearchableSpinner.setTitle(getString(R.string.select_lang));
        langSearchableSpinner.setPositiveButton(getString(R.string.lang_select_ok));

        List<String> langList = Arrays.asList(AppLanguages.getLanguages());

        langSearchableSpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item,
                langList));

        String currentLang = App.getInstance().getLangTag();
        currentLang = AppLanguages.getLangFromTag(currentLang);
        int index = langList.indexOf(currentLang);
        langSearchableSpinner.setSelection(index);
    }

    private void initListeners() {
        geoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String[] permissions = new String[] { Manifest.permission.ACCESS_FINE_LOCATION};
                    permissionsManager.startPermissionsActivity(permissions, permissionsManager.GEOLOCATION_REQUEST_CODE);
                    geoSwitch.setChecked(permissionsManager.hasGeolocationPermissions());
                }
                else{
                    permissionsManager.userPermissionsIntent();
                    geoSwitch.setChecked(permissionsManager.hasGeolocationPermissions());
                }
            }
        });

        langSearchableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean firstTime = true;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(firstTime){
                    firstTime = false;
                    return;
                }


                String lang = (String) langSearchableSpinner.getSelectedItem();

                String oldLangTag = App.getInstance().getLangTag();
                String langTag = AppLanguages.getLangTag(lang);

                //El listener del searchable spinner se ejecuta nada más iniciar la clase
                //si no se usa está condición no sería posible cambiar de lenguaje en tiempo de ejecución
                //y se acabaría en un bucle de pantalla negra parpadeante(la actividad reiniciandose continuamente...)
                if(!oldLangTag.equals(langTag)){
                    App.getInstance().setLocale(langTag);
                    recreateActivity();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getActivity(), getString(R.string.select_lang_message), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void recreateActivity() {
        getActivity().recreate();
    }

    @Override
    public void onResume() {
        super.onResume();
        geoSwitch.setChecked(permissionsManager.hasGeolocationPermissions());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        // TODO: Use the ViewModel
    }

}