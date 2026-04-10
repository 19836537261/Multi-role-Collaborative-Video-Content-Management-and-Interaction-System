package com.service.video_data_service.apis;

import com.service.video_data_service.models.service.PublisherApplicationService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping(value = "/api/apply")
public class ApplicationController {
    @Resource
    private PublisherApplicationService publisherApplicationService;
    @PreAuthorize(value = "hasRole('USER')")
    @PostMapping(value = "/add_submit")
    public HashMap<String,Object>add_submit(@RequestParam("name") String name,
                                            @RequestParam("contact") String contact,
                                            @RequestParam("reason") String reason,
                                            @RequestParam("username") String username){
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","add_submit");
        result.put("dat",publisherApplicationService.add_application(name, contact, reason, username));
        return result;
    }
    @PreAuthorize(value = "hasRole('USER')")
    @PostMapping(value = "/see_apply")
    public HashMap<String,Object>see_apply(@RequestParam("username")String username){
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","see_apply");
        result.put("dat",publisherApplicationService.get_application(username));
        return result;
    }
    @PreAuthorize(value = "hasRole('ADMIN')")
    @PostMapping(value = "/audit_all_apply")
    public HashMap<String,Object>audit_all_apply(){
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","audit_all_apply");
        result.put("dat",publisherApplicationService.get_all_applications());
        return result;
    }
    @PreAuthorize(value = "hasRole('ADMIN')")
    @PostMapping(value = "/audit_one_apply")
    public HashMap<String,Object>audit_one_apply(@RequestParam("id")Long id,
                                                 @RequestParam("status")int status,
                                                 @RequestParam("username")String username){
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","audit_one_apply");
        result.put("result",publisherApplicationService.audit_one_apply(id, status, username));
        return result;
    }
}
