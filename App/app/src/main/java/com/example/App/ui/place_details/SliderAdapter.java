package com.example.App.ui.place_details;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.App.R;
import com.example.App.models.TPlace;
import com.example.App.utilities.UserInterfaceUtils;
import com.smarteist.autoimageslider.SliderViewAdapter;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {

    private Activity activity;
    private TPlace place;

    public SliderAdapter(TPlace place, Activity activity){
        this.place = place;
        this.activity = activity;
    }

    @Override
    public SliderAdapter.Holder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_detail_item_slider, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(SliderAdapter.Holder viewHolder, int position) {
        //viewHolder.imageView.setImageResource(images.get(position));

        viewHolder.textView.setText(place.getName());
        viewHolder.textView.setVisibility(View.GONE);

        String url = place.getImagesList().get(position);
        UserInterfaceUtils.loadImage(activity, url, viewHolder.imageView);
    }

    @Override
    public int getCount() {
        return place.getImagesList().size();
    }

    class Holder extends  SliderViewAdapter.ViewHolder{

        ImageView imageView;
        TextView textView;

        public Holder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_slider_view);
            textView = itemView.findViewById(R.id.image_slider_view_title);

        }
    }
}
