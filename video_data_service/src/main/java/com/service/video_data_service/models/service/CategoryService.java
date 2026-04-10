package com.service.video_data_service.models.service;

import com.service.video_data_service.configs.ConstConfig.PathHelper;
import com.service.video_data_service.models.implement.CategoryRepository;
import com.service.video_data_service.models.implement.UserRepository;
import com.service.video_data_service.models.implement.VideoRepository;
import com.service.video_data_service.models.model.Category;
import com.service.video_data_service.models.model.User;
import com.service.video_data_service.models.model.Video;
import com.service.video_data_service.models.model.tags.AuditStatus;
import com.service.video_data_service.models.model.tags.VideoStatus;
import com.service.video_data_service.utils.EncryptTools.CryptoUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class CategoryService {
    @Resource
    private CategoryRepository categoryRepository;
    @Resource
    private VideoRepository videoRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private AuditService auditService;
    @Resource
    private PathHelper pathHelper;
    public Category add_new_category(String name, String desc,String img_dat, User user) throws Exception {
        Long unique_id=System.currentTimeMillis();
        String img_url=pathHelper.image_url_path_root+unique_id+".png";
        Category category=new Category(
                CryptoUtils.encrypt_msg(name,user.getCryptoKey()),
                CryptoUtils.encrypt_msg(desc,user.getCryptoKey()),user);
        category.setThumbnail(CryptoUtils.encrypt_msg(img_url,user.getCryptoKey()));
        write_img_file(img_dat,unique_id);
        return categoryRepository.save(category);
    }
    public List<Category>get_category_by_user(User user) throws Exception {
        List<Category>categoryList=categoryRepository.findByUserId(user.getId());
        List<Category>categories=new ArrayList<>();
        for (int i = 0; i < categoryList.size(); i++) {
            Category temp=categoryList.get(i);
            temp.setThumbnail(CryptoUtils.decrypt_msg(temp.getThumbnail(),user.getCryptoKey()));
            temp.setDescription(CryptoUtils.decrypt_msg(temp.getDescription(),user.getCryptoKey()));
            temp.setName(CryptoUtils.decrypt_msg(temp.getName(),user.getCryptoKey()));
            temp.setUser(null);
            categories.add(i,temp);
        }
        return categories;
    }
    public boolean attach_video(Long category_id, Long video_id){
        Optional<Video>optionalVideo=videoRepository.findById(video_id);
        if (optionalVideo.isEmpty()){
            return false;
        }else {
            Optional<Category>optionalCategory=categoryRepository.findById(category_id);
            if (optionalCategory.isEmpty())
            {
                return false;
            }else {
                Category category=optionalCategory.get();
                List<Long>video_list=category.getVideos();
                video_list.add(optionalVideo.get().getId());
                category.setVideos(video_list);
                categoryRepository.save(category);
                return true;
            }
        }
    }
    public boolean detach_videos(Long category_id, Long video_id){
        Optional<Video>optionalVideo=videoRepository.findById(video_id);
        if (optionalVideo.isEmpty()){
            return false;
        }else {
            Optional<Category>optionalCategory=categoryRepository.findById(category_id);
            if (optionalCategory.isEmpty())
            {
                return false;
            }else {
                Category category=optionalCategory.get();
                List<Long>video_list=category.getVideos();
                List<Long>back_list=new ArrayList<>();
                for (int i = 0; i < video_list.size(); i++) {
                    if (optionalVideo.get().getId()!=video_list.get(i)){
                        back_list.add(video_list.get(i));
                    }
                }
                category.setVideos(back_list);
                categoryRepository.save(category);
                return true;
            }
        }
    }
    public List<Video>get_all_category_videos(Long category_id) throws Exception {
        Optional<Category>optionalCategory=categoryRepository.findById(category_id);
        if (optionalCategory.isEmpty())
        {
            return List.of();
        }else {
            List<Video>videoList=new ArrayList<>();
            Category category=optionalCategory.get();
            for (int i = 0; i < category.getVideos().size(); i++) {
                Video video_found=videoRepository.getById(category.getVideos().get(i));
                video_found.setDescription(CryptoUtils.decrypt_msg(video_found.getDescription(),video_found.getUser().getCryptoKey()));
                video_found.setCoverImage(CryptoUtils.decrypt_msg(video_found.getCoverImage(),video_found.getUser().getCryptoKey()));
                video_found.setTitle(CryptoUtils.decrypt_msg(video_found.getTitle(),video_found.getUser().getCryptoKey()));
                video_found.setVideoUrl(CryptoUtils.decrypt_msg(video_found.getVideoUrl(),video_found.getUser().getCryptoKey()));
                video_found.setUser(null);
                videoList.add(i,video_found);
            }
            return videoList;
        }
    }
    public List<Video>get_no_category_videos(String username,Long category_id) throws Exception {
        Optional<User>optionalUser=userRepository.findByUsername(username);
        if (optionalUser.isEmpty()){
            return List.of();
        }
        else {
            Optional<Category>optionalCategory=categoryRepository.findById(category_id);
            if (optionalUser.isEmpty()){
                return List.of();
            }
            List<Video>user_videos=videoRepository.findByUserId(optionalUser.get().getId());
            List<Video>videoList=new ArrayList<>();
            List<Long>attached_videos=optionalCategory.get().getVideos();
            for (int i = 0; i < user_videos.size(); i++) {
                if (!attached_videos.contains(user_videos.get(i).getId()))
                {
                    Video video_found=user_videos.get(i);
                    video_found.setDescription(CryptoUtils.decrypt_msg(video_found.getDescription(),video_found.getUser().getCryptoKey()));
                    video_found.setCoverImage(CryptoUtils.decrypt_msg(video_found.getCoverImage(),video_found.getUser().getCryptoKey()));
                    video_found.setTitle(CryptoUtils.decrypt_msg(video_found.getTitle(),video_found.getUser().getCryptoKey()));
                    video_found.setVideoUrl(CryptoUtils.decrypt_msg(video_found.getVideoUrl(),video_found.getUser().getCryptoKey()));
                    video_found.setUser(null);
                    videoList.add(video_found);
                }
            }
            return videoList;
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
    public boolean update_category_msg(Long category_id, String description, String name, String img_dat) throws Exception {
        Long unique_id=System.currentTimeMillis();
        String img_url=pathHelper.image_url_path_root+unique_id+".png";
        Optional<Category> optionalCategory=categoryRepository.findById(category_id);
        if (optionalCategory.isEmpty())
        {
            return false;
        }else {
            Category category=optionalCategory.get();
            category.setDescription(CryptoUtils.encrypt_msg(description,category.getUser().getCryptoKey()));
            category.setName(CryptoUtils.encrypt_msg(name,category.getUser().getCryptoKey()));
            if (img_dat!=""){
                category.setThumbnail(CryptoUtils.encrypt_msg(img_url,category.getUser().getCryptoKey()));
                write_img_file(img_dat,unique_id);
            }
            categoryRepository.save(category);
            return true;
        }
    }
    public List<Object>get_all_user_categories() throws Exception {
        List<Category>categoryList=categoryRepository.findByStatus(AuditStatus.APPROVED);
        List<Object>result=new ArrayList<>();
        for (int i = 0; i < categoryList.size(); i++) {
            HashMap<String,Object>temp=new HashMap<>();
            Category category=categoryList.get(i);
            temp.put("username",category.getUser().getUsername());
            String decrypt_key=category.getUser().getCryptoKey();
            category.setDescription(CryptoUtils.decrypt_msg(category.getDescription(),decrypt_key));
            category.setName(CryptoUtils.decrypt_msg(category.getName(),decrypt_key));
            category.setThumbnail(CryptoUtils.decrypt_msg(category.getThumbnail(),decrypt_key));
            category.setUser(null);
            temp.put("category_msg",category);
            List<Long>longList=category.getVideos();
            List<Video>videoList=new ArrayList<>();
            for (Long item:longList) {
                Optional<Video>videoOptional=videoRepository.findById(item);
                if (videoOptional.isEmpty()){
                    break;
                }
                if (videoOptional.get().getStatus()== VideoStatus.APPROVED){
                    Video video=videoOptional.get();
                    video.setDescription(CryptoUtils.decrypt_msg(video.getDescription(),video.getUser().getCryptoKey()));
                    video.setCoverImage(CryptoUtils.decrypt_msg(video.getCoverImage(),video.getUser().getCryptoKey()));
                    video.setTitle(CryptoUtils.decrypt_msg(video.getTitle(),video.getUser().getCryptoKey()));
                    video.setVideoUrl(CryptoUtils.decrypt_msg(video.getVideoUrl(),video.getUser().getCryptoKey()));
                    video.setUser(null);
                    videoList.add(video);
                }
            }
            temp.put("category_videos",videoList);
            result.add(temp);
        }
        return result;
    }
    public List<Category>get_all_categories_by_manager() throws Exception {
        List<Category>categoryList=categoryRepository.findAll();
        for (int i = 0; i < categoryList.size(); i++) {
            categoryList.get(i).setDescription(CryptoUtils.decrypt_msg(categoryList.get(i).getDescription(),categoryList.get(i).getUser().getCryptoKey()));
            categoryList.get(i).setName(CryptoUtils.decrypt_msg(categoryList.get(i).getName(),categoryList.get(i).getUser().getCryptoKey()));
            categoryList.get(i).setThumbnail(CryptoUtils.decrypt_msg(categoryList.get(i).getThumbnail(),categoryList.get(i).getUser().getCryptoKey()));
        }
        return categoryList;
    }

    /**
     *
     * @param id
     * @param status status等于1代表审核通过，等于0代表审核不通过
     * @param username
     * @return
     */
    public boolean audit_one_category(Long id,int status,String username) throws Exception {
        Optional<Category>categoryOptional=categoryRepository.findById(id);
        if (categoryOptional.isEmpty()){
            return false;
        }
        Optional<User>optionalUser=userRepository.findByUsername(username);
        if (optionalUser.isEmpty()){
            return false;
        }
        Category category=categoryOptional.get();
        if (status==1){
            category.setStatus(AuditStatus.APPROVED);
            auditService.add_audit_log("管理员:"+username+" 通过了栏目:"+CryptoUtils.decrypt_msg(category.getName(),category.getUser().getCryptoKey())+" 的审核请求",username);
        } else if (status==0) {
            category.setStatus(AuditStatus.REJECTED);
            auditService.add_audit_log("管理员:"+username+" 驳回了栏目:"+CryptoUtils.decrypt_msg(category.getName(),category.getUser().getCryptoKey())+" 的审核请求",username);
        }
        categoryRepository.save(category);
        return true;
    }
}
