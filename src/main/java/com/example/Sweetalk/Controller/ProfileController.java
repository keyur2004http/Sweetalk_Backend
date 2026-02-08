package com.example.Sweetalk.Controller;

import com.example.Sweetalk.DTO.EditProfileRequest;
import com.example.Sweetalk.DTO.ProfileDTO;
import com.example.Sweetalk.Model.Profile;
import com.example.Sweetalk.Repository.ProfileRepository;
import com.example.Sweetalk.Service.HomePage;
import com.example.Sweetalk.DTO.PostDto;
import com.example.Sweetalk.Service.PostService;
import com.example.Sweetalk.Service.ProfileService;
import com.example.Sweetalk.Util.JwtUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profiles") // Use a consistent base path

public class ProfileController {
    @Autowired private ProfileRepository profileRepository;
    @Autowired private ProfileService profileService;


    @GetMapping("/{username}")
    public ResponseEntity<ProfileDTO> getProfile(
            @PathVariable String username,
            @RequestParam Long requesterId) {
        return profileService.getProfile(username, requesterId);
    }
    @GetMapping("/getProfileById/{userId}")
    public ResponseEntity<ProfileDTO> getProfileById(@PathVariable Long userId) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(new ProfileDTO(profile));
    }

    @PutMapping(value = "/edit/{profileId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> editProfile(
            @PathVariable Long profileId,
            @RequestParam("username") String username,
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname ,
            @RequestParam("bio") String bio,
            @RequestParam("isPublic") boolean isPublic,
            @RequestParam(value = "profilePic", required = false) MultipartFile profilePic) {
        profileService.editProfile(profileId, username,firstname,lastname, bio, profilePic, isPublic);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @GetMapping("/loggedin")
    public ResponseEntity<ProfileDTO> getLoggedInUser(HttpSession session) {
        Profile user = (Profile) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(new ProfileDTO(user));
    }
    @Transactional
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAccount(@RequestParam String username) {
        profileService.deleteAccount(username);
        return ResponseEntity.ok().build();
    }
}