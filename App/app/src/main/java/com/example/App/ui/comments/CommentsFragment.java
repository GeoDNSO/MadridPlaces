package com.example.App.ui.comments;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.App.App;
import com.example.App.R;
import com.example.App.models.TComment;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.ViewListenerUtilities;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class CommentsFragment extends Fragment implements CommentListAdapter.CommentObserver {
    private String placeName;
    private View root;
    private CommentsViewModel mViewModel;

    private App app; //global variable

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ShimmerFrameLayout shimmerFrameLayout;

    private TextInputLayout etComment; //Será usado para newComment

    private RatingBar ratingBar;
    private Button sendRateButton;

    private List<TComment> commentsList = new ArrayList<>();
    private CommentListAdapter commentListAdapter;

    private int page = 1, quant = 5, limit = 3; //Aun no implementado paginado en comentarios

    public CommentsFragment(String placeName) {
        this.placeName = placeName;
    }

    public static CommentsFragment newInstance(String placeName) {
        return new CommentsFragment(placeName);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CommentsViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.comments_fragment, container, false);

        app = App.getInstance(getActivity()); //para coger el nombre del usuario

        mViewModel = new ViewModelProvider(this).get(CommentsViewModel.class);
        mViewModel.init();

        initUI();


        mViewModel.getmCommentsList().observe(getViewLifecycleOwner(), new Observer<List<TComment>>() {
            @Override
            public void onChanged(List<TComment> tComments) {
                commentsList = tComments;
                if(tComments != null) {
                    commentListAdapter = new CommentListAdapter(getActivity(), commentsList, CommentsFragment.this);
                }
                recyclerView.setAdapter(commentListAdapter);
            }
        });

        mViewModel.getSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                if(integer.equals(AppConstants.ERROR_DELETE_COMMENT)){
                    Toast.makeText(getActivity(), "Error al borrar comentario", Toast.LENGTH_SHORT ).show();
                    return;
                }
                else if(integer.equals(AppConstants.DELETE_COMMENT_OK)){
                    Toast.makeText(getActivity(), "Comentario borrado con exito", Toast.LENGTH_SHORT ).show();
                    return;
                }

                //Mostrar el recyclerView
                recyclerView.setVisibility(View.VISIBLE);
                //Parar el efecto shimmer
                shimmerFrameLayout.stopShimmer();
                //Esconder al frameLayout de shimmer
                shimmerFrameLayout.setVisibility(View.GONE);
            }
        });

        mViewModel.getMLV_IsLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                ViewListenerUtilities.setVisibility(progressBar, aBoolean);
            }
        });

        listeners();
        commentListManagement();

        return root;
    }

    private void listeners() {

        sendRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Tres casos: comentario sin valoración, con valoración o con las dos cosas
                if(app.isLogged()) { //Tienes que estar logueado

                    if (ratingBar.getRating() != 0) {
                        mViewModel.newComment(app.getUsername(), etComment.getEditText().getText().toString(), placeName, ratingBar.getRating());
                    } else {
                        //No se hace nada
                        Toast.makeText(getActivity(), "Selecciona una valoración", Toast.LENGTH_SHORT).show();
                    }
                }

                else{
                    Toast.makeText(getActivity(), "Necesitas tener iniciada la sesión", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    //PlaceListDetail usa esta función para actualizar el scroll
    public void onScrollViewAtBottom(){
        //Cuando alacance al ultimo item de la lista incrementea el numero de la pagina
        page++;

        //Mostrar progress bar
        progressBar.setVisibility(View.VISIBLE);

        shimmerFrameLayout.startShimmer();

        shimmerFrameLayout.setVisibility(View.VISIBLE);

        mViewModel.appendComments(placeName, page, quant);
    }

    private void initUI() {
        recyclerView = root.findViewById(R.id.comments_RecyclerView);
        progressBar = root.findViewById(R.id.comments_ProgressBar);
        shimmerFrameLayout = root.findViewById(R.id.comment_ShimmerLayout);

        etComment = root.findViewById(R.id.etComment);

        ratingBar = root.findViewById(R.id.placeDetailsRatingBar);
        sendRateButton = root.findViewById(R.id.placeDetailSendRating);
    }

    private void commentListManagement() {
        if(commentsList == null){
            commentsList = new ArrayList<>();
        }
        commentListAdapter = new CommentListAdapter(getActivity(), commentsList, this);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(commentListAdapter);
        
        //getData();
        mViewModel.showComments(placeName, page, quant);
        //Empezar el efecto de shimmer
        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onCommentDelete(int position) {
        Toast.makeText(getActivity(), "Delete a Comentario Num " + position, Toast.LENGTH_SHORT).show();
        TComment comment = commentsList.get(position);
        mViewModel.deleteComment(comment, position);
    }
}