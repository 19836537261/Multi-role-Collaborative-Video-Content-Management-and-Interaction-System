package com.service.video_data_service.models.service;

import com.service.video_data_service.models.implement.UserRepository;
import com.service.video_data_service.models.implement.VideoTypeRepository;
import com.service.video_data_service.models.model.User;
import com.service.video_data_service.models.model.VideoType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VideoTypeService {
    @Resource
    private VideoTypeRepository videoTypeRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private AuditService auditService;
    public boolean add_new_item(String kind,String username){
        Optional<User>optionalUser=userRepository.findByUsername(username);
        if (optionalUser.isEmpty()){
            return false;
        }
        VideoType videoType=new VideoType();
        videoType.type=kind;
        videoTypeRepository.save(videoType);
        auditService.add_audit_log("管理员:"+username+" 添加了类目:"+kind,username);
        return true;
    }
    public boolean delete_item(String kind,String username){
        Optional<User>optionalUser=userRepository.findByUsername(username);
        if (optionalUser.isEmpty()){
            return false;
        }
        Optional<VideoType>optionalVideoType=videoTypeRepository.findByType(kind);
        if (optionalVideoType.isEmpty()){
            return false;
        }
        videoTypeRepository.delete(optionalVideoType.get());
        auditService.add_audit_log("管理员:"+username+" 删除了类目:"+kind,username);
        return true;
    }
}
