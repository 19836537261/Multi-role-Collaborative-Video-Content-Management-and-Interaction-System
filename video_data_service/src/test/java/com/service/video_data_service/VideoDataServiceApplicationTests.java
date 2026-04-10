package com.service.video_data_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootTest
class VideoDataServiceApplicationTests {

    @Test
    void contextLoads() {}
//    未启用Spring容器实例
    public static void main(String[]args) throws IOException, InterruptedException{
        Process process=Runtime.getRuntime().exec("src/main/resources/static/configs/clear_server_task.bat");
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
        int waitExit=process.waitFor();
        System.out.println("程序返回值:"+waitExit);
    }

}
