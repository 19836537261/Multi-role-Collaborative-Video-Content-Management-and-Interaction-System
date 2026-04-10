package com.service.video_data_service.models.implement;
import com.service.video_data_service.models.model.*;
import com.service.video_data_service.models.model.tags.*;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByStatus(VideoStatus status);
    List<Video> findByUserId(Long userId);
    Optional<Video>findById(Long id);
    @Modifying
    @Transactional
    @Query(value = "update videos set title=:title,description=:desc,cover_image=:cover_img,video_url=:video_url,status='PENDING',like_count=0,video_type=:video_type where id=:id",nativeQuery = true)
    int updateVideoByVideoId(@Param("title")String title,
                             @Param("desc")String description,
                             @Param("cover_img")String cover_img,
                             @Param("video_url")String video_url,
                             @Param("video_type")String video_type,
                             @Param("id")Long id);
    @Modifying
    @Transactional
    @Query(value = "update videos set title=:title,description=:desc,video_url=:video_url,status='PENDING',like_count=0,video_type=:video_type where id=:id",nativeQuery = true)
    int updateVideoUrlByVideoId(@Param("title")String title,
                             @Param("desc")String description,
                             @Param("video_url")String video_url,
                                @Param("video_type")String video_type,
                             @Param("id")Long id);
    @Modifying
    @Transactional
    @Query(value = "update videos set title=:title,description=:desc,cover_image=:cover_img,status='PENDING',like_count=0,video_type=:video_type where id=:id",nativeQuery = true)
    int updateVideoImgUrlByVideoId(@Param("title")String title,
                                   @Param("desc")String description,
                                   @Param("cover_img")String cover_img,
                                   @Param("video_type")String video_type,
                                   @Param("id")Long id);
    @Modifying
    @Transactional
    @Query(value = "update videos set title=:title,description=:desc,status='PENDING',like_count=0,video_type=:video_type where id=:id",nativeQuery = true)
    int updateVideoMsgByVideoId(@Param("title")String title,
                                   @Param("desc")String description,
                                @Param("video_type")String video_type,
                                   @Param("id")Long id);
    @Query(value = "select * from videos where video_type=:category and status='APPROVED' order by like_count desc limit :no,6",nativeQuery = true)
    List<Video>findByVideo_typeSortByLikeCount(@Param("category")String category,@Param("no")int index);
    @Query(value = "select * from videos where video_type=:category and status='APPROVED' order by create_time desc limit :no,6",nativeQuery = true)
    List<Video>findByVideo_typeSortByTime(@Param("category")String category,@Param("no")int index);
    @Query(value = "select * from videos where video_type=:category and status='APPROVED' order by id limit :no,6",nativeQuery = true)
    List<Video>findByVideo_type(@Param("category")String category,@Param("no")int index);
    List<Video>findAll();
}
