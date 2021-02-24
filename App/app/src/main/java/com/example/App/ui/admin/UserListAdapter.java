package com.example.App.ui.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.App.R;
import com.example.App.models.transfer.TUser;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {
    private Context context;
    private List<TUser> listUser;

    public UserListAdapter(Context context, List<TUser> listUser) {
        this.context = context;
        this.listUser = listUser;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_user_item_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TUser user = listUser.get(position);

        //holder.iv_imgProfile.setImageResource();
        holder.tv_birthdayProfile.setText(user.getBirthDate());
        holder.tv_emailProfile.setText(user.getEmail());
        holder.tv_entireNameProfile.setText(user.getName() + " " + user.getSurname());
        holder.tv_genderProfile.setText(user.getGender());
        holder.tv_nameProfile.setText(user.getUsername());
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_imgProfile;
        TextView tv_nameProfile;
        TextView tv_entireNameProfile;
        TextView tv_emailProfile;
        TextView tv_birthdayProfile;
        TextView tv_genderProfile;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_imgProfile = (ImageView) itemView.findViewById(R.id.imageViewUser);
            tv_nameProfile = (TextView) itemView.findViewById(R.id.nameTextViewProfile);
            tv_entireNameProfile = (TextView) itemView.findViewById(R.id.completeNameTextViewProfile);
            tv_emailProfile = (TextView) itemView.findViewById(R.id.emailTextViewProfile);
            tv_birthdayProfile = (TextView) itemView.findViewById(R.id.birthdayTextViewProfile);
            tv_genderProfile = (TextView) itemView.findViewById(R.id.genderTextViewProfile);
        }
    }

    public void setListUser(List<TUser> listUser){
        this.listUser = listUser;
    }
}