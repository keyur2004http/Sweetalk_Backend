package com.example.Sweetalk.DTO;

public class PostImages {
    private Long postId;
    private String imageUrl;

    public PostImages(Long postId, String imageUrl) {
        this.postId = postId;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
