package com.example.Sweetalk.Controller;

import com.example.Sweetalk.DTO.ShowComment;
import com.example.Sweetalk.Model.Comment;
import com.example.Sweetalk.Repository.CommentRepository;
import com.example.Sweetalk.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://192.168.1.6:3000")
@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    PostService postService;
    @Autowired
    CommentRepository commentRepository;

    @PostMapping("/add")
    public List<ShowComment> addComment(@RequestParam Long postId, @RequestParam Long userId, @RequestBody String content) {
        return(postService.comment(postId,userId,content));
    }

    //Get All Comment
    @GetMapping("/get/{postId}")
    public List<ShowComment> getCommentsForPost(@PathVariable Long postId) {
        List<Comment> comments = commentRepository.findByPost_PostId(postId);
        return comments.stream()
                .map(ShowComment::new)
                .collect(Collectors.toList());
    }
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId, @RequestParam Long userId) {
        return postService.deleteComment(commentId,userId);
    }
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(
                commentRepository.findByPostPostIdOrderByIdAsc(postId)
        );
    }
}
