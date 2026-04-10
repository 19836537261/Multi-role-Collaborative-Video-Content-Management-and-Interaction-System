package com.service.video_data_service.configs.nginx_config;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NginxStartConfiguration {
    @Bean
    public JobDetail runNginxServer(){
        return JobBuilder.newJob(StartNginxServerJob.class)
                .withIdentity("run_nginx_job_identity","run_server_group1")
                .storeDurably()
                .build();
    }
    @Bean
    public Trigger runNginxServerTrigger(){
        SimpleScheduleBuilder simpleScheduleBuilder=SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(3)
                .withRepeatCount(0);
        return TriggerBuilder.newTrigger()
                .withIdentity("run_nginx_trigger_identity","run_server_group1")
                .withSchedule(simpleScheduleBuilder)
                .forJob(runNginxServer())
                .build();
    }
}
