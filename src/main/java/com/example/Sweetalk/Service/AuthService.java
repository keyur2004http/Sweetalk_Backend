package com.example.Sweetalk.Service;

import com.example.Sweetalk.DTO.ApiResponse;
import com.example.Sweetalk.Model.Users;
import com.example.Sweetalk.Repository.UserRepository;
import com.example.Sweetalk.DTO.RegisterRequest;
import com.example.Sweetalk.Enum.Role;
import com.example.Sweetalk.Model.Profile;
import com.example.Sweetalk.Repository.ProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private  ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public ApiResponse registerAuthUser(RegisterRequest registerRequest) {
        if (registerRequest.getUsername() == null || !registerRequest.getUsername().matches("^[a-z0-9._]+$")) {
            return new ApiResponse(false,
                    "Username must be lowercase and can only contain letters, numbers, '.' or '_'");
        }
        if (!registerRequest.getEmail().contains("@")) {
            return new ApiResponse(false, "Please provide a valid email address");
        }
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return new ApiResponse(false, "Username already exists!");
        }

        try {
            Profile profile = new Profile();
            profile.setUsername(registerRequest.getUsername());
            profile.setFirstname(registerRequest.getFirstname());
            profile.setLastname(registerRequest.getLastname());
            profile.setPublic(true);
            profileRepository.save(profile);
            Users users = new Users();
            users.setUsername(registerRequest.getUsername());
            users.setEmail(registerRequest.getEmail());

            users.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            users.setRole(Role.ROLE_USER);
            userRepository.save(users);

            return new ApiResponse(true, "User registered successfully!");
        } catch (Exception e) {
            return new ApiResponse(false, "System Error: " + e.getMessage());
        }
    }

    public boolean authenticateUser (String username, String rawPassword) {
        Users users = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Users not found"));
        return passwordEncoder.matches(rawPassword, users.getPassword());
    }
}
