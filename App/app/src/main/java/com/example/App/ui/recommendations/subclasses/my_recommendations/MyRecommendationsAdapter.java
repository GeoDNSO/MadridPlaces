package com.example.App.ui.recommendations.subclasses.my_recommendations;

import android.app.Activity;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.App.App;
import com.example.App.R;
import com.example.App.models.TRecommendation;
import com.example.App.utilities.AppConstants;

import java.util.List;

public class MyRecommendationsAdapter extends RecyclerView.Adapter<MyRecommendationsAdapter.ViewHolder> {

    private Activity activity;
    private List<TRecommendation> recommendationList;
    private RecommendationAdapterListener listener;

    public MyRecommendationsAdapter(Activity activity, List<TRecommendation> recommendationList, RecommendationAdapterListener listener) {
        this.activity = activity;
        this.recommendationList = recommendationList;
        this.listener = listener;
    }



    @NonNull
    @Override
    public MyRecommendationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_recommendation_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecommendationsAdapter.ViewHolder holder, int position) {

        App app = App.getInstance();
        TRecommendation recommendation = recommendationList.get(position);

        String state = recommendation.getState();
        String userDest = recommendation.getUserDest();

        //Para construir el mensaje principal
        String msg_1 = app.getAppString(R.string.my_recommendation_text_part_1);
        String msg_placeName = recommendation.getPlace().getName();
        String msg_2 = app.getAppString(R.string.my_recommendation_text_part_2);

        //Texto azul del lugar
        int blueColorId = ContextCompat.getColor(activity, R.color.blue_link);
        int purpleColorId = ContextCompat.getColor(activity, R.color.purple_500);
        SpannableString spanPlace = new SpannableString(msg_placeName);


        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                listener.onSpanClick(msg_placeName);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        spanPlace.setSpan(clickableSpan, 0, msg_placeName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanPlace.setSpan(new ForegroundColorSpan(blueColorId),0, msg_placeName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //Texto negrita del nombre del usuario destino
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        SpannableString spanUserDest = new SpannableString(userDest);
        spanUserDest.setSpan(bss,0, userDest.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanUserDest.setSpan(new ForegroundColorSpan(Color.BLACK),0, userDest.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        //Para construir el mensaje de estado
        int colorState = ContextCompat.getColor(activity, R.color.grey);
        String messageState = userDest;
        String msg_state_1 = app.getAppString(R.string.my_recommendation_text_state);

        if(state.equals(AppConstants.STATE_ACCEPTED)){
            colorState = ContextCompat.getColor(activity, R.color.green_ok);
            messageState += app.getAppString(R.string.my_recommendation_text_state_accept) + " " + msg_state_1;
        }
        else if(state.equals(AppConstants.STATE_PENDING)){
            messageState += (" " + app.getAppString(R.string.my_recommendation_text_state_still));
        }
        else{
            colorState = ContextCompat.getColor(activity, R.color.colorFavRed);
            messageState += app.getAppString(R.string.my_recommendation_text_state_not) + " " + msg_state_1;
        }


        //Asignación final
        holder.tvMyRecommendationMainText.setText(TextUtils.concat(msg_1, " ", spanPlace, " ", msg_2, " ", spanUserDest));
        holder.tvMyRecommendationMainText.setMovementMethod(LinkMovementMethod.getInstance());

        holder.tvMyRecommendationState.setBackgroundColor(colorState);
        holder.tvMyRecommendationState.setText(messageState);

    }

    @Override
    public int getItemCount() {
        return recommendationList.size();
    }

    public interface RecommendationAdapterListener{
        public void onSpanClick(String placeName);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvMyRecommendationMainText;
        private TextView tvMyRecommendationState;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMyRecommendationMainText = itemView.findViewById(R.id.tvMyRecommendationMainText);
            tvMyRecommendationState = itemView.findViewById(R.id.tvMyRecommendationState);
        }
    }
}