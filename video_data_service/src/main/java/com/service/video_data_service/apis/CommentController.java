package com.service.video_data_service.apis;

import com.service.video_data_service.models.model.Comment;
import com.service.video_data_service.models.service.CommentService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/comment")
public class CommentController {
    @Resource
    private CommentService commentService;
    @PostMapping(value = "/get_all_comments_by_video_id/{id}")
    public HashMap<String,Object>get_all_comments_by_video_id(@PathVariable("id")Long id,@RequestParam("username")String username){
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","get_all_comments_by_video_id");
        List<CommentService.AdditionalComment> commentList=commentService.get_all_video_comments(id,username);
        result.put("dat",commentList);
        return result;
    }
    @PreAuthorize(value = "hasRole('ADMIN')")
    @PostMapping(value = "/delete_comment")
    public HashMap<String,Object>delete_comment(@RequestParam("id")Long id,
                                                @RequestParam("username")String username){
        HashMap<String,Object>result=new HashMap<>();
        result.put("transaction","delete_comment");
        result.put("result",commentService.delete_one_comment_by_manager(id, username));
        return result;
    }
}
