package com.service.video_data_service.models.service;

import com.service.video_data_service.models.implement.AuditLogRepository;
import com.service.video_data_service.models.implement.UserRepository;
import com.service.video_data_service.models.model.AuditLog;
import com.service.video_data_service.models.model.User;
import com.service.video_data_service.utils.EncryptTools.CryptoUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuditService {
    @Resource
    private UserRepository userRepository;
    @Resource
    private AuditLogRepository auditLogRepository;
    public AuditLog add_audit_log(String content,String username){
        Optional<User>optionalUser=userRepository.findByUsername(username);
        if (optionalUser.isEmpty()){
            return null;
        }else {
            try {
                AuditLog auditLog=new AuditLog(CryptoUtils.encrypt_msg(content,optionalUser.get().getCryptoKey()),optionalUser.get());
                AuditLog result=auditLogRepository.save(auditLog);
                return result;
            }catch (Exception exception){
                exception.printStackTrace();
                return null;
            }
        }
    }
    public List<AuditLog>get_audit_logs_by_manager(String username) throws Exception {
        Optional<User>optionalUser=userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
        {
            return List.of();
        }
        List<AuditLog>auditLogList=auditLogRepository.findByAdminId(optionalUser.get().getId());
        for (int i = 0; i < auditLogList.size(); i++) {
            auditLogList.get(i).setContent(CryptoUtils.decrypt_msg(auditLogList.get(i).getContent(),auditLogList.get(i).getAdmin().getCryptoKey()));
        }
        return auditLogList;
    }
}
