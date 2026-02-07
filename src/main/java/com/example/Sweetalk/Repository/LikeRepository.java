package com.example.Sweetalk.Repository;
import com.example.Sweetalk.Model.Likes;
import com.example.Sweetalk.Model.Post;
import com.example.Sweetalk.Model.Profile;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Repository
    public interface LikeRepository extends JpaRepository<Likes, Long> {
    List<Likes> findByPost_PostId(Long postId);

    @Query("SELECT l FROM Likes l WHERE l.post.postId = :postId AND l.profile.userId = :userId")
    Optional<Likes> findByPostAndUser(Long postId, Long userId);

    Optional<Likes> findByPostAndProfile(Post post, Profile user);

    boolean existsByPost_PostIdAndProfile_UserId(Long postId, Long userId);

    void deleteByPost_PostId(Long postId);


    void deleteByPost(Post post);
    @Modifying
    @Transactional
    @Query("DELETE FROM Likes l WHERE l.post.postId = :postId")
    void deleteAllByPostId(@Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM Likes l WHERE l.profile.userId = :profileId")
    void deleteByProfileId(Long profileId);
}
