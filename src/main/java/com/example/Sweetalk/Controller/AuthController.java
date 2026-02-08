package com.example.Sweetalk.Controller;

import com.example.Sweetalk.DTO.*;
import com.example.Sweetalk.Util.JwtUtil;
import com.example.Sweetalk.Model.Profile;
import com.example.Sweetalk.Repository.ProfileRepository;
import com.example.Sweetalk.Service.AuthService;
import com.example.Sweetalk.Service.FollowService;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    @Autowired
    FollowService followService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Register User
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.registerAuthUser(registerRequest));

    }

    //Login
    @PostMapping("/login")
    public ResponseEntity<?> login( @RequestBody AuthenticationRequest request,HttpSession session) {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            Profile userDetails = profileRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Users not found"));
            String token = jwtUtil.generateToken(userDetails.getUsername());
            session.setAttribute("user", userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(
                token,
                userDetails.getUserId(),
                userDetails.getUsername()
        ));
    }
    @GetMapping("/loggedin")
    public ResponseEntity<ProfileDTO> getLoggedInUser(HttpSession session) {
        Profile user = (Profile) session.getAttribute("user");
        return ResponseEntity.ok(new ProfileDTO(user));
    }


}
