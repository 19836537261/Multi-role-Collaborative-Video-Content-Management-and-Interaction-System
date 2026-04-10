package com.service.video_data_service.models.implement;
import com.service.video_data_service.models.model.*;
import com.service.video_data_service.models.model.tags.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByAdminId(Long adminId);
}