package com.service.video_data_service.models.service;

import com.service.video_data_service.models.implement.FavoriteRepository;
import com.service.video_data_service.models.implement.UserRepository;
import com.service.video_data_service.models.implement.VideoRepository;
import com.service.video_data_service.models.model.Category;
import com.service.video_data_service.models.model.Favorite;
import com.service.video_data_service.models.model.User;
import com.service.video_data_service.models.model.Video;
import com.service.video_data_service.utils.EncryptTools.CryptoUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService {
    @Resource
    private FavoriteRepository favoriteRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private VideoRepository videoRepository;
    public boolean add_favorite(String username,Long video_id,boolean isLiked){
        Optional<User>optionalUser=userRepository.findByUsername(username);
        if (optionalUser.isEmpty()){
            return false;
        }
        else {
            Optional<Video>optionalVideo=videoRepository.findById(video_id);
            if (optionalUser.isEmpty()){
                return false;
            }
            else {
                boolean is_exist=favoriteRepository.existsByVideoAndUser(optionalVideo.get(),optionalUser.get());
                if (is_exist){
                    Optional<Favorite>optionalFavorite=favoriteRepository.findByUserAndVideo(optionalUser.get(),optionalVideo.get());
                    if (optionalFavorite.isEmpty()){
                        return false;
                    }
                    favoriteRepository.delete(optionalFavorite.get());
                    Video video=optionalVideo.get();
                    video.setLikeCount(video.getLikeCount()-1);
                    videoRepository.save(video);
                    isLiked=false;
                    return true;
                }
                Favorite favorite=new Favorite(optionalUser.get(),optionalVideo.get());
                Favorite result=favoriteRepository.save(favorite);
                Video video=optionalVideo.get();
                video.setLikeCount(video.getLikeCount()+1);
                videoRepository.save(video);
                User user=new User();
                user.setUsername(result.getUser().getUsername());
                result.setUser(user);
                isLiked=true;
                return true;
            }
        }
    }
    public Favorite get_one_record(String username,Long video_id){
        Optional<User>optionalUser=userRepository.findByUsername(username);
        if (optionalUser.isEmpty()){
            return null;
        }
        Optional<Video>optionalVideo=videoRepository.findById(video_id);
        if (optionalVideo.isEmpty()){
            return null;
        }
        Optional<Favorite>optionalFavorite=favoriteRepository.findByUserAndVideo(optionalUser.get(),optionalVideo.get());
        if (optionalFavorite.isEmpty()){
            return null;
        }else {
            return optionalFavorite.get();
        }
    }
    public List<Favorite>get_favorite_videos(String username) throws Exception {
        Optional<User>optionalUser=userRepository.findByUsername(username);
        if (optionalUser.isEmpty()){
            return List.of();
        }
        List<Favorite>favoriteList=favoriteRepository.findByUser(optionalUser.get());
        for (int i = 0; i < favoriteList.size(); i++) {
            User user=favoriteList.get(i).getUser();
            User ret_usr=new User();
            ret_usr.setUsername(user.getUsername());
            favoriteList.get(i).setUser(ret_usr);
            Video video=favoriteList.get(i).getVideo();
            video.setDescription(CryptoUtils.decrypt_msg(video.getDescription(),video.getUser().getCryptoKey()));
            video.setCoverImage(CryptoUtils.decrypt_msg(video.getCoverImage(),video.getUser().getCryptoKey()));
            video.setTitle(CryptoUtils.decrypt_msg(video.getTitle(),video.getUser().getCryptoKey()));
            video.setVideoUrl(CryptoUtils.decrypt_msg(video.getVideoUrl(),video.getUser().getCryptoKey()));
            video.setUser(null);
            favoriteList.get(i).setVideo(video);
        }
        return favoriteList;
    }
}
