package com.example.Sweetalk.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.Sweetalk.DTO.PostDto;
import com.example.Sweetalk.DTO.ShowComment;
import com.example.Sweetalk.Model.Comment;
import com.example.Sweetalk.Model.Likes;
import com.example.Sweetalk.Model.Post;
import com.example.Sweetalk.Repository.CommentRepository;
import com.example.Sweetalk.Repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.Sweetalk.Model.Profile;
import com.example.Sweetalk.Repository.PostRepository;
import com.example.Sweetalk.Repository.ProfileRepository;

@Service
public class PostService {

    @Autowired
    private Cloudinary cloudinary;

    private final ProfileRepository profileRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    @Autowired
    public PostService(ProfileRepository profileRepository, PostRepository postRepository, CommentRepository commentRepository, LikeRepository likeRepository) {
        this.profileRepository = profileRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
    }

    public List<PostDto> getUserFeed(String username) {
        List<Post> posts = postRepository.findFeedByUsername(username);
        return posts.stream()
                .map(post -> new PostDto(
                        post.getPostId(),
                        post.getProfile().getUsername(),
                        post.getProfile().getProfilePic(),
                        post.getImageUrl(),
                        post.getContent(),
                        post.getLikes().size(),
                        post.getComments().size(),
                        post.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public String uploadImage(MultipartFile file, String username, String content, String location) {
        try {
            Profile owner = profileRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "sweetalk_posts",
                            "resource_type", "auto"
                    ));

            String cloudinaryUrl = uploadResult.get("secure_url").toString();
            Post post = new Post();
            post.setProfile(owner); // Ensure this matches your Post.java field name
            post.setImageUrl(cloudinaryUrl); // Storing the full web URL
            post.setContent(content);
            post.setLocation(location);

            postRepository.save(post);
            return cloudinaryUrl;

        } catch (IOException e) {
            throw new RuntimeException("Cloudinary upload failed: " + e.getMessage(), e);
        }
    }
    @Transactional
    public ResponseEntity<?> deletePost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Post post = optionalPost.get();
        String imageUrl = post.getImageUrl();

        if (imageUrl != null && imageUrl.contains("cloudinary.com")) {
            try {
                String publicIdWithExtension = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                String publicId = "sweetalk_posts/" + publicIdWithExtension.split("\\.")[0];
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (Exception e) {
                System.err.println("Failed to delete image from Cloudinary: " + e.getMessage());
            }
        }

        likeRepository.deleteAllByPostId(postId);
        commentRepository.deleteByPost(post);
        postRepository.delete(post);

        return ResponseEntity.ok("Post and image deleted successfully");
    }

    @Transactional
    public List<ShowComment> comment(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Profile user = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Comment newComment = new Comment(post, user, content);
        commentRepository.save(newComment);
        List<Comment> allComments = commentRepository.findByPost_PostId(postId);

        return allComments.stream()
                .map(ShowComment::new)
                .collect(Collectors.toList());
    }

    public ResponseEntity<String>deleteComment(Long commentId,Long userId){
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        Post post = comment.getPost();
        if (comment.getUser().getUserId().equals(userId) || post.getProfile().getUserId().equals(userId)) {
            commentRepository.delete(comment);
            return ResponseEntity.ok("Comment deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed to delete this comment");
        }
    }

    public void likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Profile user = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Likes> existingLike = likeRepository.findByPostAndProfile(post, user);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            Likes like = new Likes();
            like.setPost(post);
            like.setProfile(user);
            likeRepository.save(like);
        }
    }

    public int getLikesCount(Long postId) {
        return postRepository.findById(postId)
                .map(post -> post.getLikes().size())
                .orElse(0);
    }
    public List<String> getUsernamesWhoLikedPost(Long postId) {
        List<Likes> likes = likeRepository.findByPost_PostId(postId);
        return likes.stream()
                .map(like -> like.getProfile().getUsername())
                .collect(Collectors.toList());
    }
    public boolean isPostLikedByUser(Long postId, Long userId) {
       return likeRepository.existsByPost_PostIdAndProfile_UserId(postId, userId);
    }
}

