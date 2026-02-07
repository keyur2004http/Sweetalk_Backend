package com.example.Sweetalk.Service;
import com.example.Sweetalk.DTO.ProfileDTO;
import com.example.Sweetalk.Enum.FollowStatus;
import com.example.Sweetalk.Model.Follow;
import com.example.Sweetalk.Model.Profile;
import com.example.Sweetalk.Repository.FollowRepository;
import com.example.Sweetalk.Repository.PostRepository;
import com.example.Sweetalk.Repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service

public class FollowService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private FollowRepository followRepository;

//    @Autowired
//    private NotificationService notificationService;

    @Autowired
    private PostRepository postRepository;


    @Transactional
    public String sendFollowRequest(Long followerId, Long followingId) {
        Optional<Profile> followerOpt = profileRepository.findById(followerId);
        Optional<Profile> followingOpt = profileRepository.findById(followingId);
        if (followerOpt.isPresent() && followingOpt.isPresent()) {
            Profile follower = followerOpt.get();
            Profile following = followingOpt.get();
            if (followRepository.existsByFollower_UserIdAndFollowing_UserId(followerId, followingId)) {
                return "Follow request already sent.";
            }

            com.example.Sweetalk.Model.Follow followRequest = new com.example.Sweetalk.Model.Follow();
            followRequest.setFollower(follower);
            followRequest.setFollowing(following);

            followRepository.save(followRequest);
//            notificationService.sendFollowNotification(following.getUsername(), follower.getUsername() + " sent you a follow request!");
            return "Follow request sent.";
        }
        return "Users not found!";
    }

    @Transactional
    public ResponseEntity<String> handleFollowRequest(Long followerId, Long followingId) {
        Profile follower = profileRepository.findById(followerId).orElseThrow();
        Profile following = profileRepository.findById(followingId).orElseThrow();
        Optional<Follow> followRequest = followRepository.findByFollowerAndFollowing(follower, following);
        if (followRequest == null) {
            return ResponseEntity.badRequest().body("Follow request not found");
        }

        return null;
    }
    @Transactional
    public String cancelFollowRequest(Long followerId, Long followingId) {
        Profile follower = profileRepository.findById(followerId).orElseThrow();
        Profile following = profileRepository.findById(followingId).orElseThrow();

        Optional<Follow> followRequest = followRepository.findByFollowerAndFollowing(follower, following);
        if (followRequest.isEmpty()) return "No follow request found!";

//        followRepository.delete(followRequest);
        return "Follow request removed.";
    }
    @Transactional
    public String unfollowUser(Long followerId, Long followingId) {
        Follow follow = followRepository
                .findByFollower_UserIdAndFollowing_UserId(followerId, followingId);
        if (follow == null ) {
            return "You are not following this user!";
        }
        followRepository.delete(follow);
        return "Unfollowed successfully!";
    }
    public List<ProfileDTO> getFollowers(Long userId) {
        List<Follow> acceptedFollowers = followRepository.findByFollowingUserIdAndStatus(userId, FollowStatus.ACCEPTED);

        return acceptedFollowers.stream()
                .map(follow -> new ProfileDTO(follow.getFollower())) // Map the follower, not the following
                .collect(Collectors.toList());
    }
    public List<ProfileDTO> getFollowing(Long userId) {
        // Use the new method that filters by the ACCEPTED enum status
        List<Follow> acceptedFollows = followRepository.findByFollowerUserIdAndStatus(userId, FollowStatus.ACCEPTED);
        return acceptedFollows.stream()
                .map(follow -> new ProfileDTO(follow.getFollowing()))
                .collect(Collectors.toList());
    }
    public Set<Profile> getMutualFollowers(Long userId, Long otherUserId) {
        Profile user = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Profile otherUser = profileRepository.findById(otherUserId)
                .orElseThrow(() -> new RuntimeException("Other user not found"));

        Set<Profile> mutual = new HashSet<>(user.getFollowers());
        mutual.retainAll(otherUser.getFollowers());

        return mutual;
    }
    public boolean isFollowing(Long followerId, Long targetId) {
        return followRepository.existsByFollower_UserIdAndFollowing_UserId(followerId, targetId);
    }
    public String toggleFollow(Profile follower, Profile targetUser) {
        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowing(follower, targetUser);
        if (existingFollow.isPresent()) {
            followRepository.delete(existingFollow.get());
            return "FOLLOW";
        }
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(targetUser);
        follow.setCreatedAt(System.currentTimeMillis());

        if (Boolean.TRUE.equals(targetUser.getPublic())) {
            follow.setStatus(FollowStatus.ACCEPTED); // Public: Go straight to following
            followRepository.save(follow);
            return "FOLLOWING";
        } else {
            follow.setStatus(FollowStatus.PENDING); // Private: Request sent
            followRepository.save(follow);
            return "PENDING"; // Use "PENDING" to match your frontend status check
        }
    }
    public String getFollowStatus(Profile follower, Profile targetUser) {
        if (follower.getUserId().equals(targetUser.getUserId())) return "OWN";

        return followRepository.findByFollowerAndFollowing(follower, targetUser)
                .map(f -> {
                    if (f.getStatus() == FollowStatus.ACCEPTED) return "FOLLOWING";
                    if (f.getStatus() == FollowStatus.PENDING) return "PENDING";
                    return "FOLLOW";
                })
                .orElse("FOLLOW"); // Only returns "FOLLOW" if no record exists
    }

    public void processRequest(Long followerId, Profile currentUser, boolean accept) {
        Profile follower = profileRepository.findById(followerId).orElseThrow();
        Follow follow = followRepository.findByFollowerAndFollowing(follower, currentUser)
                .orElseThrow(() -> new RuntimeException("No request found"));

        if (accept) {
            follow.setStatus(FollowStatus.ACCEPTED);
            followRepository.save(follow);
        } else {
            followRepository.delete(follow);
        }
    }

}
