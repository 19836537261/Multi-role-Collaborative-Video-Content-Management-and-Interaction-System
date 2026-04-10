package com.service.video_data_service.utils.JWTTools;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.util.Date;
import java.util.HashMap;

@Component
public class JWTConfigurationTool {
    @Resource
    private JWTProperties jwtProperties;
    private long expire_time;
    private KeyPair keyPair=Keys.keyPairFor(SignatureAlgorithm.ES256);
    public String generateToken(String subject, HashMap<String,Object>userMap){
        this.expire_time=System.currentTimeMillis()+ jwtProperties.expire_time;
        String token= Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(this.expire_time))
                .addClaims(userMap)
                .signWith(keyPair.getPrivate())
                .compact();
        return token;
    }
    public long getExpire_time() {
        return expire_time;
    }

    public String getSubject(String token){
        Claims claims=Jwts.parserBuilder().setSigningKey(keyPair.getPublic()).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
    public String getUserRole(String token){
        Claims claims=Jwts.parserBuilder().setSigningKey(keyPair.getPublic()).build().parseClaimsJws(token).getBody();
        return (String) claims.get(jwtProperties.user_role);
    }
    public String getUserKey(String token){
        Claims claims=Jwts.parserBuilder().setSigningKey(keyPair.getPublic()).build().parseClaimsJws(token).getBody();
        return (String) claims.get(jwtProperties.user_key);
    }
    public String getUserName(String token){
        Claims claims=Jwts.parserBuilder().setSigningKey(keyPair.getPublic()).build().parseClaimsJws(token).getBody();
        return (String) claims.get(jwtProperties.user_name);
    }
    public String getUserPwd(String token){
        Claims claims=Jwts.parserBuilder().setSigningKey(keyPair.getPublic()).build().parseClaimsJws(token).getBody();
        return (String) claims.get(jwtProperties.user_pwd);
    }
    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(keyPair.getPublic()).build().parseClaimsJws(token);
            return true;
        }catch (Exception exception){
            exception.printStackTrace();
            return false;
        }
    }
}
