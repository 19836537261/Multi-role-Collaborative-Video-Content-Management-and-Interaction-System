package com.service.video_data_service.models.model;

import jakarta.persistence.*;

@Entity
@Table(name = "video_type")
public class VideoType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public String type;
}
