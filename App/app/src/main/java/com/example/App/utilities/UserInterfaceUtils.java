package com.example.App.utilities;

import android.graphics.Color;

import com.facebook.shimmer.Shimmer;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class UserInterfaceUtils {

    public static Shimmer defaultShimmer(){
        //Efecto shimmer
        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(Color.parseColor("#F3F3F3"))
                .setBaseAlpha(1)
                .setHighlightColor(Color.parseColor("#E7E7E7"))
                .setHighlightAlpha(1)
                .setDropoff(50)
                .build();

        return  shimmer;
    }

    /*Recibe un número double y devuelve el String correspondiente con la precisión de un decimal*/
    public static String rating2UIString(double number){
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(number);

    }
}
