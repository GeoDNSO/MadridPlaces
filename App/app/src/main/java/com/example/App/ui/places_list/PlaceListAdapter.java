package com.example.App.ui.places_list;

import android.app.Activity;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.App.R;
import com.example.App.models.transfer.TPlace;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.ViewHolder> {

    private Activity activity;
    private List<TPlace> placeList;

    private OnPlaceListener onPlaceListener;

    public PlaceListAdapter(Activity activity, List<TPlace> placeList, OnPlaceListener onPlaceListener){
        this.activity = activity;
        this.placeList = placeList;
        this.onPlaceListener = onPlaceListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_list_item_fragment, parent, false);

        return new ViewHolder(view, onPlaceListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        TPlace place = placeList.get(position);

        //Efecto shimmer
        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(Color.parseColor("#F3F3F3"))
                .setBaseAlpha(1)
                .setHighlightColor(Color.parseColor("#E7E7E7"))
                .setHighlightAlpha(1)
                .setDropoff(50)
                .build();

        //Shimmer Drawable
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();

        shimmerDrawable.setShimmer(shimmer);


        //Imagen del Lugar
        //load sirve para multitud de recursos: enlaces, drawables, bitmaps, etc...
        //@TODO decidir como se obtendran las imagenes para poder seleccionar un metodo...
        /*
        Glide.with(activity).load(place.getImage())
                .placeholder(shimmerDrawable)
                .into(holder.placeImage);
        */

        Glide.with(activity).load(R.drawable.imagen_lugar_default)
                .placeholder(shimmerDrawable)
                .into(holder.placeImage);

        //Nombre del Lugar
        holder.tvPlaceName.setText(place.getName());
        //Rating del lugar
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
        holder.tvRatingValue.setText(df.format(place.getRating()));
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    //Clase correspondiente a la representacion de un lugar en la lista --> place_list_item
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView placeImage;
        TextView tvPlaceName;
        ImageView favImage;
        TextView tvRatingValue;
        ImageView starImage;

        private OnPlaceListener onPlaceListener;

        public ViewHolder(@NonNull View itemView, OnPlaceListener onPlaceListener){
            super(itemView);

            placeImage = itemView.findViewById(R.id.placePicture);
            tvPlaceName = itemView.findViewById(R.id.tvPlaceName);
            favImage = itemView.findViewById(R.id.favImage);
            tvRatingValue = itemView.findViewById(R.id.tvPlaceRating);
            starImage = itemView.findViewById(R.id.placeStarImage);

            this.onPlaceListener = onPlaceListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onPlaceListener.onPlaceClick(getAdapterPosition());
        }
    }

    public interface OnPlaceListener{
        void onPlaceClick(int position);
    }

}
