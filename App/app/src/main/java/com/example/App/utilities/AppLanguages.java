package com.example.App.utilities;

import android.app.Activity;

import com.example.App.R;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class AppLanguages {

    private static String[] languages;
    private static Map<String, String> langMap;

    public AppLanguages(Activity activity) {
        String spanish = activity.getString(R.string.spanish);
        String english = activity.getString(R.string.english);
        String french = activity.getString(R.string.french);
        String german = activity.getString(R.string.german);

        languages = new String[]{spanish, english, french, german};

        langMap = new HashMap<>();
        langMap.put(spanish, "es");
        langMap.put(english, "en");
        langMap.put(french, "fr");
        langMap.put(german, "de");
    }

    public static String[] getLanguages(){
        return languages;
    }

    public static String getLangTag(String language){
        return langMap.get(language);
    }

    public static String getLangFromTag(String tag){
        return getKey(langMap, tag);
    }

    public static  <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
