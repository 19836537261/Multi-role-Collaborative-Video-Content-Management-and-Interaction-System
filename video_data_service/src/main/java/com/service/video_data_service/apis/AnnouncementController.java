package com.service.video_data_service.apis;

import com.service.video_data_service.models.model.Announcement;
import com.service.video_data_service.models.model.AuditLog;
import com.service.video_data_service.models.service.AnnouncementService;
import com.service.video_data_service.models.service.AuditService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping(value = "/api/announcement")
public class AnnouncementController {
    @Resource
    private AnnouncementService announcementService;
    @PostMapping(value = "/add_announce")
    public HashMap<String,Object>add_announcement(@RequestParam("title") String title,
                                                  @RequestParam("content") String content,
                                                  @RequestParam("img_dat")String img_dat,
                                                  @RequestParam("token") String token){
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","add_announce");
        Announcement announcement=announcementService.add_announce(title, content,img_dat, token);
        result.put("result",announcement==null?false:true);
        return result;
    }
    @GetMapping(value = "/list_all_announce")
    public HashMap<String,Object>get_all_announce() throws Exception {
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","list_all_announce");
        result.put("dat",announcementService.get_all_announcement());
        return result;
    }
    @PostMapping(value = "/get_announce_by_admin")
    public HashMap<String,Object>get_announce_by_admin(@RequestParam("username")String username) throws Exception {
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","get_announce_by_admin");
        result.put("dat",announcementService.get_announcements_by_user(username));
        return result;
    }
    @PostMapping(value = "/delete_one_announce")
    public HashMap<String,Object>delete_one_announce(@RequestParam("id") Long id,
                                                     @RequestParam("username") String username){
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","delete_one_announce");
        result.put("result",announcementService.delete_announce(id, username));
        return result;
    }
}
