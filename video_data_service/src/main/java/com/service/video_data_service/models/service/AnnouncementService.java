package com.service.video_data_service.models.service;

import com.service.video_data_service.configs.ConstConfig.PathHelper;
import com.service.video_data_service.models.implement.AnnouncementRepository;
import com.service.video_data_service.models.implement.UserRepository;
import com.service.video_data_service.models.model.Announcement;
import com.service.video_data_service.models.model.User;
import com.service.video_data_service.utils.EncryptTools.CryptoUtils;
import com.service.video_data_service.utils.JWTTools.JWTConfigurationTool;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementService {
    @Resource
    private JWTConfigurationTool jwtConfigurationTool;
    @Resource
    private AnnouncementRepository announcementRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private AuditService auditService;
    @Resource
    private PathHelper pathHelper;
    public Announcement add_announce(String title,String content,String img_dat,String token){
        Long unique_id=System.currentTimeMillis();
        String img_url=pathHelper.image_url_path_root+unique_id+".png";
        String username=jwtConfigurationTool.getUserName(token);
        Optional<User>optionalUser=userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
        {
            return null;
        }
        try {
            Announcement announcement=new Announcement(
                    CryptoUtils.encrypt_msg(title,optionalUser.get().getCryptoKey()),
                    CryptoUtils.encrypt_msg(content,optionalUser.get().getCryptoKey()),optionalUser.get());
            write_img_file(img_dat,unique_id);
            announcement.setImg_url(CryptoUtils.encrypt_msg(img_url,optionalUser.get().getCryptoKey()));
            Announcement result=announcementRepository.save(announcement);
            auditService.add_audit_log("管理员发布了公告："+content+" 时间："+announcement.getCreateTime(),username);
            return result;
        }catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
    }
    public List<Announcement>get_all_announcement() throws Exception {
        List<Announcement>announcementList=announcementRepository.findAll();
        List<Announcement>result=new ArrayList<>();
        for (int i = 0; i < announcementList.size(); i++) {
            Announcement announcement=announcementList.get(i);
            User admin=announcement.getAdmin();
            Announcement temp=new Announcement(CryptoUtils.decrypt_msg(announcement.getTitle(),admin.getCryptoKey()),
                    CryptoUtils.decrypt_msg(announcement.getContent(),admin.getCryptoKey()),null);
            temp.setId(announcement.getId());
            temp.setImg_url(CryptoUtils.decrypt_msg(announcement.getImg_url(),admin.getCryptoKey()));
            result.add(temp);
        }
        return result;
    }
    public List<Announcement>get_announcements_by_user(String username) throws Exception {
        Optional<User>optionalUser=userRepository.findByUsername(username);
        if (optionalUser.isEmpty()){
            return List.of(null);
        }else {
            List<Announcement>announcementList=announcementRepository.findAllByAdmin(optionalUser.get());
            List<Announcement>result=new ArrayList<>();
            for (int i = 0; i < announcementList.size(); i++) {
                Announcement announcement=announcementList.get(i);
                User admin=announcement.getAdmin();
                Announcement temp=new Announcement(CryptoUtils.decrypt_msg(announcement.getTitle(),admin.getCryptoKey()),
                        CryptoUtils.decrypt_msg(announcement.getContent(),admin.getCryptoKey()),null);
                temp.setId(announcement.getId());
                temp.setImg_url(CryptoUtils.decrypt_msg(announcement.getImg_url(),admin.getCryptoKey()));
                result.add(temp);
            }
            return result;
        }
    }
    public boolean delete_announce(Long id,String username){
        Optional<User>optionalUser=userRepository.findByUsername(username);
        if (optionalUser.isEmpty()){
            return false;
        }
        Optional<Announcement>optionalAnnouncement=announcementRepository.findByIdAndAdmin(id,optionalUser.get());
        if (optionalAnnouncement.isEmpty()){
            return false;
        }
        try {
            announcementRepository.delete(optionalAnnouncement.get());
            auditService.add_audit_log("管理员删除了公告："+optionalAnnouncement.get().getContent()+" 时间："+ LocalDateTime.now(),username);
            return true;
        }catch (Exception exception){
            exception.printStackTrace();
            return false;
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
}
