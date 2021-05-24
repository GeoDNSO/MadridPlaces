package com.example.App.ui.comments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

        mSuccess = super.updateOnChange(commentRepository.getSuccess());
        mCommentsList = super.updateOnChange(commentRepository.getCommentsList());
    }

    public void showComments(String placeName, int page, int quant){
        commentRepository.listComments(placeName, page, quant);
    }
    public void appendComments(String placeName, int page, int quant){
        commentRepository.appendComments(placeName, page, quant);
    }
    public void newComment(String userName, String content, String placeName, float rate){
        commentRepository.newComment(userName, content, placeName, rate);
    }
    public void deleteComment(TComment comment, int position) {
        commentRepository.deleteComment(comment, position);
    }

    public LiveData<List<TComment>> getmCommentsList(){ return mCommentsList; }
}