package com.example.App.ui.sendRecomendation;

import android.app.Activity;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.App.R;
import com.example.App.models.transfer.TUser;
import com.example.App.ui.friends.subclasses.friends_list.FriendListAdapter;

import java.util.ArrayList;
import java.util.List;

public class SendRecomendationAdapter extends RecyclerView.Adapter<SendRecomendationAdapter.MyViewHolder> {
    private Activity activity;
    private List<TUser> listUser;
    private List<String> selectedList;
    private SendRecomendationAdapter.SendRecomendationActionListener sendRecomendationActionListener;
    private boolean isChecked = false;
    private boolean isSelectAll = false;
    private SendRecomendationViewModel sendRecomendationViewModel;

    public SendRecomendationAdapter(Activity activity, List<TUser> listUser, SendRecomendationAdapter.SendRecomendationActionListener sendRecomendationActionListener) {
        this.activity = activity;
        this.listUser = listUser;
        this.sendRecomendationActionListener = sendRecomendationActionListener;
    }

    @NonNull
    @Override
    public SendRecomendationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_friend_item_row, parent, false);
        sendRecomendationViewModel = ViewModelProviders.of((FragmentActivity) activity).get(SendRecomendationViewModel.class);
        return new MyViewHolder(view, sendRecomendationActionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SendRecomendationAdapter.MyViewHolder holder, int position) {
        TUser user = listUser.get(position);

        if(user.getImage_profile() == null || user.getImage_profile() == ""){
            holder.iv_imgProfile.setImageResource(R.drawable.ic_username);
        }
        else {
            Glide.with(activity).load(user.getImage_profile()).circleCrop().into(holder.iv_imgProfile);
        }

        holder.tv_nameProfile.setText(user.getUsername());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!isChecked){
                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            //Initializate menu inflater
                            MenuInflater menuInflater = mode.getMenuInflater();
                            //Inflate menu
                            menuInflater.inflate(R.menu.send_recomendation_menu, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            //when action mode is prepared
                            isChecked = true;

                            clickItem(holder);

                            sendRecomendationViewModel.getmSelectedItems().observe((LifecycleOwner) activity, new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    mode.setTitle(String.format("%s seleccionados", s));
                                }
                            });

                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            //when click on mode item
                            int i = item.getItemId();

                            if(i == R.id.send_recomendation_select_all){
                                if(selectedList.size() == listUser.size()){
                                    //when all item selected
                                    isSelectAll = false;
                                    selectedList.clear();
                                }
                                else{
                                    isSelectAll = true;
                                    selectedList.clear();
                                    for(TUser user: listUser) {
                                        selectedList.add(user.getUsername());
                                    }
                                }
                                sendRecomendationViewModel.setmSelectedItems(String.valueOf(selectedList.size()));
                                notifyDataSetChanged();
                            }

                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {
                            isChecked = false;
                            isSelectAll = false;
                            selectedList.clear();
                            notifyDataSetChanged();
                        }
                    };
                    ((AppCompatActivity) v.getContext()).startActionMode(callback);
                }
                else{
                    //when action is already checked
                    clickItem(holder);
                }
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isChecked){
                    clickItem(holder);
                }
            }
        });

        if(isSelectAll){
            holder.iv_check.setVisibility(View.VISIBLE);
        }
        else {
            holder.iv_check.setVisibility(View.GONE);

        }
    }

    private void clickItem(MyViewHolder holder) {
        TUser user = listUser.get(holder.getAdapterPosition());

        if(holder.iv_check.getVisibility() == View.GONE){
            holder.iv_check.setVisibility(View.VISIBLE);

            selectedList.add(user.getUsername());
        }
        else {
            holder.iv_check.setVisibility(View.GONE);
            selectedList.remove(user.getUsername());
        }
        sendRecomendationViewModel.setmSelectedItems(String.valueOf(selectedList.size()));
    }

    @Override
    public int getItemCount() {
        if(this.listUser != null) {
            return listUser.size();
        }
        return 0;
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
        private ImageView iv_imgProfile;
        private TextView tv_nameProfile;
        private ImageView iv_check;
        private SendRecomendationAdapter.SendRecomendationActionListener sendRecomendationActionListener;

        public MyViewHolder(View itemView, SendRecomendationAdapter.SendRecomendationActionListener sendRecomendationActionListener) {
            super(itemView);
            iv_imgProfile = itemView.findViewById(R.id.imageView_send_recomendation);
            tv_nameProfile = itemView.findViewById(R.id.completeName_send_recomendation);
            iv_check = itemView.findViewById(R.id.iv_check_send_recomendation);

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
