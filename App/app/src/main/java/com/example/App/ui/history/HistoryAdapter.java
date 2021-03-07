package com.example.App.ui.history;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.App.R;
import com.example.App.models.transfer.TPlace;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Activity activity;
    private List<TPlace> historyPlaceList;
    private List<TPlace> historyPlaceListComplete;

    private OnHistoryListListener onHistoryListener;

    public HistoryAdapter(Activity activity, List<TPlace> historyPlaceList, HistoryAdapter.OnHistoryListListener onHistoryListener){
        this.activity = activity;
        this.historyPlaceList = historyPlaceList;
        this.onHistoryListener = onHistoryListener;
        //this.placeList = new ArrayList<>();
    }

    @NonNull
    @Override
    public HistoryAdapter.HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_place_item, parent, false);

        return new HistoryAdapter.HistoryViewHolder(view, onHistoryListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.HistoryViewHolder holder, int position) {
        TPlace place = historyPlaceList.get(position);

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

        int favTint = ContextCompat.getColor(activity, R.color.grey);
        if(place.isUserFav()){
            favTint = ContextCompat.getColor(activity, R.color.colorFavRed);
        }
        ImageViewCompat.setImageTintList(holder.favImage, ColorStateList.valueOf(favTint));
    }

    @Override
    public int getItemCount() {
        return historyPlaceList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView placeImage;
        TextView tvPlaceName;
        ImageView favImage;
        TextView tvRatingValue;
        TextView tvVisited;
        ImageView starImage;
        OnHistoryListListener onHistoryListener;

        public HistoryViewHolder(@NonNull View itemView, OnHistoryListListener onHistoryListener) {
            super(itemView);
            placeImage = itemView.findViewById(R.id.historyPlacePicture);
            tvPlaceName = itemView.findViewById(R.id.tvHistoryPlaceName);
            favImage = itemView.findViewById(R.id.historyFavImage);
            tvRatingValue = itemView.findViewById(R.id.tvHistoryPlaceRating);
            starImage = itemView.findViewById(R.id.historyPlaceStarImage);
            tvVisited = itemView.findViewById(R.id.historyTextViewVisited);

            this.onHistoryListener = onHistoryListener;
            itemView.setOnClickListener(this);

            favImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "fav listener", Toast.LENGTH_SHORT).show();

                    TPlace place = historyPlaceList.get(getAdapterPosition());
                    place.setUserFav(!place.isUserFav());

                    int favTint = ContextCompat.getColor(activity, R.color.grey);
                    if(place.isUserFav()){
                        favTint = ContextCompat.getColor(activity, R.color.colorFavRed);
                    }

                    ImageViewCompat.setImageTintList(favImage, ColorStateList.valueOf(favTint));
                }
            });
        }

        @Override
        public void onClick(View v) {
            onHistoryListener.OnHistoryListClick(getAdapterPosition());
        }
    }

    public interface OnHistoryListListener {
        void OnHistoryListClick(int position);
    }
}
