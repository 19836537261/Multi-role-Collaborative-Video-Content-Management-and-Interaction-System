package com.service.video_data_service.models.service;

import com.service.video_data_service.models.implement.PublisherApplicationRepository;
import com.service.video_data_service.models.implement.UserRepository;
import com.service.video_data_service.models.model.PublisherApplication;
import com.service.video_data_service.models.model.User;
import com.service.video_data_service.models.model.tags.AuditStatus;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PublisherApplicationService {
    @Resource
    private PublisherApplicationRepository publisherApplicationRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private AuditService auditService;
    public PublisherApplication add_application(String name,String contact,String reason,String username){
        Optional<User>optionalUser=userRepository.findByUsername(username);
        if (optionalUser.isEmpty()){
            return null;
        }
        Optional<PublisherApplication>applicationOptional=publisherApplicationRepository.findByUser(optionalUser.get());
        if (applicationOptional.isEmpty()){
            PublisherApplication publisherApplication=new PublisherApplication(name,contact,reason,optionalUser.get());
            publisherApplication.setStatus(AuditStatus.PENDING);
            publisherApplication.setResponseText("正在审核中，预计1到3个工作日审核完成。");
            publisherApplication.setAdmin(null);
            PublisherApplication result=publisherApplicationRepository.save(publisherApplication);
            return result;
        }else {
            return null;
        }
    }
    public PublisherApplication get_application(String username){
        Optional<User>optionalUser=userRepository.findByUsername(username);
        if (optionalUser.isEmpty()){
            return null;
        }
        Optional<PublisherApplication>publisherApplicationOptional=publisherApplicationRepository.findByUser(optionalUser.get());
        if (publisherApplicationOptional.isEmpty()){
            return null;
        }
        PublisherApplication publisherApplication=publisherApplicationOptional.get();
        publisherApplication.setAdmin(null);
        publisherApplication.setUser(null);
        return publisherApplication;
    }
    public List<PublisherApplication>get_all_applications(){
        List<PublisherApplication>publisherApplicationList=publisherApplicationRepository.findAll();
        return publisherApplicationList;
    }

    /**
     *
     * @param id
     * @param status status等于1代表审核通过，等于0代表审核不通过
     * @param username
     * @return
     */
    public boolean audit_one_apply(Long id,int status,String username){
        Optional<PublisherApplication>applicationOptional=publisherApplicationRepository.findById(id);
        if (applicationOptional.isEmpty()){
            return false;
        }
        Optional<User>optionalUser=userRepository.findByUsername(username);
        if (optionalUser.isEmpty()){
            return false;
        }
        PublisherApplication publisherApplication=applicationOptional.get();
        publisherApplication.setProcessTime(LocalDateTime.now());
        if (status==1){
            publisherApplication.setStatus(AuditStatus.APPROVED);
            publisherApplication.setAdmin(optionalUser.get());
            publisherApplication.setResponseText("审核通过，您已经成为视频发布者。欢迎您的加入。");
            publisherApplicationRepository.save(publisherApplication);
            auditService.add_audit_log("管理员:"+username+" 通过了用户:"+publisherApplication.getReal_name()+" 的申请.",username);
        } else if (status==0) {
            publisherApplication.setStatus(AuditStatus.REJECTED);
            publisherApplication.setAdmin(optionalUser.get());
            publisherApplication.setResponseText("非常抱歉，您递交的请求审核失败。");
            publisherApplicationRepository.save(publisherApplication);
            auditService.add_audit_log("管理员:"+username+" 拒绝了用户:"+publisherApplication.getReal_name()+" 的申请.",username);
        }
        return true;
    }
}
