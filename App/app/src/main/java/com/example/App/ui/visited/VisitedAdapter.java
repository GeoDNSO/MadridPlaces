package com.example.App.ui.visited;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.App.utilities.UserInterfaceUtils;
import com.facebook.shimmer.ShimmerDrawable;

import java.util.List;

public class VisitedAdapter extends RecyclerView.Adapter<VisitedAdapter.VisitedViewHolder> {

    private Activity activity;
    private List<TPlace> historyPlaceList;
    private List<TPlace> historyPlaceListComplete;

    private OnHistoryListListener onHistoryListener;

    public VisitedAdapter(Activity activity, List<TPlace> historyPlaceList, VisitedAdapter.OnHistoryListListener onHistoryListener){
        this.activity = activity;
        this.historyPlaceList = historyPlaceList;
        this.onHistoryListener = onHistoryListener;
        //this.placeList = new ArrayList<>();
    }

    @NonNull
    @Override
    public VisitedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_place_item, parent, false);

        return new VisitedViewHolder(view, onHistoryListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitedViewHolder holder, int position) {
        TPlace place = historyPlaceList.get(position);

       //Shimmer Drawable
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(UserInterfaceUtils.defaultShimmer());

        //Imagen del Lugar
        Glide.with(activity).load(R.drawable.imagen_lugar_default)
                .placeholder(shimmerDrawable)
                .into(holder.placeImage);

        //Nombre del Lugar
        holder.tvPlaceName.setText(place.getName());
        //Rating del lugar
        String validRating = UserInterfaceUtils.rating2UIString(place.getRating());
        holder.tvRatingValue.setText(validRating);

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

    public class VisitedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView placeImage;
        TextView tvPlaceName;
        ImageView favImage;
        TextView tvRatingValue;
        TextView tvVisited;
        ImageView starImage;
        OnHistoryListListener onHistoryListener;

        public VisitedViewHolder(@NonNull View itemView, OnHistoryListListener onHistoryListener) {
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
