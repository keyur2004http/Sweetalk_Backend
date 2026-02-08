package com.example.Sweetalk.Repository;
import com.example.Sweetalk.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {
    Optional<Users> findByUsername(String username);

    @Modifying
    @Query("DELETE FROM Users u WHERE u.username = :username")
    void deleteByUsername(String username);
}

