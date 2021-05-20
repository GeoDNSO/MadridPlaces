package com.example.App.ui.categories;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.App.R;
import com.example.App.models.TCategory;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>{

    private Activity activity;
    private List<TCategory> categoryList;
    private CategoryObserver categoryObserver;

    public CategoriesAdapter(Activity activity, List<TCategory> categoryList, CategoryObserver categoryObserver){
        this.activity = activity;
        this.categoryList = categoryList;
        this.categoryObserver = categoryObserver;
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
                    categoryObserver.onClickListener(getAdapterPosition());
                }
            });
        }
    }

    interface CategoryObserver {
        public void onClickListener(int position);
    }
}
