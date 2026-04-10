package com.service.video_data_service.models.service;

import com.service.video_data_service.models.implement.CommentRepository;
import com.service.video_data_service.models.model.Comment;
import com.service.video_data_service.models.model.User;
import com.service.video_data_service.models.model.Video;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Resource
    private CommentRepository commentRepository;
    @Resource
    private UserService userService;
    @Resource
    private VideoService videoService;
    @Resource
    private AuditService auditService;
    public AdditionalComment add_comment(User user, Long video_id, String message,String username){
        Optional<Video>optionalVideo=videoService.get_video_by_id(video_id);
        if (optionalVideo.isEmpty())
        {
            return null;
        }
        AdditionalComment additionalComment=new AdditionalComment();
        Comment comment=new Comment(message,user,optionalVideo.get());
        Comment result=commentRepository.save(comment);
        User new_user=new User();
        new_user.setUsername(result.getUser().getUsername());
        additionalComment.is_self_user=true;
        result.setUser(new_user);
        additionalComment.setUser(result.getUser());
        additionalComment.setVideo(result.getVideo());
        additionalComment.setCreateTime(result.getCreateTime());
        additionalComment.setId(result.getId());
        additionalComment.setContent(result.getContent());
        return additionalComment;
    }
    public List<AdditionalComment>get_all_video_comments(Long video_id,String username){
        List<Comment>commentList=commentRepository.findByVideo(video_id);
        List<AdditionalComment>additionalComments=new ArrayList<>();
        for (int i = 0; i < commentList.size(); i++) {
            Comment comment=commentList.get(i);
            AdditionalComment additionalComment=new AdditionalComment();
            additionalComment.is_self_user=(username.compareTo(comment.getUser().getUsername())==0)?true:false;
            User temp_user=new User();
            temp_user.setUsername(comment.getUser().getUsername());
            comment.setUser(temp_user);
            additionalComment.setUser(comment.getUser());
            additionalComment.setVideo(comment.getVideo());
            additionalComment.setCreateTime(comment.getCreateTime());
            additionalComment.setId(comment.getId());
            additionalComment.setContent(comment.getContent());
            additionalComments.add(additionalComment);
        }
        return additionalComments;
    }
    public boolean delete_one_comment_by_manager(Long comment_id,String username){
        Optional<Comment>optionalComment=commentRepository.findById(comment_id);
        if (optionalComment.isEmpty()){
            return false;
        }
        Optional<User>optionalUser=userService.get_one_user(username);
        if (optionalUser.isEmpty())
        {
            return false;
        }
        commentRepository.delete(optionalComment.get());
        auditService.add_audit_log("管理员："+username+" 删除了用户:"+optionalComment.get().getUser().getUsername()+" 的评论。评论内容为:"+optionalComment.get().getContent(),username);
        return true;
    }
    public class AdditionalComment extends Comment{
        public boolean is_self_user;
    }
    public boolean delete_one_comment(Long id){
        Optional<Comment>optionalComment=commentRepository.findById(id);
        if (optionalComment.isEmpty()){
            return false;
        }
        else {
            commentRepository.delete(optionalComment.get());
            return true;
        }
    }
}
