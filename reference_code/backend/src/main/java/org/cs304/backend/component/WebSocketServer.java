package org.cs304.backend.component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.cs304.backend.entity.ChatMessage;
import org.cs304.backend.service.IChatMessageService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author websocket服务
 */

@Slf4j
@ServerEndpoint(value = "/chatserver/{userID}")
@Component
public class WebSocketServer {

    IChatMessageService chatMessageService;
//    IUserService userService;

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketServer.applicationContext = applicationContext;
    }

    /**
     * 记录当前在线连接数
     */
    public static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();//<userID,session>

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userID") String userID) {
//        userService = applicationContext.getBean(IUserService.class);
//        String username = userService.getById(userID).getName();
        if (sessionMap.containsKey(userID)) {
            sendMessage("USER ALREADY EXISTS", session);
            return;
        }
        sessionMap.put(userID, session);
        log.info("有新用户加入，username={}, 当前在线人数为：{}", userID, sessionMap.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam("userID") String userID) {
//        userService = applicationContext.getBean(IUserService.class);
//        String username = userService.getById(userID).getName();
        sessionMap.remove(userID);
        log.info("有一连接关闭，移除username={}的用户session, 当前在线人数为：{}", userID, sessionMap.size());
    }

    /**
     * 收到客户端消息后调用的方法
     * 后台收到客户端发送过来的消息
     * onMessage 是一个消息的中转站
     * 接受 浏览器端 socket.send 发送过来的 json数据
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userID") String userID) {
        chatMessageService = applicationContext.getBean(IChatMessageService.class);
//        userService = applicationContext.getBean(IUserService.class);
        log.info("服务端收到用户username={}的消息:{}", userID, message);
        JSONObject obj = JSON.parseObject(message);
        String toUserID = obj.getString("to"); // to表示发送给哪个用户，比如 admin
        String text = obj.getString("text"); // 发送的消息文本  hello
        Session toSession = sessionMap.get(toUserID); // 根据 to用户名来获取 session，再通过session发送消息文本
        if (toSession != null) {
            // 服务器端 再把消息组装一下，组装后的消息包含发送人和发送的文本内容
            // {"from": "zhang", "text": "hello"}
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("from", userID);  // from 是 zhang
            jsonObject.put("text", text);  // text 同上面的text
            this.sendMessage(jsonObject.toString(), toSession);
            log.info("发送给用户username={}，消息：{}", toUserID, jsonObject);
        } else {
            log.info("发送给离线用户username={}，消息：{}", toUserID, text);
        }
        ChatMessage msg = new ChatMessage();
        msg.setSenderId(userID);
        msg.setReceiverId(toUserID);
        msg.setContent(text);
        msg.setSendTime(LocalDateTime.now());
        msg.setHasRead(false);
        chatMessageService.save(msg);
    }


    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 服务端发送消息给客户端
     */
    private void sendMessage(String message, Session toSession) {
        try {
            log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
            toSession.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }

    /**
     * 服务端发送消息给所有客户端
     */
    private void sendAllMessage(String message) {
        try {
            for (Session session : sessionMap.values()) {
                log.info("服务端给客户端[{}]发送消息{}", session.getId(), message);
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }
}
