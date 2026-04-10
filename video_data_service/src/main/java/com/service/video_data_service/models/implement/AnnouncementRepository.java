package com.service.video_data_service.models.implement;
import com.service.video_data_service.models.model.*;
import com.service.video_data_service.models.model.tags.FeedbackStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findAll();
    List<Announcement> findAllByAdmin(User admin);
    Optional<Announcement> findByIdAndAdmin(Long id,User admin);
}
