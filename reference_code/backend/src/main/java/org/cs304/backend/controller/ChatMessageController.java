package org.cs304.backend.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cs304.backend.entity.ChatMessage;
import org.cs304.backend.entity.User;
import org.cs304.backend.mapper.ChatMessageMapper;
import org.cs304.backend.mapper.UserMapper;
import org.cs304.backend.utils.Result;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/chatMessage")
public class ChatMessageController {

    @Resource
    ChatMessageMapper chatMessageMapper;

    @Resource
    UserMapper userMapper;

    /**
     * This method is used to fetch chat messages between two users.
     * It is mapped to the "/get/{toUserID}" endpoint and is invoked on HTTP POST requests.
     *
     * @param request  The HttpServletRequest object that contains the request the client made of the servlet.
     * @param response The HttpServletResponse object that contains the response the servlet sends to the client.
     * @param toUserID The ID of the user to whom the chat messages are to be fetched.
     * @return Result object that contains the response code, message and a list of chat messages.
     */
    @PostMapping("/get/{toUserID}")
    @Operation(summary = "获取两个用户之间的聊天记录",
            description = """
                    ### 参数 ###
                    toUserID(String): 聊天对象的ID
                    ### 返回值 ###
                    {
                        "code": "200",
                        "msg": "Request Success",
                        "data": {
                            "avatarMap": {
                                "userID": "userAvatar",
                                "toUserID": "toUserAvatar"
                            },
                            "message": [
                                {
                                    "id": 1,
                                    "senderId": "userID",
                                    "receiverId": "toUserID",
                                    "content": "Hello",
                                    "sendTime": "2024-03-15T19:08:08",
                                    "hasRead": true,
                                    "receiverName": "toUserName",
                                    "senderName": "userName"
                                }
                            ]
                        }
                    }
                    ### 实现逻辑 ###
                    1. 获取当前用户ID
                    2. 获取当前用户和聊天对象的用户名和头像
                    3. 查询两个用户之间的聊天记录
                    4. 将聊天记录中的用户ID转换为用户名
                    5. 返回聊天记录
                    """)
    public Result onLogin(@NotNull HttpServletRequest request, HttpServletResponse response, @PathVariable("toUserID") String toUserID) {
        try {
            String userID = (String) request.getAttribute("loginUserId");
            String userName = userMapper.selectById(userID).getName();
            String toUserName = userMapper.selectById(toUserID).getName();
            Integer toUserAvatar = userMapper.selectById(toUserID).getIconId();
            Integer userAvatar = userMapper.selectById(userID).getIconId();
            HashMap<String, String> map = new HashMap<>();
            map.put(userID, userName);
            map.put(toUserID, toUserName);
            HashMap<String, String> avatarMap = new HashMap<>();
            avatarMap.put(userID, userAvatar.toString());
            avatarMap.put(toUserID, toUserAvatar.toString());
            List<ChatMessage> allMessages = chatMessageMapper.selectList(new QueryWrapper<ChatMessage>().eq("receiver_id", userID).eq("sender_id", toUserID).or().eq("receiver_id", toUserID).eq("sender_id", userID).orderByDesc("send_time").last("LIMIT 50"));
            List<ChatMessage> updateMessages = allMessages.stream().filter(chatMessage -> {
                if (chatMessage.getReceiverId().equals(userID)) {
                    return !chatMessage.getHasRead();
                }
                return false;
            }).toList();
            if (!updateMessages.isEmpty()) {
                chatMessageMapper.update(new UpdateWrapper<ChatMessage>().in("id", updateMessages.stream().map(ChatMessage::getId).toList()).set("has_read", true));
            }
            allMessages.sort(Comparator.comparing(ChatMessage::getSendTime));
            List<JSONObject> allMessages0 = allMessages.stream().map(s -> {
                JSONObject json = (JSONObject) JSON.toJSON(s);
                json.put("receiverName", map.get(s.getReceiverId()));
                json.put("senderName", map.get(s.getSenderId()));
                return json;
            }).toList();
            JSONObject returnObj = new JSONObject();
            returnObj.put("avatarMap", avatarMap);
            returnObj.put("message", allMessages0);
            return Result.success(response, returnObj);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.error(response, "fail to get chat message");
        }
    }

    @GetMapping("/getUnread")
    @Operation(summary = "获取未读消息用户",
            description = """
                    ### 参数 ###
                    无
                    ### 返回值 ###
                    {
                        "code": "200",
                        "msg": "Request Success",
                        "data": [
                            {
                                "id": "userID",
                                "name": "userName",
                                "iconId": 1
                            }
                        ]
                    }
                    ### 实现逻辑 ###
                    1. 获取当前用户ID
                    2. 查询未读消息的用户ID
                    3. 查询未读消息的用户信息
                    4. 返回未读消息的用户信息
                    """)
    public Result getUnread(HttpServletRequest request, HttpServletResponse response) {
        try {
            JSONObject returnObj = new JSONObject();
            String userID = (String) request.getAttribute("loginUserId");
            List<String> users = chatMessageMapper.selectList(new QueryWrapper<ChatMessage>().select("sender_id").eq("receiver_id", userID).eq("has_read", false).groupBy("sender_id")).stream().map(ChatMessage::getSenderId).toList();
            if (users.isEmpty()) {
                returnObj.put("unread", List.of());
            }else {
                List<User> userList = userMapper.selectBatchIds(users);
                returnObj.put("unread", userList);
            }
            List<String> users2 = chatMessageMapper.selectList(new QueryWrapper<ChatMessage>().select("sender_id").eq("receiver_id", userID).eq("has_read", true).groupBy("sender_id")).stream().map(ChatMessage::getSenderId).toList();
            users2 = users2.stream().filter(s -> !users.contains(s)).toList();
            if (users2.isEmpty()) {
                returnObj.put("read", List.of());
            }else {
                List<User> userList = userMapper.selectBatchIds(users2);
                returnObj.put("read", userList);
            }
            return Result.success(response, returnObj);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.error(response, "fail to get unread message");
        }
    }
}