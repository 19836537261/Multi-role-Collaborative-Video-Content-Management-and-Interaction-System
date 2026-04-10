package com.service.video_data_service.models.model;

import com.service.video_data_service.models.model.tags.AuditAction;
import com.service.video_data_service.models.model.tags.AuditType;
import com.service.video_data_service.models.model.tags.UserRole;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private LocalDateTime createTime = LocalDateTime.now();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User admin;
    public AuditLog() {}
    public AuditLog(String content,User admin) {
        this.content=content;
        this.admin = admin;
    }
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public User getAdmin() { return admin; }
    public void setAdmin(User admin) { this.admin = admin; }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}