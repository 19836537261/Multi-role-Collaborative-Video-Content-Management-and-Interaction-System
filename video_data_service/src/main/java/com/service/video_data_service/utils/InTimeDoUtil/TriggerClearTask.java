package com.service.video_data_service.utils.InTimeDoUtil;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TriggerClearTask {
    @Bean
    public JobDetail runClearTask(){
        return JobBuilder.newJob(HandleUserApplication.class)
                .withIdentity("clear_task","no_classify_group")
                .storeDurably().build();
    }
    @Bean
    public Trigger runClearTaskTrigger(){
        CronScheduleBuilder cronScheduleBuilder=CronScheduleBuilder.cronSchedule( "0 0/5 * * * ?");
        return TriggerBuilder.newTrigger()
                .withIdentity("clear_task_trigger","no_classify_trigger_group")
                .withSchedule(cronScheduleBuilder).forJob(runClearTask())
                .build();
    }
}
