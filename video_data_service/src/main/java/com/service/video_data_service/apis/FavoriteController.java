package com.service.video_data_service.apis;

import com.service.video_data_service.models.model.Favorite;
import com.service.video_data_service.models.service.FavoriteService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/favorite")
public class FavoriteController {
    @Resource
    private FavoriteService favoriteService;
    @PostMapping(value = "/is_like")
    public HashMap<String,Object>is_like(@RequestParam("username")String username,
                                         @RequestParam("video_id")Long v_id){
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","is_like");
        Favorite favorite=favoriteService.get_one_record(username, v_id);
        if (favorite!=null){
            result.put("result",true);
        }else {
            result.put("result",false);
        }
        return result;
    }
    @PostMapping(value = "/get_favorite_videos")
    public HashMap<String,Object>get_favorite_videos(@RequestParam("username")String username) throws Exception {
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","get_favorite_videos");
        result.put("dat",favoriteService.get_favorite_videos(username));
        return result;
    }
}
