package com.example.Sweetalk.Service;

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
    public String registerAuthUser(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return "Users already exists";
        }
        try {
            Profile profile = new Profile();
            profile.setUsername(registerRequest.getUsername());
            profile.setFirstname(registerRequest.getFirstname());
            profile.setLastname(registerRequest.getLastname());
            profileRepository.save(profile);
            Users users=new Users();
            users.setUsername(registerRequest.getUsername());
            users.setEmail(registerRequest.getEmail());
            users.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            users.setRole(Role.ROLE_USER);
            userRepository.save(users);
            return "Users registered successfully";
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "Error occurred while registering user";
        }
    }
    public boolean authenticateUser (String username, String rawPassword) {
        Users users = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Users not found"));
        return passwordEncoder.matches(rawPassword, users.getPassword());
    }
}
