package com.example.App.ui.comments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.App.repositories.CommentRepository;
import com.example.App.models.TComment;
import com.example.App.components.ViewModelParent;
import java.util.List;

public class CommentsViewModel extends ViewModelParent {
    private CommentRepository commentRepository;
    private LiveData<List<TComment>> mCommentsList = new MutableLiveData<>();

    @Override
    public void init() {
        commentRepository = new CommentRepository();

        mSuccess = super.updateOnChange(mSuccess, commentRepository.getSuccess());
        mCommentsList = super.updateOnChange(mCommentsList, commentRepository.getCommentsList());
    }

    public void showComments(String placeName, int page, int quant){
        mlv_isLoading.postValue(true);
        commentRepository.listComments(placeName, page, quant);
    }
    public void appendComments(String placeName, int page, int quant){
        mlv_isLoading.postValue(true);
        commentRepository.appendComments(placeName, page, quant);
    }
    public void newComment(String userName, String content, String placeName, float rate){
        mlv_isLoading.postValue(true);
        commentRepository.newComment(userName, content, placeName, rate);
    }
    public void deleteComment(TComment comment, int position) {
        mlv_isLoading.setValue(true);
        commentRepository.deleteComment(comment, position);
    }

    public LiveData<List<TComment>> getmCommentsList(){ return mCommentsList; }
}