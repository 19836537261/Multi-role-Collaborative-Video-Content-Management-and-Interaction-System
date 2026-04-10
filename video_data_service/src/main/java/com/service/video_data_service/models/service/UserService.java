package com.service.video_data_service.models.service;

import com.service.video_data_service.models.implement.UserRepository;
import com.service.video_data_service.models.model.User;
import com.service.video_data_service.utils.EncryptTools.CryptoUtils;
import jakarta.annotation.Resource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Resource
    private UserRepository userRepository;
    public User add_user(String username, String password, String email, String phone, PasswordEncoder passwordEncoder) throws Exception {
        User user=new User(username,passwordEncoder.encode(password),email,phone);
        String key= CryptoUtils.GenerateKey();
        user.setCryptoKey(key);
        user.setRoles(Collections.singletonList("USER"));
        user.setEnable(true);
        user.setEmail(CryptoUtils.encrypt_msg(email,key));
        return userRepository.save(user);
    }
    public boolean is_valid_user(String username,String password){
//        Optional<User>optionalUser=userRepository.findByUsernameAndPassword(username,password);
//        if (optionalUser.isEmpty())
//        {
//            return false;
//        }else {
//            return true;
//        }
        return false;
    }

    public UserDetails loadUserByUsername(String username) {
        User user=userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("未找到用户名:"+username));
        List<GrantedAuthority> authorities=user.getRoles().stream()
                .map(role->new SimpleGrantedAuthority("ROLE_"+role)).collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),user.isEnabled(),true,true,true,authorities);
    }
    public Optional<User>get_one_user(String username,String password){
        return userRepository.findByUsernameAndPassword(username,password);
    }
    public Optional<User>get_one_user(String username){
        return userRepository.searchOneUser(username);
    }
    public Optional<User>get_one_user(Long id){
        return userRepository.findById(id);
    }
}
