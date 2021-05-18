package com.example.App.ui.recommendations.subclasses.pending_recommendations;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.App.R;
import com.example.App.models.TRecommendation;

import java.util.List;

public class PendingRecommendationsListAdapter extends RecyclerView.Adapter<PendingRecommendationsListAdapter.ViewHolder>{

    private List<TRecommendation> listRecommendation;
    private OnPendingRecommendationsListener onPendingRecommendationsListener;

    public PendingRecommendationsListAdapter(List<TRecommendation> listRecommendation, OnPendingRecommendationsListener onPendingRecommendationsListener){
        this.listRecommendation = listRecommendation;
        this.onPendingRecommendationsListener = onPendingRecommendationsListener;
    }

    @NonNull
    @Override
    public PendingRecommendationsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendations_item_pending, parent, false);
        return new PendingRecommendationsListAdapter.ViewHolder(view, onPendingRecommendationsListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingRecommendationsListAdapter.ViewHolder holder, int position) {
        TRecommendation recomendation = listRecommendation.get(position);

        holder.tv_name_place.setText(String.format("%s: %s", holder.itemView.getContext().getString(R.string.place_default_name), recomendation.getPlace().getName()));
        holder.tv_rating_place.setText(String.format("%s: %s", holder.itemView.getContext().getString(R.string.place_rate), recomendation.getPlace().getRating()));
        holder.tv_category_place.setText(String.format("%s: %s", holder.itemView.getContext().getString(R.string.place_category), recomendation.getPlace().getTypeOfPlace()));
        holder.tv_recommended_by.setText(String.format("%s %s", holder.itemView.getContext().getString(R.string.recommendation_by), recomendation.getUserOrigin()));
        holder.ib_accept_place.setBackgroundResource(R.drawable.ic_baseline_check_24);
        holder.ib_deny_place.setBackgroundResource(R.drawable.ic_close);
    }

    @Override
    public int getItemCount() {
        return listRecommendation.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tv_name_place;
        private TextView tv_rating_place;
        private TextView tv_category_place;
        private TextView tv_recommended_by;
        private ImageButton ib_accept_place;
        private ImageButton ib_deny_place;
        private OnPendingRecommendationsListener onPendingRecommendationsListener;

        public ViewHolder(@NonNull View itemView, OnPendingRecommendationsListener onPendingRecommendationsListener) {
            super(itemView);
            tv_name_place = itemView.findViewById(R.id.recommendations_pending_textView_name_place);
            tv_rating_place = itemView.findViewById(R.id.recommendations_pending_textView_rating_place);
            tv_category_place = itemView.findViewById(R.id.recommendations_pending_textView_cat_place);
            tv_recommended_by = itemView.findViewById(R.id.recommendations_pending_textView_recommended_by);
            ib_accept_place = itemView.findViewById(R.id.recommendations_pending_imageButton_accept);
            ib_deny_place = itemView.findViewById(R.id.recommendations_pending_imageButton_deny);
            this.onPendingRecommendationsListener = onPendingRecommendationsListener;

            ib_accept_place.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPendingRecommendationsListener.onPendingRecommendationsAcceptClick(getAdapterPosition());
                }
            });

            ib_deny_place.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPendingRecommendationsListener.onPendingRecommendationsDenyClick(getAdapterPosition());
                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onPendingRecommendationsListener.onPendingRecommendationsClick(getAdapterPosition());
        }
    }

    public interface OnPendingRecommendationsListener {
        void onPendingRecommendationsClick(int position);
        void onPendingRecommendationsDenyClick(int position);
        void onPendingRecommendationsAcceptClick(int position);
    }


}
