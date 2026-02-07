package com.example.Sweetalk.Model;

import com.example.Sweetalk.Enum.FollowStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"}))
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id", referencedColumnName = "id" ,nullable = false) // Fix: link to the @Column name "id"
    private Profile follower;

    @ManyToOne
    @JoinColumn(name = "following_id", referencedColumnName = "id") // Fix: link to the @Column name "id"
    private Profile following;

    @Enumerated(EnumType.STRING)
    private FollowStatus status; // PENDING or ACCEPTED

    private Long createdAt = System.currentTimeMillis();

    // Getters and Setters
   public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Profile getFollower() {
        return follower;
    }

    public void setFollower(Profile follower) {
        this.follower = follower;
    }

    public Profile getFollowing() {
        return following;
    }

    public void setFollowing(Profile following) {
        this.following = following;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FollowStatus getStatus() {
        return status;
    }

    public void setStatus(FollowStatus status) {
        this.status = status;
    }

    public Follow() {

    }

}
