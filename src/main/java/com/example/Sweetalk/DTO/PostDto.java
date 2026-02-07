package com.example.Sweetalk.DTO;

import com.example.Sweetalk.Model.Comment;
import com.example.Sweetalk.Model.Post;

import java.util.List;

public class PostDto {
    private Long postId;
    private String ownerUsername;
    private String profilePic;
    private Long ownerId;
    private String imageUrl;
    private int likeCount;
    private int commentCount;
    private  String content;
    private Long createdAt;
    private String location;
    private List<ShowComment> comments;

    public PostDto(Long postId, String ownerUsername, String profilePic, String imageUrl, String content,int likeCount, int commentCount, Long createdAt) {
        this.postId = postId;
        this.ownerUsername = ownerUsername;
        this.profilePic=profilePic;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
        this.content=content;
        this.commentCount = commentCount;
        this.createdAt = createdAt;

    }
    public PostDto(Post post, List<ShowComment>comments) {
        this.postId = post.getPostId();
        this.ownerId =post.getUser().getUserId();
        this.ownerUsername = post.getUser().getUsername();
        this.profilePic=post.getProfile().getProfilePic();
        this.imageUrl = post.getImageUrl();
        this.likeCount = post.getLikes().size();
        this.commentCount = post.getComments().size();
        this.content=post.getContent();
        this.comments=comments;
        this.location=post.getLocation();
        this.createdAt = post.getCreatedAt();
    }

    public List<ShowComment> getComments() {
        return comments;
    }

    public void setComments(List<ShowComment> comments) {
        this.comments = comments;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
    @Override
    public String toString() {
        return "PostDto{" +
                "postId=" + postId +
                ", ownerUsername='" + ownerUsername + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", createdAt=" + createdAt +
                '}';
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

