package com.example.App.ui.places_list;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.App.R;
import com.example.App.models.transfer.TPlace;
import com.example.App.models.transfer.TUser;
import com.example.App.utilities.UserInterfaceUtils;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;
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
        //this.placeList = new ArrayList<>();
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
        holder.tvPlaceDistance.setText("500m");
        holder.ratingBar.setRating((float)place.getRating());
        holder.tvPlaceNumberOfRatings.setText("100 Valoraciones");


        //Rating del lugar
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
        holder.tvRatingValue.setText(df.format(place.getRating()));

        int favTint = ContextCompat.getColor(activity, R.color.grey);
        if(place.isUserFav()){
            favTint = ContextCompat.getColor(activity, R.color.colorFavRed);
        }
        ImageViewCompat.setImageTintList(holder.favImage, ColorStateList.valueOf(favTint));


        try{
            Glide.with(activity).load(place.getImagesList().get(0))
                    .placeholder(shimmerDrawable)
                    .into(holder.placeImage);
        }catch (Exception e){
            Log.e("ERROR_CARGA_IMAGEN", "PlaceListAdapter: Fallo de carga de imagen debido a cierre de socket" +
                    ", fallo de conexión, timeout, etc... )");
        }


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
                    Toast.makeText(itemView.getContext(), "fav listener", Toast.LENGTH_SHORT).show();

                    TPlace place = placeList.get(getAdapterPosition());
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
            onPlaceListener.onPlaceClick(getAdapterPosition());
        }
    }

    public interface OnPlaceListener{
        void onPlaceClick(int position);
    }

}
