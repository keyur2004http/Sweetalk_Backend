package com.example.Sweetalk.Repository;

import com.example.Sweetalk.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderAndReceiverOrReceiverAndSender(
            String sender1, String receiver1, String sender2, String receiver2);

    @Query("SELECT DISTINCT m.sender FROM Message m WHERE m.receiver = :username " +
            "UNION SELECT DISTINCT m.receiver FROM Message m WHERE m.sender = :username")
    List<String> findChatUsernames(@Param("username") String username);
        void deleteBySenderOrReceiver(String sender, String receiver);
    @Modifying
    @Query("""
        DELETE FROM Message m
        WHERE m.sender = :username
           OR m.receiver = :username
    """)
    void deleteUserMessages(String username);

}
