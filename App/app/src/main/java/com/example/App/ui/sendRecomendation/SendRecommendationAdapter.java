package com.example.App.ui.sendRecomendation;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.App.R;
import com.example.App.models.TRequestFriend;

import java.util.ArrayList;
import java.util.List;

public class SendRecommendationAdapter extends RecyclerView.Adapter<SendRecommendationAdapter.MyViewHolder> {
    private Activity activity;
    private List<TRequestFriend> listUser;
    private List<String> selectedList;
    private SendRecommendationAdapter.SendRecomendationActionListener sendRecomendationActionListener;
    private MenuItem button;

    public SendRecommendationAdapter(Activity activity, List<TRequestFriend> listUser, SendRecommendationAdapter.SendRecomendationActionListener sendRecomendationActionListener, MenuItem button) {
        this.activity = activity;
        this.listUser = listUser;
        this.sendRecomendationActionListener = sendRecomendationActionListener;
        this.button = button;
        selectedList = new ArrayList<>();
    }

    @NonNull
    @Override
    public SendRecommendationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_recomendation_item_view, parent, false);
        return new MyViewHolder(view, sendRecomendationActionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SendRecommendationAdapter.MyViewHolder holder, int position) {
        TRequestFriend user = listUser.get(position);

        if(user.getUserOrigin().getImage_profile() == null || user.getUserOrigin().getImage_profile() == ""){
            holder.iv_img.setImageResource(R.drawable.ic_username);
        }
        else {
            Glide.with(activity).load(user.getUserOrigin().getImage_profile()).circleCrop().into(holder.iv_img);
        }

        holder.tv_name.setText(String.format("%s %s", user.getUserOrigin().getName(), user.getUserOrigin().getSurname()));
        holder.tv_userName.setText(user.getUserOrigin().getUsername());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickItem(holder);
            }
        });

    }

    private void clickItem(MyViewHolder holder) {
        TRequestFriend user = listUser.get(holder.getAdapterPosition());

        if(holder.iv_check.getVisibility() == View.GONE){
            holder.iv_check.setVisibility(View.VISIBLE);
            holder.iv_check.setColorFilter(Color.GREEN);

            selectedList.add(user.getUserOrigin().getUsername());
        }
        else {
            holder.iv_check.setVisibility(View.GONE);
            holder.iv_check.setColorFilter(Color.TRANSPARENT);
            selectedList.remove(user.getUserOrigin().getUsername());
        }
        enableButtons();
    }

    @Override
    public int getItemCount() {
        if(this.listUser != null) {
            return listUser.size();
        }
        return 0;
    }

    public int getSelectedCount() {
        return selectedList.size();
    }

    public List<String> getListSelected() {
        return selectedList;
    }

    private void enableButtons() {
        if(selectedList.size() == 0){
            button.setVisible(false);
        }
        else{
            button.setVisible(true);
        }
    }

    /*
    @Override
    public Filter getFilter() {
        return listUserFilter;
    }

    private Filter listUserFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<TUser> filterList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filterList.addAll(listUserComplete); //Como no se filtra, se llena la lista de forma en la que al principio aparece
            }
            else {
                String inputFilter = constraint.toString().toLowerCase().trim();

                for( TUser user : listUserComplete) {
                    if(user.getUsername().toLowerCase().contains(inputFilter) || user.getName().toLowerCase().contains(inputFilter)
                            || user.getSurname().toLowerCase().contains(inputFilter)) {
                        filterList.add(user);
                    }
                }
            }
            FilterResults encounteredResults = new FilterResults();
            encounteredResults.values = filterList;

            return encounteredResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listUser.clear();
            listUser.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };*/

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iv_img;
        private TextView tv_name;
        private TextView tv_userName;
        private ImageView iv_check;
        private SendRecommendationAdapter.SendRecomendationActionListener sendRecomendationActionListener;

        public MyViewHolder(View itemView, SendRecommendationAdapter.SendRecomendationActionListener sendRecomendationActionListener) {
            super(itemView);
            iv_img = itemView.findViewById(R.id.imageView_send_recomendation);
            tv_name = itemView.findViewById(R.id.completeName_send_recomendation);
            iv_check = itemView.findViewById(R.id.iv_check_send_recomendation);
            tv_userName = itemView.findViewById(R.id.send_recomendation_textView_username_item);

            this.sendRecomendationActionListener = sendRecomendationActionListener;

            itemView.setOnClickListener(this); //this referencia a la interfaz que se implementa en el constructor
        }

        @Override
        public void onClick(View v) {
            sendRecomendationActionListener.onListClick(getAdapterPosition());
        }
    }

    public interface SendRecomendationActionListener {
        void onListClick(int position);
    }

}
