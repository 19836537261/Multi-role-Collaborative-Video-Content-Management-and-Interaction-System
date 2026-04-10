package com.service.video_data_service.configs.global_config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.servlet.MultipartConfigFactory;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.util.unit.DataSize;

@Configuration
public class MultipartConfig {
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(2048));
        factory.setMaxRequestSize(DataSize.ofMegabytes(2048));
        return factory.createMultipartConfig();
    }
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory>tomcatCustomer(){
        return factory -> factory.addConnectorCustomizers(connector -> {
            connector.setMaxPostSize(-1);
        });
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring()
                .requestMatchers("/files/**",
                        "/configs/**",
                        "/javascripts/**",
                        "/redis_server/**",
                        "/stylesheets/**",
                        "/favicon.ico",
                        "/api/category/get_all_categories",
                        "/api/video/get_all_video_kinds",
                        "/api/video/get_videos_by_type",
                        "/error",
                        "/api/announcement/list_all_announce",
                        "/api/video/get_all_videos");
    }
}
