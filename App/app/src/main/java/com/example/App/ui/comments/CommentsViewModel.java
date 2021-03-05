package com.example.App.ui.comments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.App.models.repositories.CommentRepository;
import com.example.App.models.transfer.TComment;
import com.example.App.models.transfer.TPlace;
import com.example.App.ui.ViewModelParent;
import java.util.List;

public class CommentsViewModel extends ViewModelParent {
    private CommentRepository commentRepository;
    private LiveData<List<TComment>> mCommentsList = new MutableLiveData<>();

    @Override
    public void init() {
        commentRepository = new CommentRepository();
        mSuccess = Transformations.switchMap(
                commentRepository.getSuccess(),
                success -> setSuccess(success)
        );

        mCommentsList = Transformations.switchMap(
                commentRepository.getCommentsList(),
                comments -> setAndGetCommentsList(comments));
    }

    // TODO: Por ahora, no esta paginado los comentarios
    public void showComments(String placeName){
        mProgressBar.postValue(true);
        commentRepository.listComments(placeName);
    }

    private LiveData<List<TComment>> setAndGetCommentsList(List<TComment> comments) {
        MutableLiveData<List<TComment>> mAux = new MutableLiveData<>();
        mAux.setValue(comments);
        return mAux;
    }

    private LiveData<Integer> setSuccess(Integer success) {
        mProgressBar.setValue(false); //progress bar visible
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }

    public LiveData<List<TComment>> getmCommentsList(){ return mCommentsList; }
}