package com.service.video_data_service.utils.JWTTools;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JWTProperties {
    @Value(value = "${jwt.expire_time}")
    public Long expire_time;
    @Value(value = "${jwt.token_prefix}")
    public String token_prefix;
    @Value(value = "${jwt.user_role_key}")
    public String user_role;
    @Value(value = "${jwt.user_name}")
    public String user_name;
    @Value(value = "${jwt.user_pwd}")
    public String user_pwd;
    @Value(value = "${jwt.user_key}")
    public String user_key;
}
