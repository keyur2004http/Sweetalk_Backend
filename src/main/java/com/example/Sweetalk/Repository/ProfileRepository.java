package com.example.Sweetalk.Repository;

import com.example.Sweetalk.Model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile,Long> {
    Optional<Profile>findByUsername(String username);
//    Optional<Profile> findByUserId(Long userId);

    @Query("SELECT p.followers FROM Profile p WHERE p.username = :username")
    List<Profile> findFollowersByUsername(@Param("username") String username);

    List<Profile> findByUsernameContainingIgnoreCase(String username);
    List<Profile> findByUsernameIn(List<String> usernames);
    void deleteByUserId(Long userId);





}
