package com.example.Sweetalk.DTO;

import com.example.Sweetalk.Model.Profile;

import java.util.List;
import java.util.stream.Collectors;

public class ProfileDTO {
    private Long userId;
    private String firstname;
    private String lastname;
    private String username;
    private String profilePic;
    private boolean isPublic;
    private int followersCount;
    private int followingCount;
    private String bio;
    private String name ;
    private List<PostDto> posts;


    public ProfileDTO(Profile profile) {
        this.userId = profile.getUserId();
        this.username = profile.getUsername();
        this.firstname = profile.getFirstname();
        this.lastname = profile.getLastname();
        this.profilePic=profile.getProfilePic();
        this.followersCount = profile.getFollowers().size();
        this.followingCount = profile.getFollowing().size();
        this.isPublic=profile.getPublic();
        this.name=profile.getName();
        this.bio=profile.getBio();
        this.posts = profile.getPosts().stream()
                .map(post -> new PostDto(
                        post,
                        post.getComments().stream()
                                .map(c -> new ShowComment(
                                        c.getId(),
                                        c.getUser().getUsername(),
                                        c.getUser().getProfilePic(),
                                        c.getContent(),
                                        c.getUser().getUserId()
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    public ProfileDTO() {
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public List<PostDto> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDto> posts) {
        this.posts = posts;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}
