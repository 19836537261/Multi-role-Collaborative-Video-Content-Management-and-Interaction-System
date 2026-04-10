package com.service.video_data_service.utils.MessageTools;

import com.service.video_data_service.configs.component_config.SpringContextHolder;
import com.service.video_data_service.models.model.Comment;
import com.service.video_data_service.models.model.User;
import com.service.video_data_service.models.service.CommentService;
import com.service.video_data_service.models.service.FavoriteService;
import com.service.video_data_service.models.service.UserService;
import com.service.video_data_service.utils.JWTTools.JWTConfigurationTool;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.Resource;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/message/video/{video_id}")
@Component
public class RealTimeVideoHandler {
    //private static final Map<String, Session> clients = new ConcurrentHashMap<>();
    private CommentService commentService;
    private UserService userService;
    private JWTConfigurationTool jwtConfigurationTool;
    private FavoriteService favoriteService;
    private static final Map<Long, Set<Session>> clients = new ConcurrentHashMap<>();
    @OnOpen
    public void onOpen(Session session, @PathParam("video_id") Long video_id) {

        commentService= SpringContextHolder.getBean(CommentService.class);
        userService=SpringContextHolder.getBean(UserService.class);
        jwtConfigurationTool=SpringContextHolder.getBean(JWTConfigurationTool.class);
        favoriteService=SpringContextHolder.getBean(FavoriteService.class);
        Set<Session>sessions=clients.computeIfAbsent(video_id,t->new CopyOnWriteArraySet<>());
        sessions.add(session);
    }
    @OnMessage
    public void onMessage(String message, Session session) {
        JSONObject receive_object=new JSONObject(message);
        String token=receive_object.getString("token");
        JSONObject result=new JSONObject();
        if (!jwtConfigurationTool.validateToken(token)){
            result.put("result",false);
            result.put("dat","无权限操作");
            session.getAsyncRemote().sendText(result.toString());
        }else {
            String username= jwtConfigurationTool.getUserName(token);
            Optional<User> optionalUser =userService.get_one_user(username);
            if (optionalUser.isEmpty()){
                result.put("result",false);
                result.put("dat","无此用户");
                session.getAsyncRemote().sendText(result.toString());
            }
            else {
                if (receive_object.getString("command").compareTo("add_comment")==0)
                {
                    result.put("event","add_comment");
                    CommentService.AdditionalComment comment=commentService.add_comment(optionalUser.get(),receive_object.getJSONObject("video").getLong("id"),receive_object.getString("comment"),username);
                    result.put("result",true);
                    comment.is_self_user=true;
                    JSONObject jsonObject=new JSONObject(comment);
                    result.put("dat",jsonObject);
                    broadcast(receive_object.getJSONObject("video").getLong("id"),result.toString(),session);
                    //session.getAsyncRemote().sendText(result.toString());
                } else if (receive_object.getString("command").compareTo("load_comments")==0) {
                    result.put("event","load_comments");
                    JSONObject jsonObject=receive_object.getJSONObject("video");
                    //List<Comment>commentList=commentService.get_all_video_comments(jsonObject.getLong("id"));
                    result.put("result",true);
                    //JSONObject dat=new JSONObject(commentList);
                    //result.put("dat",dat);
                    session.getAsyncRemote().sendText(result.toString());
                } else if (receive_object.getString("command").compareTo("delete_comment")==0) {
                    result.put("event","delete_comment");
                    Long id=receive_object.getLong("comment");
                    result.put("result",commentService.delete_one_comment(id));
                    result.put("dat",id);
                    broadcast(receive_object.getJSONObject("video").getLong("id"),result.toString(),session);
                } else if (receive_object.getString("command").compareTo("add_favorite")==0) {
                    result.put("event","add_favorite");
                    Long video_id= receive_object.getLong("video_id");
                    String user_name=receive_object.getString("username");
                    boolean is_liked=receive_object.getBoolean("isLiked");
                    result.put("result",favoriteService.add_favorite(user_name,video_id,is_liked));
                    result.put("isLiked",!is_liked);
                    session.getAsyncRemote().sendText(result.toString());

                    JSONObject send_signal=new JSONObject();
                    send_signal.put("event","update_favorite");
                    send_signal.put("is_add",!is_liked);
                    broadcast_except_me(receive_object.getJSONObject("video").getLong("id"),send_signal.toString(),session);

                }
            }
        }
    }
    @OnClose
    public void onClose(Session session, @PathParam("video_id") Long video_id) {
        Set<Session>sessions=clients.get(video_id);
        if (sessions!=null){
            sessions.remove(session);
            if (sessions.isEmpty()){
                clients.remove(video_id);
            }
        }
    }
    private void broadcast(Long videoId, String message, Session senderSession){
        Set<Session>sessions=clients.get(videoId);
        if (sessions!=null||!sessions.isEmpty()){
            sessions.forEach(session -> {
                if (session.isOpen())
                {
                    session.getAsyncRemote().sendText(message);
                }
            });
        }
    }
    private void broadcast_except_me(Long videoId, String message, Session senderSession){
        Set<Session>sessions=clients.get(videoId);
        if (sessions!=null||!sessions.isEmpty()){
            sessions.forEach(session -> {
                if (session.isOpen())
                {
                    if (!session.equals(senderSession)){
                        session.getAsyncRemote().sendText(message);
                    }
                }
            });
        }
    }
}
