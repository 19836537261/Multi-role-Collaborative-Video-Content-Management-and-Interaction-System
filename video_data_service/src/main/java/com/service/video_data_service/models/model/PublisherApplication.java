package com.service.video_data_service.models.model;

import com.service.video_data_service.models.model.tags.AuditStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "publisher_applications")
public class PublisherApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String real_name;
    private String contact;
    @Column(length = 1000)
    private String applicationReason;
    @Enumerated(EnumType.STRING)
    private AuditStatus status = AuditStatus.PENDING;
    private LocalDateTime createTime = LocalDateTime.now();
    private LocalDateTime processTime;//处理时间，直接用LocalDateTime.now()表示
    private String responseText;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User admin;

    public PublisherApplication() {}

    public PublisherApplication(String real_name,String contact,String applicationReason, User user) {
        this.real_name=real_name;
        this.contact=contact;
        this.applicationReason = applicationReason;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getApplicationReason() { return applicationReason; }
    public void setApplicationReason(String applicationReason) { this.applicationReason = applicationReason; }
    
    public AuditStatus getStatus() { return status; }
    public void setStatus(AuditStatus status) { this.status = status; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getProcessTime() { return processTime; }
    public void setProcessTime(LocalDateTime processTime) { this.processTime = processTime; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public User getAdmin() { return admin; }
    public void setAdmin(User admin) { this.admin = admin; }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }
}