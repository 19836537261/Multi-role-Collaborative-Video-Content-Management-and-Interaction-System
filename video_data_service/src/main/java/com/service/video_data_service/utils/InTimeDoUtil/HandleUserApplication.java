package com.service.video_data_service.utils.InTimeDoUtil;

import com.service.video_data_service.configs.component_config.SpringContextHolder;
import com.service.video_data_service.models.implement.PublisherApplicationRepository;
import com.service.video_data_service.models.implement.UserRepository;
import com.service.video_data_service.models.model.PublisherApplication;
import com.service.video_data_service.models.model.User;
import com.service.video_data_service.models.model.tags.AuditStatus;
import com.service.video_data_service.models.service.PublisherApplicationService;
import com.service.video_data_service.models.service.UserService;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@DisallowConcurrentExecution
public class HandleUserApplication implements Job {
    @Resource
    private PublisherApplicationService publisherApplicationService;
    @Resource
    private UserRepository userRepository;
    @Resource
    private PublisherApplicationRepository publisherApplicationRepository;
    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<PublisherApplication>publisherApplicationList=publisherApplicationService.get_all_applications();
        for (int i = 0; i < publisherApplicationList.size(); i++) {
            PublisherApplication publisherApplication=publisherApplicationList.get(i);
            if (publisherApplication.getStatus()== AuditStatus.APPROVED){
                User user=publisherApplication.getUser();
                user.setRoles(Collections.singletonList("PUBLISHER"));
                publisherApplicationRepository.delete(publisherApplication);
                System.out.println(user.getUsername());
            }else if(publisherApplication.getStatus()==AuditStatus.REJECTED){
                publisherApplicationRepository.delete(publisherApplication);
                System.out.println(publisherApplication.getUser().getUsername());
            }
        }
    }
}
