package com.service.video_data_service.models.implement;

import com.service.video_data_service.models.model.VideoType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoTypeRepository extends JpaRepository<VideoType,Long> {
    List<VideoType> findAll();
    Optional<VideoType> findByType(String type);
}
