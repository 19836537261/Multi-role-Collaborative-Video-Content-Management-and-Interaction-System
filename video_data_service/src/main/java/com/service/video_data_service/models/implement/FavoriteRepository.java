package com.service.video_data_service.models.implement;
import com.service.video_data_service.models.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserAndVideo(User user,Video video);
    List<Favorite> findByUserId(Long userId);
    List<Favorite> findByUser(User user);
    List<Favorite>findAllByVideo(Video video);
    boolean existsByVideoAndUser(Video video,User user);

    Favorite save(Favorite entity);
}
