package com.example.Sweetalk.Controller;
import com.example.Sweetalk.DTO.PostDto;
import com.example.Sweetalk.DTO.ProfileDTO;
import com.example.Sweetalk.DTO.ShowComment;
import com.example.Sweetalk.Model.Comment;
import com.example.Sweetalk.Model.Post;
import com.example.Sweetalk.Repository.CommentRepository;
import com.example.Sweetalk.Repository.PostRepository;
import com.example.Sweetalk.Repository.ProfileRepository;
import com.example.Sweetalk.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/post")
public class PostController {
    @Autowired
    private  PostService postService;
    @Autowired
    private   CommentRepository commentRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    ProfileRepository profileRepository;


    // Upload post
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("username") String username,
            @RequestParam("content") String content,
            @RequestParam("location") String location) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload.");
        }
        try {
            String imageUrl = postService.uploadImage(file, username, content, location);
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("message", "Post created successfully!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/getPost/{postId}")
    public PostDto getPostByImagePath(@PathVariable Long postId) {
            Post post=postRepository.findById(postId).orElseThrow();
            List<ShowComment> commentDtoList = commentRepository.findByPost_PostId(postId)
                .stream()
                .map(ShowComment::new)
                .collect(Collectors.toList());
        System.out.println(commentDtoList);
            return new PostDto(post,commentDtoList);
    }

    //add comment


    @GetMapping("/username")
    public ResponseEntity<ProfileDTO> getprofile(@PathVariable String username){

        return ResponseEntity.ok(new ProfileDTO((profileRepository.findByUsername(username).orElseThrow())));
    }

    // like and unlike post
    @PostMapping("/like/{postId}")
        public ResponseEntity<?> likeOrUnlikePost(@PathVariable Long postId, @RequestParam Long userId) {
            postService.likePost(postId, userId);
            return ResponseEntity.ok().build();
        }
    // get all the post like
    @GetMapping("/likes/{postId}")
    public ResponseEntity<List<String>> getLikesOnPost(@PathVariable Long postId) {
        List<String> likedUsers = postService.getUsernamesWhoLikedPost(postId);
        return ResponseEntity.ok(likedUsers);
    }
    // post is liked or not
    @GetMapping("/isLiked/{postId}")
    public ResponseEntity<Map<String, Boolean>> isPostLiked(
            @PathVariable Long postId,
            @RequestParam Long userId) {

        boolean liked = postService.isPostLikedByUser(postId, userId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("liked", liked);

        return ResponseEntity.ok(response);
    }
    //Delete Post
    @DeleteMapping("/deletePosts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, @RequestParam String username) {
        return postService.deletePost(postId);
    }

}

