package com.service.video_data_service.models.implement;
import com.service.video_data_service.models.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublisherApplicationRepository extends JpaRepository<PublisherApplication, Long> {
    PublisherApplication save(PublisherApplication publisherApplication);
    Optional<PublisherApplication> findByUser(User user);
    List<PublisherApplication> findAll();
    Optional<PublisherApplication> findById(Long id);
}