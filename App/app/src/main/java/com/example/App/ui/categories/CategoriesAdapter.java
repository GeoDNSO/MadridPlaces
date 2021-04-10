package com.example.App.ui.categories;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.App.R;
import com.example.App.models.transfer.TCategory;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>{

    private Activity activity;
    private List<TCategory> categoryList;
    private CategoryListener categoryListener;

    public CategoriesAdapter(Activity activity, List<TCategory> categoryList, CategoryListener categoryListener){
        this.activity = activity;
        this.categoryList = categoryList;
        this.categoryListener = categoryListener;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);

        return new CategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.CategoriesViewHolder holder, int position) {

        TCategory category = categoryList.get(position);

        holder.textView.setText(category.getName());
        holder.imageView.setImageResource(category.getDrawableId());

    }

    @Override
    public int getItemCount() {
        //Log.i("CAT", "se va a devolver el tamaño de lista -->"+ categoryList.size());
        return categoryList.size();
    }

    class CategoriesViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout category_item_layout;
        private ImageView imageView;
        private TextView textView;
        //private CategoryListener categoryListener;

        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);

            category_item_layout = itemView.findViewById(R.id.category_item_layout);
            imageView = itemView.findViewById(R.id.ivCategory);
            textView = itemView.findViewById(R.id.tvCategory);


            category_item_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoryListener.onClicListener(getAdapterPosition());
                }
            });
        }
    }

    interface CategoryListener{
        public void onClicListener(int position);
    }
}
