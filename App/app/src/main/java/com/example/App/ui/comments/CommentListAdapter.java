package com.example.App.ui.comments;

import android.app.Activity;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.App.R;
import com.example.App.models.transfer.TComment;
import com.example.App.utilities.TextViewExpandableUtil;
import com.example.App.utilities.ViewListenerUtilities;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {

    private Activity activity;
    private List<TComment> commentList;
    private CommentObserver commentObserver;

    public CommentListAdapter(Activity activity, List<TComment> commentList, CommentObserver commentObserver){
        this.activity = activity;
        this.commentList = commentList;
        this.commentObserver = commentObserver;

        Toast.makeText(activity, "Coments: " + commentList.size(), Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public CommentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item_fragment, parent, false);

        return new CommentListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentListAdapter.ViewHolder holder, int position) {

        TComment comment = commentList.get(position);
        Log.d("AAAAAAAAA", "value: " + position);
        //Efecto shimmer
        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(Color.parseColor("#F3F3F3"))
                .setBaseAlpha(1)
                .setHighlightColor(Color.parseColor("#E7E7E7"))
                .setHighlightAlpha(1)
                .setDropoff(50)
                .build();

        //Shimmer Drawable
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();

        shimmerDrawable.setShimmer(shimmer);


        //TODO cambiar por la imagen de los usuarios

        Glide.with(activity).load(R.drawable.imagen_lugar_default)
                .circleCrop()
                .placeholder(shimmerDrawable)
                .into(holder.userImage);

        //Colocar los valores del comentario en el view holder
        holder.tvComment.setText(comment.getContent());
        holder.tvUsername.setText(comment.getUsername());
        holder.tvDate.setText(comment.getDate());
        holder.ratingBar.setRating((float) comment.getRating()); // TODO comment.getRating() Es en double y es necesario float

        ViewListenerUtilities.makeTextViewExpandable(holder.tvComment, true);


        holder.ivOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopMenu(holder, position).show();
            }
        });

    }

    private PopupMenu createPopMenu(@NonNull CommentListAdapter.ViewHolder holder, int position){
        PopupMenu popupMenu = new PopupMenu(activity.getApplicationContext(), holder.ivOptions);

        popupMenu.inflate(R.menu.comment_item_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.delete_commentMenu:
                        commentObserver.onCommentDelete(position);
                        break;
                    default:
                        break;

                }

                return false;
            }
        });

        return popupMenu;
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //private CircleImageView userImage;
        private ImageView userImage;
        private TextView tvUsername;
        private TextView tvComment;
        private TextView tvDate;
        private RatingBar ratingBar;
        private ImageView ivOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.comment_profile_image);
            tvUsername = itemView.findViewById(R.id.comment_username);
            tvComment = itemView.findViewById(R.id.comment_textview);
            tvDate = itemView.findViewById(R.id.comment_time_posted);
            ratingBar = itemView.findViewById(R.id.comment_rating_bar);
            ivOptions = itemView.findViewById(R.id.comment_more_options);


        }

        @Override
        public void onClick(View v) {

        }
    }

    public interface CommentObserver{
        public void onCommentDelete(int position);
    }
}
