package com.example.App.ui.friends.subclasses.friends_list;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.App.R;
import com.example.App.models.transfer.TRequestFriend;
import com.example.App.models.transfer.TUser;

import java.util.ArrayList;
import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.MyViewHolder> /*implements Filterable*/ {

    private Activity activity;
    private List<TRequestFriend> listUser;
    private List<TRequestFriend> listUserComplete;
    private FriendActionListener friendActionListener;

    public FriendListAdapter(Activity activity, List<TRequestFriend> listUser, FriendActionListener friendActionListener) {
        this.activity = activity;
        this.listUser = listUser;
        this.friendActionListener = friendActionListener;
        listUserComplete = new ArrayList<>(listUser); //Se crea un nuevo array para que no apunten a la misma lista
    }

    @NonNull
    @Override
    public FriendListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_friend_item_row, parent, false);
        return new FriendListAdapter.MyViewHolder(view, friendActionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListAdapter.MyViewHolder holder, int position) {
        TRequestFriend user = listUser.get(position);

        if(user.getUserOrigin().getImage_profile() == null || user.getUserOrigin().getImage_profile() == ""){
            holder.iv_imgProfile.setImageResource(R.drawable.ic_username);
        }
        else {
            Glide.with(activity).load(user.getUserOrigin().getImage_profile()).circleCrop().into(holder.iv_imgProfile);
        }

        holder.tv_nameProfile.setText(String.format("%s %s", user.getUserOrigin().getName(), user.getUserOrigin().getSurname()));
        holder.tv_nicknameProfile.setText(user.getUserOrigin().getUsername());

        holder.ivOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopMenu(holder, position).show();
            }
        });
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

    private PopupMenu createPopMenu(@NonNull MyViewHolder holder, int position){
        PopupMenu popupMenu = new PopupMenu(activity.getApplicationContext(), holder.ivOptions);

        popupMenu.inflate(R.menu.friend_item_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.delete_friend_item_menu:
                        friendActionListener.onDeleteFriend(position);
                        break;
                    default:
                        break;

                }
                return false;
            }
        });
        return popupMenu;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_imgProfile;
        private TextView tv_nameProfile;
        private TextView tv_nicknameProfile;
        private ImageView ivOptions;

        public MyViewHolder(View itemView, FriendActionListener friendActionListener) {
            super(itemView);
            iv_imgProfile = (ImageView) itemView.findViewById(R.id.imageViewFriend);
            tv_nameProfile = (TextView) itemView.findViewById(R.id.completeNameTextViewFriend);
            tv_nicknameProfile = (TextView) itemView.findViewById(R.id.friend_textView_username_item);
            ivOptions = itemView.findViewById(R.id.friend_more_options);
        }

    }

    public interface FriendActionListener {
        void onDeleteFriend(int position);
    }

    public void setListUser(List<TRequestFriend> listUser){
        this.listUser = listUser;
        notifyDataSetChanged();
    }
}
