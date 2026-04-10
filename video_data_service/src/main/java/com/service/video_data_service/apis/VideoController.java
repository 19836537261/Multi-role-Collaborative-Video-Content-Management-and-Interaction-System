package com.service.video_data_service.apis;

import com.service.video_data_service.models.implement.VideoTypeRepository;
import com.service.video_data_service.models.model.User;
import com.service.video_data_service.models.model.Video;
import com.service.video_data_service.models.model.VideoType;
import com.service.video_data_service.models.service.UserService;
import com.service.video_data_service.models.service.VideoService;
import com.service.video_data_service.models.service.VideoTypeService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/video")
public class VideoController {
    @Resource
    private VideoService videoService;
    @Resource
    private UserService userService;
    @Resource
    private VideoTypeService videoTypeService;
    @PostMapping(value = "/publish_video")
    @PreAuthorize(value = "hasRole('PUBLISHER')")
    public HashMap<String,Object>publish_video(@RequestParam("title")String title,
                                               @RequestParam("description")String description,
                                               @RequestParam("img_dat")String img_dat,
                                               @RequestParam("video_dat")String video_dat,
                                               @RequestParam("video_type")String video_type,
                                               @RequestParam("username")String username){
        HashMap<String,Object>result=new HashMap<>();
        result.put("transection","publish_video");
        try {
            videoService.add_new_video(title, description, img_dat, video_dat,video_type, username);
            result.put("result",true);
        }catch (Exception exception){
            exception.printStackTrace();
            result.put("result",false);
        }
        return result;
    }
    @GetMapping(value = "/get_videos_by_user")
    public HashMap<String,Object>get_videos(@RequestParam("username")String username) throws Exception {
        HashMap<String,Object>result=new HashMap<>();
        result.put("transection","get_videos_by_user");
        Optional<User> optionalUser=userService.get_one_user(username);
        if (optionalUser.isEmpty()){
            result.put("result",false);
            return result;
        }else {
            List<Video>videos=videoService.get_videos_by_user(optionalUser.get());
            result.put("dat",videos);
            result.put("size",videos.size());
            result.put("result",true);
            return result;
        }
    }
    @PostMapping(value = "/update_video")
    public HashMap<String,Object>update_video(@RequestParam("title")String title,
                                              @RequestParam("desc")String desc,
                                              @RequestParam("img_dat")String img_dat,
                                              @RequestParam("video_dat")String video_dat,
                                              @RequestParam("id")Long id,
                                              @RequestParam("video_type")String video_type,
                                              @RequestParam("username")String username) throws Exception {
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","update_video");
        Optional<User>optionalUser=userService.get_one_user(username);
        if (optionalUser.isEmpty()){
            result.put("result",false);
            return result;
        }else {
            if (img_dat==""&&video_dat=="")
            {
                result.put("result",videoService.change_one_video(title, desc, null, null, id,optionalUser.get(),video_type));
            } else if (img_dat!=""&&video_dat=="") {
                result.put("result",videoService.change_one_video(title, desc, img_dat, null, id,optionalUser.get(),video_type));
            } else if (video_dat!=""&&img_dat=="") {
                result.put("result",videoService.change_one_video(title, desc, null, video_dat, id,optionalUser.get(),video_type));
            } else if (img_dat!=""&&video_dat!="") {
                result.put("result",videoService.change_one_video(title, desc, img_dat, video_dat, id,optionalUser.get(),video_type));
            }
            return result;
        }
    }
    @GetMapping(value = "/get_all_video_kinds")
    public HashMap<String,Object>get_all_kinds(){
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","get_all_video_kinds");
        result.put("dat",videoService.get_all_type());
        return result;
    }
    @PreAuthorize(value = "hasRole('ADMIN')")
    @PostMapping(value = "/add_new_type")
    public HashMap<String,Object>add_new_type(@RequestParam("type")String type,
                                              @RequestParam("username")String username){
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","add_new_type");
        result.put("result",videoTypeService.add_new_item(type, username));
        return result;
    }
    @PreAuthorize(value = "hasRole('ADMIN')")
    @PostMapping(value = "/delete_type")
    public HashMap<String,Object>delete_type(@RequestParam("type")String type,
                                              @RequestParam("username")String username){
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","delete_type");
        result.put("result",videoTypeService.delete_item(type, username));
        return result;
    }
    @PostMapping(value = "/get_videos_by_type")
    public HashMap<String,Object>get_videos_by_type(@RequestParam(value = "category")String category,
                                                    @RequestParam(value = "page")int page,
                                                    @RequestParam(value = "sort")String sort) throws Exception {
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","get_videos_by_type");
        result.put("dat",videoService.get_video_by_kind_and_index(category, page,sort));
        return result;
    }
    @PreAuthorize(value = "hasRole('PUBLISHER')")
    @PostMapping(value = "/delete_video")
    public HashMap<String,Object>delete_one_video(@RequestParam(value = "id")Long id){
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","delete_video");
        result.put("result",videoService.delete_video_by_id(id));
        return result;
    }
    @PreAuthorize(value = "hasRole('ADMIN')")
    @GetMapping(value = "/audit_all_videos")
    public HashMap<String,Object>audit_all_videos() throws Exception {
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","audit_all_videos");
        result.put("dat",videoService.get_all_videos());
        return result;
    }
    @PreAuthorize(value = "hasRole('ADMIN')")
    @PostMapping(value = "/audit_one_video")
    public HashMap<String,Object>audit_one_video(@RequestParam("status")int status,
                                                 @RequestParam("id")Long id,
                                                 @RequestParam("username")String username) throws Exception {
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","audit_one_video");
        result.put("result",videoService.update_video_by_status(status, id,username));
        return result;
    }
    @GetMapping(value = "/get_all_videos")
    public HashMap<String,Object>get_all_videos() throws Exception {
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","get_all_videos");
        result.put("dat",videoService.get_all_videos_by_status());
        return result;
    }
}
