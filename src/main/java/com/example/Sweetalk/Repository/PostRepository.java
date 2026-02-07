package com.example.Sweetalk.Repository;

import com.example.Sweetalk.Model.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Sweetalk.Model.Post;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findByProfileIn(Set<Profile> following);
    Post findByPostId(Long postId);

    @Modifying
    @Query(value = "DELETE FROM post_likes WHERE post_id = :postId", nativeQuery = true)
    void deleteLikesByPostId(@Param("postId") Long postId);

    @Query("SELECT p FROM Post p WHERE p.profile IN " +
            "(SELECT f.following FROM Follow f WHERE f.follower.username = :username AND f.status = 'ACCEPTED') " +
            "ORDER BY p.createdAt DESC")
    List<Post> findFeedByUsername(@Param("username") String username);


    @Modifying
    @Query("DELETE FROM Post p WHERE p.profile.userId = :profileId")
    void deleteByProfileId(Long profileId);
}
