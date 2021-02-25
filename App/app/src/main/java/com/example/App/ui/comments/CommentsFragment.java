package com.example.App.ui.comments;

import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.App.R;
import com.example.App.models.transfer.TComment;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class CommentsFragment extends Fragment {

    private View root;
    private CommentsViewModel mViewModel;

    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ShimmerFrameLayout shimmerFrameLayout;

    private ImageView ivPostComment;
    private EditText etComment;

    private List<TComment> commentsList = new ArrayList<>();
    private CommentListAdapter commentListAdapter;

    private int page = 1, limit = 3;

    public static CommentsFragment newInstance() {
        return new CommentsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.comments_fragment, container, false);
        
        initUI();

        listeners();
        
        commentListManagement();

        return root;
    }

    private void listeners() {

        ivPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmpty(etComment)){
                    Toast.makeText(getActivity(), "El texto de comentario está vacío", Toast.LENGTH_SHORT).show();
                }
                TComment comment = new TComment("/img", "Usuario NUEVO",
                        etComment.getText().toString(), "25/02/2021", 2.0f);

                commentsList.add(comment);
                commentListAdapter = new CommentListAdapter(getActivity(), commentsList);
                recyclerView.setAdapter(commentListAdapter);
            }
        });

    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public void onScrollViewAtBottom(){
       Log.i("HIJO", "foooooooo");
        //Cuando alacance al ultimo item de la lista
        //Incrementea el numero de la pagina

        if(page >= 5){
            progressBar.setVisibility(View.GONE);
            return ;
        }

        page++;


        //Mostrar progress bar
        progressBar.setVisibility(View.VISIBLE);

        //Pedimos más datos
        getData();
    }

    private void initUI() {
        recyclerView = root.findViewById(R.id.comments_RecyclerView);
        progressBar = root.findViewById(R.id.comments_ProgressBar);
        shimmerFrameLayout = root.findViewById(R.id.comment_ShimmerLayout);

        ivPostComment = root.findViewById(R.id.ivPostComment);
        etComment = root.findViewById(R.id.etComment);
    }

    private void commentListManagement() {

        commentListAdapter = new CommentListAdapter(getActivity(), commentsList);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(commentListAdapter);
        
        getData();

        //Empezar el efecto de shimmer
        shimmerFrameLayout.startShimmer();
    }

    static int numComentario = 0;
    private void getData() {

        //NO recogemos mas datos al llegar a la pagina 5
        if(page >= 5){
            return ;
        }
        //Si la respuesta no es nula, es decir, recibimos mensaje del servidor
        if(true) {
            //Esconder la barra de carga
            progressBar.setVisibility(View.GONE);
            //Mostrar el recyclerView
            recyclerView.setVisibility(View.VISIBLE);
            //Parar el efecto shimmer
            shimmerFrameLayout.stopShimmer();
            //Esconder al frameLayout de shimmer
            shimmerFrameLayout.setVisibility(View.GONE);

            for(int i = 0; i < limit; ++i){
                float rate = (float) Math.random()*5 + 1;
                TComment comment = new TComment("/image", "Usuario" + numComentario,
                        "Comentario de Usuario "+ numComentario++ + " " + getString(R.string.lorem_ipsu),
                        "23/02/2021", rate);

                commentsList.add(comment);
            }

            commentListAdapter = new CommentListAdapter(getActivity(), commentsList);
            recyclerView.setAdapter(commentListAdapter);

        }
        else{
            //Mostrar mensaje de error o trasladar mensaje de error a la vista
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CommentsViewModel.class);
        // TODO: Use the ViewModel
    }

}