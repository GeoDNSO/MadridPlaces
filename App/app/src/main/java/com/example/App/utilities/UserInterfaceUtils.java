package com.example.App.utilities;

import android.app.Activity;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class UserInterfaceUtils {

    public static void loadImageWithShimmer(Activity activity, String url, ShimmerDrawable shimmerDrawable, ImageView imageView){
        try{
            Glide.with(activity).load(url)
                    .placeholder(shimmerDrawable)
                    .into(imageView);
        }catch (Exception e){
            Log.e("ERROR_CARGA_IMAGEN", "PlaceListAdapter: Fallo de carga de imagen debido a cierre de socket" +
                    ", fallo de conexión, timeout, etc... )");
        }
    }

    public static void loadImage(Activity activity, String url, ImageView imageView){
        try{
            Glide.with(activity).load(url)
                    .into(imageView);
        }catch (Exception e){
            Log.e("ERROR_CARGA_IMAGEN", "PlaceListAdapter: Fallo de carga de imagen debido a cierre de socket" +
                    ", fallo de conexión, timeout, etc... )");
        }
    }

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


    public static String formatDistance(double distance){
        String unit = "m";
        if(distance >= 1000){
            distance /= 10;
            unit = "km";
        }
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);

        return String.format("%s %s", df.format(distance), unit);
    }
}
