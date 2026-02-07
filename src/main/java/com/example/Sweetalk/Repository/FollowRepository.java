package com.example.Sweetalk.Repository;

import com.example.Sweetalk.Enum.FollowStatus;
import com.example.Sweetalk.Model.Follow;
import com.example.Sweetalk.Model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    @Query("SELECT f.follower.userId FROM Follow f WHERE f.following.userId = :followingId AND f.status = 'ACCEPTED'")
    List<Long> findFollowersByFollowingId(@Param("followingId") Long followingId);

    // 2. Check if profile is visible (follower is accepted)
    @Query("SELECT COUNT(f) > 0 FROM Follow f WHERE f.follower.userId = :followerId AND f.following.userId = :followingId AND f.status = 'ACCEPTED'")
    boolean isProfileVisible(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    // 3. Find Follow Back Suggestions (People you follow who don't follow you back)
    @Query("SELECT f.follower FROM Follow f WHERE f.following.userId = :userId AND f.status = 'ACCEPTED' " +
            "AND f.follower.userId NOT IN (" +
            "SELECT f2.following.userId FROM Follow f2 WHERE f2.follower.userId = :userId AND f2.status = 'ACCEPTED')")
    List<Profile> findFollowBackSuggestions(@Param("userId") Long userId);

    @Query("SELECT p FROM Profile p WHERE p.userId != :currentId AND p.userId NOT IN " +
            "(SELECT f.following.userId FROM Follow f WHERE f.follower.userId = :currentId)")
    List<Profile> findRandomSuggestions(@Param("currentId") Long currentId);

    // 4. Check if following is accepted (used for button state logic)
    @Query("SELECT COUNT(f) > 0 FROM Follow f WHERE f.follower.userId = :followerId AND f.following.userId = :followingId AND f.status = 'ACCEPTED'")
    boolean isFollowingAccepted(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    // 5. Check if a request is pending (New: helpful for the "Requested" button state)
    @Query("SELECT COUNT(f) > 0 FROM Follow f WHERE f.follower.userId = :followerId AND f.following.userId = :followingId AND f.status = 'PENDING'")
    boolean isFollowRequested(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    @Modifying
    @Query("""
        DELETE FROM Follow f
        WHERE f.follower.userId = :profileId
           OR f.following.userId = :profileId
    """)
    void deleteAllByProfileId(Long profileId);


        // --- Basic Status Checks ---

        // Replaces: findByFollowingAndIsAccept(Profile me, boolean b)
        List<Follow> findByFollowingAndStatus(Profile following, FollowStatus status);

        // Replaces: existsByFollowerAndFollowingAndIsAccept
        boolean existsByFollowerAndFollowingAndStatus(Profile follower, Profile following, FollowStatus status);

        // This stays the same (useful for toggling)
        Optional<Follow> findByFollowerAndFollowing(Profile follower, Profile following);

        // --- ID Based Lookups (Useful for Controller calls) ---

        // Replaces: findByFollowing_UserIdAndIsAcceptFalse (Pending requests for me)
        List<Follow> findByFollowingUserIdAndStatus(Long userId, FollowStatus status);

        // Replaces: findByFollowing_UserIdAndIsAcceptTrue (My accepted followers)
        // Replaces: findByFollower_UserIdAndIsAcceptTrue (People I follow)
        List<Follow> findByFollowerUserIdAndStatus(Long followerId, FollowStatus status);
    Optional<Follow> findByFollower_UserIdAndFollowing_UserIdAndStatus(
            Long followerUserId,
            Long followingUserId,
            FollowStatus status
    );


    Follow findByFollower_UserIdAndFollowing_UserId(Long followerId, Long userId);
    boolean existsByFollower_UserIdAndFollowing_UserId(Long followerId, Long followingId);

    boolean existsByFollowerUserIdAndFollowingUserIdAndStatus(Long requesterId, Long userId, FollowStatus followStatus);
}
