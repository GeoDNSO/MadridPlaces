package com.example.App.ui.friends.subclasses.friend_resquest_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.App.R;
import com.example.App.models.transfer.TRequestFriend;
import com.example.App.models.transfer.TUser;

import java.util.List;

public class FriendRequestListAdapter extends RecyclerView.Adapter<FriendRequestListAdapter.FriendRequestViewHolder>{

    private List<TRequestFriend> listFriends;
    private OnFriendRequestListener onFriendRequestListener;

    public FriendRequestListAdapter(List<TRequestFriend> listFriends, FriendRequestListAdapter.OnFriendRequestListener onFriendRequestListener){
        this.listFriends = listFriends;
        this.onFriendRequestListener = onFriendRequestListener;
    }

    @NonNull
    @Override
    public FriendRequestListAdapter.FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_item_fragment, parent, false);
        return new FriendRequestListAdapter.FriendRequestViewHolder(view, onFriendRequestListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestListAdapter.FriendRequestViewHolder holder, int position) {
        TRequestFriend user = listFriends.get(position);

        holder.tv_completeName_user.setText(String.format("%s: %s", user.getUserOrigin().getName(), user.getUserOrigin().getSurname()));
        holder.tv_username_user.setText(user.getUserOrigin().getUsername());
    }

    @Override
    public int getItemCount() {
        return listFriends.size();
    }

    public class FriendRequestViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_completeName_user;
        private TextView tv_username_user;
        private Button ib_accept_user;
        private Button ib_decline_user;

        public FriendRequestViewHolder(@NonNull View itemView, OnFriendRequestListener onFriendRequestListener) {
            super(itemView);

            tv_completeName_user = itemView.findViewById(R.id.request_friend_textView_name_item);
            tv_username_user = itemView.findViewById(R.id.request_friend_textView_username_item);
            ib_accept_user = itemView.findViewById(R.id.request_friend_button_accept_item);
            ib_decline_user = itemView.findViewById(R.id.request_friend_button_decline_item);

            ib_accept_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFriendRequestListener.onFriendRequestAcceptClick(getAdapterPosition());
                }
            });

            ib_decline_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFriendRequestListener.onFriendRequestDeclineClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnFriendRequestListener {
        void onFriendRequestDeclineClick(int position);
        void onFriendRequestAcceptClick(int position);
    }
}
