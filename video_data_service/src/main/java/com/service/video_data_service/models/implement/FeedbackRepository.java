package com.service.video_data_service.models.implement;
import com.service.video_data_service.models.model.*;
import com.service.video_data_service.models.model.tags.FeedbackStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByStatus(FeedbackStatus status);
    List<Feedback> findByUserId(Long userId);
    List<Feedback> findByAdminId(Long adminId);
}
