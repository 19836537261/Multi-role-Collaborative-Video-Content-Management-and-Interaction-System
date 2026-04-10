package com.service.video_data_service.models.implement;

import com.service.video_data_service.models.model.User;
import com.service.video_data_service.models.model.tags.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndPassword(String username,String password);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    @Query(value = "select * from users where username=?1",nativeQuery = true)
    Optional<User> searchOneUser(String username);
    Optional<User>findById(Long id);
}
