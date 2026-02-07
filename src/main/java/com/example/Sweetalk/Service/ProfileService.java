package com.example.Sweetalk.Service;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.Sweetalk.DTO.EditProfileRequest;
import com.example.Sweetalk.DTO.ProfileDTO;
import com.example.Sweetalk.Enum.FollowStatus;
import com.example.Sweetalk.Model.Profile;
import com.example.Sweetalk.Model.Users;
import com.example.Sweetalk.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
public class ProfileService {
    @Autowired ProfileRepository profileRepository;
    @Autowired FollowRepository followRepository;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    LikeRepository likesRepository;
    @Autowired
    CommentRepository commentRepository;

    public ResponseEntity<ProfileDTO> getProfile(String username, Long requesterId) {
        Profile targetProfile = profileRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isOwnProfile = targetProfile.getUserId().equals(requesterId);
        boolean isFollowing = followRepository.existsByFollowerUserIdAndFollowingUserIdAndStatus(
                requesterId, targetProfile.getUserId(), FollowStatus.ACCEPTED);
        ProfileDTO dto = new ProfileDTO(targetProfile);
        if (!isOwnProfile && !targetProfile.getPublic() && !isFollowing) {
            dto.setPosts(Collections.emptyList());
        }

        return ResponseEntity.ok(dto);
    }
    // Already configured in your CloudinaryConfig

    public void editProfile(Long profileId, String username, String firstname, String lastname, String bio, MultipartFile profilePic, boolean isPublic) {
        Profile profile = profileRepository.findById(profileId).orElseThrow();
        profile.setUsername(username);
        profile.setFirstname(firstname);
        profile.setLastname(lastname);
        profile.setBio(bio);
        profile.setPublic(isPublic);

        // Inside your editProfile method
        if (profilePic != null && !profilePic.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(profilePic.getBytes(),
                        ObjectUtils.asMap(
                                "folder", "profile_pics",
                                "width", 500,
                                "height", 500,
                                "crop", "fill",
                                "gravity", "face"
                        ));

                String cloudinaryUrl = (String) uploadResult.get("secure_url");
                profile.setProfilePic(cloudinaryUrl);
            } catch (IOException e) {
                throw new RuntimeException("Could not save image to Cloudinary", e);
            }
        }
        profileRepository.save(profile);
    }
    @Transactional
    public void deleteAccount(String username) {

        Profile profile = profileRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        Long profileId = profile.getUserId();

        likesRepository.deleteByProfileId(profileId);
        commentRepository.deleteByProfileId(profileId);
        messageRepository.deleteUserMessages(username);
        followRepository.deleteAllByProfileId(profileId);
        postRepository.deleteByProfileId(profileId);

        profileRepository.delete(profile);
    }
}