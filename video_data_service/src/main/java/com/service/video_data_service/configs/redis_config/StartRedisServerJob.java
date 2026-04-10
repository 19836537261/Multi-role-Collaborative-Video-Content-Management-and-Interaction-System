package com.service.video_data_service.configs.redis_config;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class StartRedisServerJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            Process process=Runtime.getRuntime().exec("src/main/resources/static/configs/start_redis.bat");
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            int waitExit=process.waitFor();
            if (waitExit==0){
                System.out.println("redis服务已关闭");
            }
            bufferedReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
