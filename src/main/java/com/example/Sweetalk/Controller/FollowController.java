package com.example.Sweetalk.Controller;

import com.example.Sweetalk.DTO.ProfileDTO;
import com.example.Sweetalk.Enum.FollowStatus;
import com.example.Sweetalk.Model.Follow;
import com.example.Sweetalk.Model.Profile;
import com.example.Sweetalk.Repository.FollowRepository;
import com.example.Sweetalk.Repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.example.Sweetalk.Service.FollowService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/api/follow")
public class FollowController {

    @Autowired
    private FollowService followService;
    @Autowired
    FollowRepository followRepository;
    @Autowired
    ProfileRepository profileRepository;

    @PostMapping("/sendFollowRequest/{followerId}/{followingId}")
    public ResponseEntity<String> sendFollowRequest(@PathVariable Long followerId, @PathVariable Long followingId) {
//        notificationProducer.sendFollowNotification(follower, following);
        return ResponseEntity.ok(followService.sendFollowRequest(followerId, followingId));
    }

    @PostMapping("/accept/{followerId}/{followingId}")
    public ResponseEntity<ResponseEntity<String>> handleFollowRequest(@PathVariable Long followerId,
                                                                      @PathVariable Long followingId) {
//        notificationProducer.sendFollowNotification(follower, following);
        return ResponseEntity.ok(followService.handleFollowRequest(followerId, followingId));
    }
    @DeleteMapping("/cancel/{followerId}/{followingId}")
    public ResponseEntity<String> cancelFollow(
            @PathVariable Long followerId,
            @PathVariable Long followingId) {
        return ResponseEntity.ok(followService.cancelFollowRequest(followerId, followingId));
    }

    @PutMapping("/unfollow/{follower}/{following}")
    public ResponseEntity<String> unfollow(@PathVariable Long follower,
                                                      @PathVariable Long following) {
        return ResponseEntity.ok(followService.unfollowUser(follower, following));
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<List<ProfileDTO>> getFollowers(@PathVariable Long userId) {
        List<ProfileDTO> follower = followService.getFollowers(userId);
        return ResponseEntity.ok(follower);
    }

    // Get following list
    @GetMapping("/following/{userId}")
    public ResponseEntity<List<ProfileDTO>> getFollowing(@PathVariable Long userId) {
        List<ProfileDTO> following = followService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }
    // Get mutual followers
    @GetMapping("/{id}/mutual/{otherUserId}")
    public ResponseEntity<Set<Profile>> getMutualFollowers(@PathVariable Long id, @PathVariable Long otherUserId) {
        return ResponseEntity.ok(followService.getMutualFollowers(id, otherUserId));
    }
    // Check User follow or not
    @GetMapping("/isFollowing")
    public boolean isFollowing(Long followerId, Long targetId) {
        return followRepository
                .findByFollower_UserIdAndFollowing_UserIdAndStatus(
                        followerId,
                        targetId,
                        FollowStatus.ACCEPTED
                )
                .isPresent();
    }

    @GetMapping("/isProfileVisible")
    public ResponseEntity<Boolean> isProfileVisible(@RequestParam Long followerId, @RequestParam Long followingId) {
        if (followerId.equals(followingId)) return ResponseEntity.ok(true);
        Profile target = profileRepository.findById(followingId).orElseThrow();
        if (target.getPublic()) return ResponseEntity.ok(true);
        boolean isFollowing = followRepository.isProfileVisible(followerId, followingId);
        return ResponseEntity.ok(isFollowing);
    }
    @GetMapping("/pending/{userId}")
    public List<ProfileDTO> getPendingRequests(@PathVariable Long userId) {
        List<Follow> pending = followRepository.findByFollowingUserIdAndStatus(userId, FollowStatus.PENDING);
        return pending.stream()
                .map(req -> new ProfileDTO(req.getFollower()))
                .toList();
    }

    @GetMapping("/accepted")
    public List<ProfileDTO> getAcceptedRequests(@RequestParam Long userId) {
        List<Follow> accepted = followRepository.findByFollowerUserIdAndStatus(userId, FollowStatus.ACCEPTED);

        return accepted.stream()
                .map(req -> new ProfileDTO(req.getFollowing()))
                .toList();
    }
    @GetMapping("/status/{targetId}")
    public ResponseEntity<String> getFollowStatus(
            @PathVariable Long targetId,
            @RequestParam Long requesterId) { // Change this from @AuthenticationPrincipal
        Profile currentUser = profileRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));
        Profile targetUser = profileRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        return ResponseEntity.ok(followService.getFollowStatus(currentUser, targetUser));
    }

    @PostMapping("/{targetId}")
    public ResponseEntity<String> toggleFollow(
            @PathVariable Long targetId,
            @RequestParam Long requesterId) { // React sends this as ?requesterId=1
        Profile follower = profileRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));

        Profile targetUser = profileRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Target not found"));
        String result = followService.toggleFollow(follower, targetUser);
        return ResponseEntity.ok(result);
    }


    @PutMapping("/request/{followerId}")
    public ResponseEntity<Void> handleRequest(
            @PathVariable Long followerId,
            @RequestParam boolean accept,
            @RequestParam Long userId
    ) {
        Profile currentUser = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));
        followService.processRequest(followerId, currentUser, accept);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/requests")
    public ResponseEntity<List<ProfileDTO>> getFollowRequests(@RequestParam Long userId) {

        Profile user = profileRepository.findById(userId)
                .orElseThrow();

        if (user.getPublic()) {
            return ResponseEntity.ok(List.of()); // 🔥 public users never see requests
        }

        List<Follow> pending =
                followRepository.findByFollowingUserIdAndStatus(userId, FollowStatus.PENDING);

        return ResponseEntity.ok(
                pending.stream()
                        .map(f -> new ProfileDTO(f.getFollower()))
                        .toList()
        );
    }


    @GetMapping("/followBackSuggestions/{userId}")
        public ResponseEntity<List<ProfileDTO>> getFollowBackSuggestions(@PathVariable Long userId) {
        // Logic: People who follow me (Accepted) whom I do not follow back
        List<Profile> suggestions = followRepository.findFollowBackSuggestions(userId);

        List<ProfileDTO> dtos = suggestions.stream()
                .map(ProfileDTO::new)
                .toList();
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/suggestions/random")
    public ResponseEntity<List<ProfileDTO>> getSuggestions(
            @AuthenticationPrincipal UserDetails userDetails) {

        Profile currentUser = profileRepository
                .findByUsername(userDetails.getUsername())
                .orElseThrow();

        List<Profile> suggestions =
                followRepository.findRandomSuggestions(currentUser.getUserId());

        List<ProfileDTO> dtos = suggestions.stream()
                .limit(5)
                .map(ProfileDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}

