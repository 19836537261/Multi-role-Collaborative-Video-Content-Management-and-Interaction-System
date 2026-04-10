package com.service.video_data_service.configs.filter_config;

import com.service.video_data_service.models.service.UserService;
import com.service.video_data_service.utils.JWTTools.JWTConfigurationTool;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JWTConfigurationTool jwtConfigurationTool;
    private final UserService userService;
    private List<String>filter_entrance_sheet=List.of(
            "api/user/add_user",
            "api/user/user_login",
            "/favicon.ico",
            "/files/imgs/",
            "/files/videos/",
            "/api/category/get_all_categories",
            "/api/video/get_videos_by_type",
            "/api/video/get_all_video_kinds",
            "/message/video/",
            "/api/announcement/list_all_announce",
            "/api/video/get_all_videos"
    );
    public JwtAuthenticationFilter(JWTConfigurationTool jwtConfigurationTool, UserService userService){

        this.jwtConfigurationTool = jwtConfigurationTool;
        this.userService = userService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String url_request=request.getRequestURI();
            System.out.println("请求地址:"+url_request);
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Allow-Credentials","true");
            boolean is_entrance=false;
            for (String item:filter_entrance_sheet) {
                if (url_request.endsWith(item)||url_request.contains(item))
                {
                    is_entrance=true;
                    break;
                }
            }
            if (is_entrance)
            {
                System.out.println("已放行:"+request.getRequestURL());
                filterChain.doFilter(request,response);
            }else
            {
                String jwt=getTokenFromRequest(request);
                if (jwtConfigurationTool.validateToken(jwt)){
                    String username=jwtConfigurationTool.getUserName(jwt);
                    UserDetails userDetails=userService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    System.out.println(username);
                    filterChain.doFilter(request,response);
                }else
                {
                    System.out.println("token验证失败或已过期！");
                }
            }
        }catch (Exception exception)
        {
            exception.printStackTrace();
            filterChain.doFilter(request,response);
        }
    }
    private String getTokenFromRequest(HttpServletRequest request){
        String token=request.getHeader("Auth");
        System.out.println(token);
        return token;
    }
}
