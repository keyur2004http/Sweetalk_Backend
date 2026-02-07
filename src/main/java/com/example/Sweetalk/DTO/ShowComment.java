package com.example.Sweetalk.DTO;

import com.example.Sweetalk.Model.Comment;
import redis.clients.jedis.Protocol;

public class ShowComment {
    private String userName;
    private String comment;
    private String profilePic;
    private Long userId;
    private Long commentId;

    public ShowComment(Comment comment) {
        this.comment = comment.getContent();
        this.profilePic = comment.getUser().getProfilePic();
        this.userName = comment.getUser().getUsername();
        this.userId=comment.getUser().getUserId();
        this.commentId=comment.getId();
    }
    public ShowComment(Long commentId,
                       String userName,
                       String profilePic,
                       String comment,
                       Long userId) {

        this.commentId = commentId;
        this.userName = userName;
        this.profilePic = profilePic;
        this.comment = comment;
        this.userId = userId;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }
}
