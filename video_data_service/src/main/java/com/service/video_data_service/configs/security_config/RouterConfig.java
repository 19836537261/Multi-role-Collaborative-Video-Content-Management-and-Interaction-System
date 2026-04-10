package com.service.video_data_service.configs.security_config;

import com.service.video_data_service.configs.filter_config.JwtAuthenticationFilter;
import com.service.video_data_service.models.service.UserService;
import com.service.video_data_service.utils.JWTTools.JWTConfigurationTool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class RouterConfig {
    private final JWTConfigurationTool jwtConfigurationTool;
    private final UserService userService;

    public RouterConfig(JWTConfigurationTool jwtConfigurationTool, UserService userService) {
        this.jwtConfigurationTool = jwtConfigurationTool;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain doFilter(HttpSecurity httpSecurity){
        return httpSecurity
                .sessionManagement(session->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/api/user/**",
                                "/api/video/**",
                                "/files/imgs/**",
                                "/message/video/**",
                                "/files/videos/**",
                                "/api/category/**").permitAll()
                        .anyRequest().authenticated())
                .csrf(c->c.disable())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter(jwtConfigurationTool,userService);
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
    {
        return configuration.getAuthenticationManager();
    }
}
