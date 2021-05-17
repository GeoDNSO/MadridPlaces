package com.example.App.ui.places_list;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.App.App;
import com.example.App.R;
import com.example.App.models.TPlace;
import com.example.App.utilities.UserInterfaceUtils;
import com.facebook.shimmer.ShimmerDrawable;

import java.util.ArrayList;
import java.util.List;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.ViewHolder> implements Filterable {

    private Activity activity;
    private List<TPlace> placeList;
    private List<TPlace> placeListComplete;

    private OnPlaceListener onPlaceListener;

    public PlaceListAdapter(Activity activity, List<TPlace> placeList, OnPlaceListener onPlaceListener){
        this.activity = activity;
        this.placeList = placeList;
        this.onPlaceListener = onPlaceListener;
        placeListComplete = new ArrayList<>(placeList);
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

        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(UserInterfaceUtils.defaultShimmer());

        //Nombre del Lugar
        int maxLength = 24;
        int tvLength = place.getName().length();
        String finalTitle = place.getName();

        if(tvLength >= maxLength){
            String dots = "...";
            String s = place.getName();
            finalTitle = s.substring(0, maxLength-dots.length()) + dots;
        }
        holder.tvPlaceName.setText(finalTitle);

        //Otra informacion
        holder.tvPlaceAddress.setText(place.getAddress());
        holder.ratingBar.setRating((float)place.getRating());

        String numberOfRatings = place.getNumberOfRatings() + " " + App.getInstance().getAppString(R.string.ratings_text);
        holder.tvPlaceNumberOfRatings.setText(numberOfRatings);


        //TODO revisar si son metros o km o es segun el valor...
        String distance = UserInterfaceUtils.formatDistance(place.getDistanceToUser());
        holder.tvPlaceDistance.setText(distance);


        //Rating del lugar
        String ratingValue = UserInterfaceUtils.rating2UIString(place.getRating());
        holder.tvRatingValue.setText(ratingValue);

        int favTint = ContextCompat.getColor(activity, R.color.grey);
        if(place.isUserFav()){
            favTint = ContextCompat.getColor(activity, R.color.colorFavRed);
        }
        ImageViewCompat.setImageTintList(holder.favImage, ColorStateList.valueOf(favTint));

        //Cargar imagen en el viewholder / imageview
        String url = place.getImagesList().get(0);
        UserInterfaceUtils.loadImageWithShimmer(activity, url, shimmerDrawable, holder.placeImage);
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    @Override
    public Filter getFilter() {
        return listPlacesFilter;
    }

    private Filter listPlacesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<TPlace> filterList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filterList.addAll(placeListComplete); //Como no se filtra, se llena la lista de forma en la que al principio aparece
            } else {
                String inputFilter = constraint.toString().toLowerCase().trim();

                for (TPlace place : placeListComplete) {
                    if (place.getName().toLowerCase().contains(inputFilter)) {
                        filterList.add(place);
                    }
                }
            }
            FilterResults encounteredResults = new FilterResults();
            encounteredResults.values = filterList;

            return encounteredResults;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            placeList.clear();
            placeList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    //Clase correspondiente a la representacion de un lugar en la lista --> place_list_item
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView placeImage;
        TextView tvPlaceName;
        ImageView favImage;
        TextView tvRatingValue;
        TextView tvPlaceAddress;
        TextView tvPlaceDistance;
        RatingBar ratingBar;
        TextView tvPlaceNumberOfRatings;


        private OnPlaceListener onPlaceListener;

        public ViewHolder(@NonNull View itemView, OnPlaceListener onPlaceListener){
            super(itemView);

            placeImage = itemView.findViewById(R.id.placePicture);
            tvPlaceName = itemView.findViewById(R.id.tvPlaceName);
            favImage = itemView.findViewById(R.id.favImage);
            tvRatingValue = itemView.findViewById(R.id.tvPlaceRating);
            tvPlaceAddress = itemView.findViewById(R.id.tvPlaceAddress);
            tvPlaceDistance = itemView.findViewById(R.id.tvPlaceDistance);
            ratingBar = itemView.findViewById(R.id.placeRatingBar);
            tvPlaceNumberOfRatings = itemView.findViewById(R.id.tvPlaceNumberOfRatings);

            this.onPlaceListener = onPlaceListener;
            itemView.setOnClickListener(this);

            favImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPlaceListener.onFavClick(getAdapterPosition(), favImage);
                }
            });

        }



        @Override
        public void onClick(View v) {
            onPlaceListener.onPlaceClick(getAdapterPosition());
        }
    }

    public interface OnPlaceListener{
        void onPlaceClick(int position);
        void onFavClick(int position, ImageView favImage);
    }

}
