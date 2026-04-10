package com.service.video_data_service.configs.ConstConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PathHelper {
    @Value(value = "${config.video_url_path_root}")
    public String video_url_path_root;
    @Value(value = "${config.image_url_path_root}")
    public String image_url_path_root;
    @Value(value = "${config.video_absolute_path_root}")
    public String video_absolute_path_root;
    @Value(value = "${config.image_absolute_path_root}")
    public String image_absolute_path_root;
}
