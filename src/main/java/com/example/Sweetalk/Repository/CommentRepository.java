package com.example.Sweetalk.Repository;

import com.example.Sweetalk.Model.Comment;
import com.example.Sweetalk.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
//    List<Comment> findByPostId(Long postId);
    List<Comment> findByPost_PostId(Long postId);
    List<Comment> findByPostPostIdOrderByIdAsc(Long postId);
    void deleteByPost(Post post);
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.user.userId = :profileId")
    void deleteByProfileId(Long profileId);
}

