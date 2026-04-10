package com.service.video_data_service.configs.redis_config;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisStartConfiguration {
    @Bean
    public JobDetail runServerJob(){
        return JobBuilder.newJob(StartRedisServerJob.class)
                .withIdentity("run_server_job_identity","run_server_group")
                .storeDurably()
                .build();
    }
    @Bean
    public Trigger runServerTrigger(){
        SimpleScheduleBuilder simpleScheduleBuilder=SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(3)
                .withRepeatCount(0);
        return TriggerBuilder.newTrigger()
                .withIdentity("run_server_trigger_identity","run_server_group")
                .withSchedule(simpleScheduleBuilder)
                .forJob(runServerJob())
                .build();
    }
}
