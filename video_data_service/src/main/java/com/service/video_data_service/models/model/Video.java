package com.service.video_data_service.models.model;
import com.service.video_data_service.models.model.tags.VideoStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    private String coverImage;
    private String videoUrl;
    
    @Enumerated(EnumType.STRING)
    private VideoStatus status = VideoStatus.PENDING;
    
    private Integer viewCount = 0;
    private Integer likeCount = 0;
    private LocalDateTime createTime = LocalDateTime.now();
    private LocalDateTime updateTime = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    public String video_type;//生活，娱乐，教育，体育，音乐，游戏

    public Video() {}

    public Video(String title, String description, User user) {
        this.title = title;
        this.description = description;
        this.user = user;
    }
    public Video(String title, String description,String coverImage,String videoUrl, User user) {
        this.title = title;
        this.description = description;
        this.user = user;
        this.coverImage=coverImage;
        this.videoUrl=videoUrl;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    
    public VideoStatus getStatus() { return status; }
    public void setStatus(VideoStatus status) { this.status = status; }
    
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}