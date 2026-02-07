package com.example.Sweetalk.Service;

import com.example.Sweetalk.Model.Post;
import com.example.Sweetalk.Model.Profile;
import com.example.Sweetalk.Repository.FollowRepository;
import com.example.Sweetalk.Repository.PostRepository;
import com.example.Sweetalk.Repository.ProfileRepository;
import com.example.Sweetalk.DTO.PostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class HomePage {

    private final ProfileRepository profileRepository;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    @Autowired
    public HomePage(ProfileRepository profileRepository, PostRepository postRepository, FollowService followService, FollowRepository followRepository) {
        this.profileRepository = profileRepository;
        this.postRepository = postRepository;
        this.followRepository=followRepository;
    }

//    public List<PostDto> getUserFeed(String username) {
//        Profile user = profileRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Set<Profile> following = user.getFollowing();
//        System.out.println("Following users: " + following);
//
//        if (following.isEmpty()) {
//            return Collections.emptyList(); // Return empty if no following
//        }
//        List<Post> posts = postRepository.findByProfileIn(following);
//        System.out.println("Posts: " + posts);
//
//        return posts.stream()
//                .map(post -> new PostDto(
//                        post.getPostId(),
//                        post.getProfile().getUsername(),
//                        post.getProfile().getProfilePic(),
//                        post.getImageUrl(),
//                        post.getLikes().size(),
//                        post.getComments().size(),
//                        post.getCreatedAt()))
//                .collect(Collectors.toList());
//    }


}
