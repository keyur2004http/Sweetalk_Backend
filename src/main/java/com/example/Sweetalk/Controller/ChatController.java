package com.example.Sweetalk.Controller;

import com.example.Sweetalk.DTO.ProfileDTO;
import com.example.Sweetalk.Model.Message;
import com.example.Sweetalk.Repository.MessageRepository;
import com.example.Sweetalk.Repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://192.168.1.6:3000")
@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ProfileRepository profileRepository;

    @MessageMapping("/chat")
    public void processMessage(@Payload Message message) {
        try {
            if (message.getSender() == null || message.getReceiver() == null || message.getContent() == null) {
                throw new IllegalArgumentException("Sender, receiver, and content must not be null");
            }

            message.setTimestamp(LocalDateTime.now());
            Message saved = messageRepository.save(message);

            messagingTemplate.convertAndSendToUser(
                    saved.getReceiver(),
                    "/queue/messages",
                    saved
            );
        } catch (Exception e) {
            System.err.println("Error processing WebSocket message: " + e.getMessage());
            e.printStackTrace();  // Optional: logs full stack trace
        }
    }

    // Get all users the current user has chatted with
    @GetMapping("/getChattedUsers/{username}")
    public List<ProfileDTO> getChatUsers(@PathVariable String username) {
        List<String> usernames = messageRepository.findChatUsernames(username);

        return profileRepository.findByUsernameIn(usernames)
                .stream()
                .map(profile -> {
                    ProfileDTO dto = new ProfileDTO();
                    dto.setUserId(profile.getUserId());
                    dto.setUsername(profile.getUsername());
                    dto.setProfilePic(profile.getProfilePic());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    // Get chat history
    @GetMapping("/history/{user1}/{user2}")
    public List<Message> getChatHistory(@PathVariable String user1, @PathVariable String user2) {
        return messageRepository.findBySenderAndReceiverOrReceiverAndSender(
                user1, user2, user1, user2);
    }
}
