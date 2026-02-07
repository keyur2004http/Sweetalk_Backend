package com.example.Sweetalk.Controller;

import com.example.Sweetalk.DTO.PostDto;
import com.example.Sweetalk.DTO.ProfileDTO;
import com.example.Sweetalk.Model.Profile;
import com.example.Sweetalk.Repository.ProfileRepository;
import com.example.Sweetalk.Service.HomePage;
import com.example.Sweetalk.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://192.168.1.6:3000")
@RestController
@RequestMapping("/home")
public class HomePageController {
    @Autowired
    private HomePage homePage;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private PostService postService;

    @GetMapping("/{username}")
    public ResponseEntity<List<PostDto>> getUserHomepage(
            @PathVariable String username) {
        List<PostDto> posts = postService.getUserFeed(username);
        return ResponseEntity.ok(posts);
    }
    //Search Profile
    @GetMapping("/search")
    public ResponseEntity<List<ProfileDTO>> searchProfiles(@RequestParam String username) {
        List<Profile> profiles = profileRepository.findByUsernameContainingIgnoreCase(username);
        List<ProfileDTO> profileDTOs = profiles.stream()
                .map(ProfileDTO::new)  // uses your constructor
                .collect(Collectors.toList());
        return ResponseEntity.ok(profileDTOs);
    }

}
