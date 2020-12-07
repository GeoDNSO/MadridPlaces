package com.example.App;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.App.transfer.TUser;

import java.util.List;

public class UserListAdapter extends BaseAdapter {
    Context context;
    List<TUser> listUser;

    public UserListAdapter(Context context, List<TUser> listUser) {
        this.context = context;
        this.listUser = listUser;
    }

    @Override
    public int getCount() {
        return listUser.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv_imgProfile;
        TextView tv_nameProfile;
        TextView tv_entireNameProfile;
        TextView tv_emailProfile;
        TextView tv_birthdayProfile;
        TextView tv_genderProfile;

        TUser user = listUser.get(position);

        if(convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.list_user_item_row, null);

        iv_imgProfile = convertView.findViewById(R.id.imageViewUser);
        tv_nameProfile = convertView.findViewById(R.id.nameTextViewProfile);
        tv_entireNameProfile = convertView.findViewById(R.id.completeNameTextViewProfile);
        tv_emailProfile = convertView.findViewById(R.id.emailTextViewProfile);
        tv_birthdayProfile = convertView.findViewById(R.id.birthdayTextViewProfile);
        tv_genderProfile = convertView.findViewById(R.id.genderTextViewProfile);

        //iv_imgProfile.setImageResource(user.getImage());
        tv_nameProfile.setText(user.getUsername());
        tv_entireNameProfile.setText(user.getName() + user.getSurname());
        tv_birthdayProfile.setText(user.getBirthDate());
        tv_emailProfile.setText(user.getEmail());
        tv_genderProfile.setText(user.getGender());

        return convertView;
    }
}
