package com.service.video_data_service.apis;

import com.service.video_data_service.models.model.User;
import com.service.video_data_service.models.service.AuditService;
import com.service.video_data_service.models.service.UserService;
import com.service.video_data_service.utils.EncryptTools.CryptoUtils;
import com.service.video_data_service.utils.JWTTools.JWTConfigurationTool;
import com.service.video_data_service.utils.JWTTools.JWTProperties;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/user")
public class UserApiController {
    @Resource
    private UserService userService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private JWTConfigurationTool jwtConfigurationTool;
    @Resource
    private JWTProperties jwtProperties;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private AuditService auditService;
    @PostMapping(value = "/user_login")
    public HashMap<String,Object>userLogin(@RequestParam("username")String username,
                                            @RequestParam("password")String password){
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","user_login");
        try {
            Authentication authentication= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
            List<String> roles=authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            UserDetails userDetails=userService.loadUserByUsername(username);
            String uname=userDetails.getUsername();
            String upwd=userDetails.getPassword();
            Optional<User>optionalUser=userService.get_one_user(uname,upwd);
            if (optionalUser.isEmpty())
            {
                result.put("result",false);
                return result;
            }
            HashMap<String,Object>user_data=new HashMap<>();
            user_data.put("roles",roles);
            user_data.put(jwtProperties.user_key,optionalUser.get().getCryptoKey());
            user_data.put(jwtProperties.user_name,optionalUser.get().getUsername());
            user_data.put(jwtProperties.user_pwd,optionalUser.get().getPassword());
            user_data.put(jwtProperties.user_role,optionalUser.get().getRoles());
            String token=jwtConfigurationTool.generateToken(username,user_data);
            HashMap<String,Object>additional_dat=new HashMap<>();
            additional_dat.put("token",token);
            additional_dat.put("expire_time",jwtConfigurationTool.getExpire_time());
            additional_dat.put("key",optionalUser.get().getCryptoKey());
            additional_dat.put("role",optionalUser.get().getRoles());
            additional_dat.put("username",optionalUser.get().getUsername());
            result.put("result",true);
            result.put("dat",additional_dat);
            return result;
        }catch (Exception exception){
            result.put("result",false);
            return result;
        }
    }
    @PostMapping(value = "/add_user")
    public HashMap<String,Object>addUser(@RequestParam("username")String username,
                                         @RequestParam("password")String password,
                                         @RequestParam("email")String email,
                                         @RequestParam("phone")String phone){
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","add_user");
        try {
            Optional<User>optionalUser=userService.get_one_user(username);
            if (!optionalUser.isEmpty()){
                result.put("result",false);
                return result;
            }
            userService.add_user(username, password, email, phone,passwordEncoder);
            result.put("result",true);
            return result;
        }catch (Exception exception){
            exception.printStackTrace();
            result.put("result",false);
            return result;
        }
    }
    @GetMapping(value = "/get_one_user")
    public HashMap<String,Object>get_one_user(@RequestParam("username")String username) throws Exception {
        HashMap<String,Object>result=new HashMap<>();
        result.put("transection","get_one_user");
        Optional<User> optionalUser=userService.get_one_user(username);
        if (optionalUser.isEmpty())
        {
            result.put("result",false);
            return result;
        }else {
            result.put("result",true);
            HashMap<String,Object>dat=new HashMap<>();
            dat.put("username",optionalUser.get().getUsername());
            dat.put("role",optionalUser.get().getRoles());
            dat.put("email", CryptoUtils.decrypt_msg(optionalUser.get().getEmail(),optionalUser.get().getCryptoKey()));
            dat.put("phone",optionalUser.get().getPhone());
            dat.put("create_time",optionalUser.get().getCreateTime());
            result.put("dat",dat);
            return result;
        }
    }
    @PreAuthorize(value = "hasRole('ADMIN')")
    @PostMapping(value = "/get_audit_by_admin")
    public HashMap<String,Object>get_audit_by_admin(@RequestParam("username")String username) throws Exception {
        HashMap<String,Object>result=new HashMap<>();
        result.put("transection","get_audit_by_admin");
        result.put("dat",auditService.get_audit_logs_by_manager(username));
        return result;
    }
}
