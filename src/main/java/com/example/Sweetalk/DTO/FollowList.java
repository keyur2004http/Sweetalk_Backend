package com.example.Sweetalk.DTO;

public class FollowList {
    private Long userId;
    private String name;
    private String profileImgUrl;

    public FollowList(String name, String profileImgUrl, Long userId) {
        this.name = name;
        this.profileImgUrl = profileImgUrl;
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
