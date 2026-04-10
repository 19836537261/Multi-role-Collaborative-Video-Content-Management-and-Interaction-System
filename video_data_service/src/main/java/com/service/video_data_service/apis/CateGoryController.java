package com.service.video_data_service.apis;

import com.service.video_data_service.models.model.Category;
import com.service.video_data_service.models.model.User;
import com.service.video_data_service.models.model.Video;
import com.service.video_data_service.models.service.CategoryService;
import com.service.video_data_service.models.service.UserService;
import com.service.video_data_service.utils.EncryptTools.CryptoUtils;
import io.lettuce.core.VAddArgs;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "api/category/")
public class CateGoryController {
    @Resource
    private UserService userService;
    @Resource
    private CategoryService categoryService;
    @PostMapping(value = "/add_category")
    public HashMap<String,Object>add_category(@RequestParam("name")String name,
                                              @RequestParam("desc")String desc,
                                              @RequestParam("img_dat")String img_dat,
                                              @RequestParam("username")String username) throws Exception {
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","add_category");
        Optional<User>optionalUser=userService.get_one_user(username);
        if (optionalUser.isEmpty()||img_dat=="")
        {
            result.put("result",false);
            return result;
        }else
        {
            Category category_result=categoryService.add_new_category(name,desc,img_dat,optionalUser.get());
            result.put("result",true);
            result.put("dat",category_result);
            return result;
        }
    }
    @GetMapping(value = "/get_category_by_user")
    public HashMap<String,Object>get_category_by_user(@RequestParam("username")String username) throws Exception {
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","get_category_by_user");
        Optional<User>optionalUser=userService.get_one_user(username);
        if (optionalUser.isEmpty()){
            result.put("result",false);
            return result;
        }
        else {
            List<Category>categoryList=categoryService.get_category_by_user(optionalUser.get());
            result.put("result",true);
            result.put("dat",categoryList);
            return result;
        }
    }
    @PostMapping(value = "/attach_video")
    public HashMap<String,Object>attach_video(@RequestParam("video_id")Long video_id,
                                              @RequestParam("category_id")Long category_id){
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","attach_video");
        result.put("result",categoryService.attach_video(category_id,video_id));
        return result;
    }
    @PostMapping(value = "/detach_video")
    public HashMap<String,Object>detach_video(@RequestParam("video_id")Long video_id,
                                              @RequestParam("category_id")Long category_id){
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","detach_video");
        result.put("result",categoryService.detach_videos(category_id,video_id));
        return result;
    }
    @GetMapping(value = "/get_category_videos")
    public HashMap<String,Object>get_category_videos(@RequestParam("category_id")Long category_id) throws Exception {
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","get_category_videos");
        List<Video>videoList=categoryService.get_all_category_videos(category_id);
        result.put("dat",videoList);
        return result;
    }
    @GetMapping(value = "/get_pre_classify_videos")
    public HashMap<String,Object>get_pre_classify_videos(@RequestParam("username") String username,
                                                         @RequestParam("category_id") Long category_id) throws Exception {
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","get_pre_classify_videos");
        List<Video>videoList=categoryService.get_no_category_videos(username, category_id);
        result.put("dat",videoList);
        return result;
    }
    @PostMapping(value = "/update_category")
    public HashMap<String,Object>update_category(@RequestParam("id")Long category_id,
                                                 @RequestParam("desc")String description,
                                                 @RequestParam("name")String name,
                                                 @RequestParam("img_dat")String img_dat) throws Exception {
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","update_category");
        result.put("result",categoryService.update_category_msg(category_id, description, name, img_dat));
        return result;
    }
    @GetMapping(value = "/get_all_categories")
    public HashMap<String,Object>get_all_categories() throws Exception {
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","get_all_categories");
        result.put("dat",categoryService.get_all_user_categories());
        return result;
    }
    @PreAuthorize(value = "hasRole('ADMIN')")
    @GetMapping(value = "/audit_all_categories")
    public HashMap<String,Object>audit_all_categories() throws Exception {
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","audit_all_categories");
        result.put("dat",categoryService.get_all_categories_by_manager());
        return result;
    }
    @PreAuthorize(value = "hasRole('ADMIN')")
    @PostMapping(value = "/audit_one_category")
    public HashMap<String,Object>audit_one_category(@RequestParam("id")Long id,
                                                    @RequestParam("status")int status,
                                                    @RequestParam("username")String username) throws Exception {
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","audit_one_category");
        result.put("result",categoryService.audit_one_category(id, status, username));
        return result;
    }

}
