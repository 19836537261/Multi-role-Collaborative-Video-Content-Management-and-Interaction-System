package com.service.video_data_service.models.service;

import com.service.video_data_service.configs.ConstConfig.PathHelper;
import com.service.video_data_service.models.implement.FavoriteRepository;
import com.service.video_data_service.models.implement.UserRepository;
import com.service.video_data_service.models.implement.VideoRepository;
import com.service.video_data_service.models.implement.VideoTypeRepository;
import com.service.video_data_service.models.model.Favorite;
import com.service.video_data_service.models.model.User;
import com.service.video_data_service.models.model.Video;
import com.service.video_data_service.models.model.VideoType;
import com.service.video_data_service.models.model.tags.VideoStatus;
import com.service.video_data_service.utils.EncryptTools.CryptoUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class VideoService {
    @Resource
    private VideoRepository videoRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private VideoTypeRepository videoTypeRepository;
    @Resource
    private FavoriteRepository favoriteRepository;
    @Resource
    private PathHelper pathHelper;
    @Resource
    private AuditService auditService;
    public Video add_new_video(String title,String description,String img_dat,String video_dat,String video_type,String username) throws Exception {
        Optional<User>optionalUser=userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
        {
            return null;
        }
        Long unique_id=System.currentTimeMillis();
        Video video=new Video(
                CryptoUtils.encrypt_msg(title,optionalUser.get().getCryptoKey()),
                CryptoUtils.encrypt_msg(description,optionalUser.get().getCryptoKey()),optionalUser.get());
        write_video_file(video_dat,unique_id);
        String video_url=pathHelper.video_url_path_root+unique_id+".mp4";
        video.setVideoUrl(CryptoUtils.encrypt_msg(video_url,optionalUser.get().getCryptoKey()));
        write_img_file(img_dat,unique_id);
        String img_url=pathHelper.image_url_path_root+unique_id+".png";
        video.setCoverImage(CryptoUtils.encrypt_msg(img_url,optionalUser.get().getCryptoKey()));
        video.setStatus(VideoStatus.PENDING);
        video.setLikeCount(0);
        video.setViewCount(0);
        video.video_type=video_type;
        Video result=videoRepository.save(video);
        return result;
    }
    public List<Video>get_videos_by_user(User user) throws Exception {
        List<Video>videoList=videoRepository.findByUserId(user.getId());
        List<Video>result=new ArrayList<>();
        for (int i = 0; i < videoList.size(); i++) {
            Video temp=videoList.get(i);
            temp.setTitle(CryptoUtils.decrypt_msg(temp.getTitle(),user.getCryptoKey()));
            temp.setCoverImage(CryptoUtils.decrypt_msg(temp.getCoverImage(),user.getCryptoKey()));
            temp.setVideoUrl(CryptoUtils.decrypt_msg(temp.getVideoUrl(),user.getCryptoKey()));
            temp.setDescription(CryptoUtils.decrypt_msg(temp.getDescription(),user.getCryptoKey()));
            temp.setUser(null);
            result.add(i,temp);
        }
        return result;
    }
    public boolean change_one_video(String title,String description,String img_dat,String video_dat, Long id,User user,String video_type) throws Exception {
        Long unique_id=System.currentTimeMillis();
        int result=-1;
        String img_url=pathHelper.image_url_path_root+unique_id+".png";
        String video_url=pathHelper.video_url_path_root+unique_id+".mp4";
        if (img_dat!=null&&video_dat!=null)
        {
            write_img_file(img_dat,unique_id);
            write_video_file(video_dat,unique_id);
            result=videoRepository.updateVideoByVideoId(
                    CryptoUtils.encrypt_msg(title,user.getCryptoKey()),
                    CryptoUtils.encrypt_msg(description,user.getCryptoKey()), CryptoUtils.encrypt_msg(img_url,user.getCryptoKey()), CryptoUtils.encrypt_msg(video_url,user.getCryptoKey()),video_type, id);
        } else if (img_dat!=null&&video_dat==null) {
            write_img_file(img_dat,unique_id);
            result=videoRepository.updateVideoImgUrlByVideoId(
                    CryptoUtils.encrypt_msg(title,user.getCryptoKey()),
                    CryptoUtils.encrypt_msg(description,user.getCryptoKey()),CryptoUtils.encrypt_msg(img_url,user.getCryptoKey()),video_type,id);
        } else if (video_dat!=null&&img_dat==null) {
            write_video_file(video_dat,unique_id);
            result=videoRepository.updateVideoUrlByVideoId(
                    CryptoUtils.encrypt_msg(title,user.getCryptoKey()),
                    CryptoUtils.encrypt_msg(description,user.getCryptoKey()),CryptoUtils.encrypt_msg(video_url,user.getCryptoKey()),video_type,id);
        } else if (video_dat==null&&img_dat==null) {
            result=videoRepository.updateVideoMsgByVideoId(
                    CryptoUtils.encrypt_msg(title,user.getCryptoKey()),
                    CryptoUtils.encrypt_msg(description,user.getCryptoKey()),video_type, id);
        }
        return (result==1)?true:false;
    }
    public void write_video_file(String video_dat,Long unique_id) throws IOException {
        if (video_dat.contains("video/mp4")){
            File video_file=new File(pathHelper.video_absolute_path_root+unique_id+".mp4");
            FileOutputStream video_fileOutputStream=new FileOutputStream(video_file);
            video_fileOutputStream.write(Base64.getDecoder().decode(video_dat.substring(22).getBytes()));
            video_fileOutputStream.close();
        }
    }
    public void write_img_file(String img_dat,Long unique_id) throws IOException {
        if (img_dat.contains("image/jpeg")){
            File img_file=new File(pathHelper.image_absolute_path_root+unique_id+".png");
            FileOutputStream img_fileOutputStream=new FileOutputStream(img_file);
            img_fileOutputStream.write(Base64.getDecoder().decode(img_dat.substring(23).getBytes()));
            img_fileOutputStream.close();
        } else if (img_dat.contains("image/png")) {
            File img_file=new File(pathHelper.image_absolute_path_root+unique_id+".png");
            FileOutputStream img_fileOutputStream=new FileOutputStream(img_file);
            img_fileOutputStream.write(Base64.getDecoder().decode(img_dat.substring(22).getBytes()));
            img_fileOutputStream.close();
        }
    }
    public List<VideoType>get_all_type(){
        return videoTypeRepository.findAll();
    }
    public List<Video>get_video_by_kind_and_index(String category,int page,String sort) throws Exception {
        int start=page*6;
        List<Video>videoList;
        if (sort.compareTo("id")==0)
        {
            videoList=videoRepository.findByVideo_type(category,start);
        }else if (sort.compareTo("like_count")==0){
            videoList=videoRepository.findByVideo_typeSortByLikeCount(category,start);
        }else {
            videoList=videoRepository.findByVideo_typeSortByTime(category,start);
        }
        for (int i = 0; i < videoList.size(); i++) {
            User final_user=new User();
            Video video=videoList.get(i);
            User user=video.getUser();
            final_user.setUsername(user.getUsername());
            video.setDescription(CryptoUtils.decrypt_msg(video.getDescription(),user.getCryptoKey()));
            video.setCoverImage(CryptoUtils.decrypt_msg(video.getCoverImage(),user.getCryptoKey()));
            video.setTitle(CryptoUtils.decrypt_msg(video.getTitle(),user.getCryptoKey()));
            video.setVideoUrl(CryptoUtils.decrypt_msg(video.getVideoUrl(),user.getCryptoKey()));
            video.setUser(final_user);
        }
        return videoList;
    }
    public Optional<Video>get_video_by_id(Long id){
        return videoRepository.findById(id);
    }
    public boolean delete_video_by_id(Long id){
        Optional<Video>optionalVideo=videoRepository.findById(id);
        if (optionalVideo.isEmpty()){
            return false;
        }else {
            List<Favorite>optionalFavorite=favoriteRepository.findAllByVideo(optionalVideo.get());
            for (int i = 0; i < optionalFavorite.size(); i++) {
                favoriteRepository.delete(optionalFavorite.get(i));
            }
            try {
                videoRepository.delete(optionalVideo.get());
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
    public List<Video>get_all_videos() throws Exception {
        List<Video>videoList=videoRepository.findAll();
        List<Video>result=new ArrayList<>();
        for (int i = 0; i < videoList.size(); i++) {
            Video video=videoList.get(i);
            User user=video.getUser();
            Video temp=new Video(
                    CryptoUtils.decrypt_msg(video.getTitle(),user.getCryptoKey()),
                    CryptoUtils.decrypt_msg(video.getDescription(),user.getCryptoKey()),
                    CryptoUtils.decrypt_msg(video.getCoverImage(),user.getCryptoKey()),
                    CryptoUtils.decrypt_msg(video.getVideoUrl(),user.getCryptoKey()),user);
            temp.setId(video.getId());
            temp.video_type=video.video_type;
            temp.setCreateTime(video.getCreateTime());
            temp.setUpdateTime(video.getUpdateTime());
            temp.setStatus(video.getStatus());
            result.add(temp);
        }
        return result;
    }
    public List<Video>get_all_videos_by_status() throws Exception {
        List<Video>videoList=videoRepository.findAll();
        List<Video>result=new ArrayList<>();
        for (int i = 0; i < videoList.size(); i++) {
            Video video=videoList.get(i);
            if (video.getStatus()==VideoStatus.APPROVED){
                User user=video.getUser();
                Video temp=new Video(
                        CryptoUtils.decrypt_msg(video.getTitle(),user.getCryptoKey()),
                        CryptoUtils.decrypt_msg(video.getDescription(),user.getCryptoKey()),
                        CryptoUtils.decrypt_msg(video.getCoverImage(),user.getCryptoKey()),
                        CryptoUtils.decrypt_msg(video.getVideoUrl(),user.getCryptoKey()),user);
                temp.setId(video.getId());
                temp.video_type=video.video_type;
                temp.setCreateTime(video.getCreateTime());
                temp.setUpdateTime(video.getUpdateTime());
                temp.setStatus(video.getStatus());
                result.add(temp);
            }
        }
        return result;
    }

    /**
     *
     * @param status  status等于1代表审核通过，等于0代表审核不通过。
     * @param id
     * @return
     */
    public boolean update_video_by_status(int status,Long id,String username) throws Exception {
        Optional<Video>optionalVideo=videoRepository.findById(id);
        if (optionalVideo.isEmpty()){
            return false;
        }
        Optional<User>optionalUser=userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
        {
            return false;
        }
        Video video=optionalVideo.get();
        if (status==1){
            video.setStatus(VideoStatus.APPROVED);
            auditService.add_audit_log("管理员："+optionalUser.get().getUsername()+"通过了视频："+CryptoUtils.decrypt_msg(video.getTitle(),video.getUser().getCryptoKey())+"的审核请求",username);
        } else if (status==0) {
            video.setStatus(VideoStatus.REJECTED);
            auditService.add_audit_log("管理员："+optionalUser.get().getUsername()+"拦截了视频："+CryptoUtils.decrypt_msg(video.getTitle(),video.getUser().getCryptoKey())+"的审核请求",username);
        }
        try {
            videoRepository.save(video);
            return true;
        }catch (Exception exception)
        {
            exception.printStackTrace();
            return false;
        }
    }
}
