package com.service.video_data_service.models.implement;
import com.service.video_data_service.models.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment save(Comment entity);
    @Query(value = "select * from comments where video_id=:id",nativeQuery = true)
    List<Comment>findByVideo(@Param("id")Long id);
    Optional<Comment>findById(Long id);
}
