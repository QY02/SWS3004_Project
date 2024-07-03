package org.cs304.backend.service;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import org.cs304.backend.entity.Notification;

import java.util.List;

/**
 * @author zyp
 * @date 2024/4/21 0:35
 * @description
 **/
public interface INotificationService extends IService<Notification> {

    void insertEventPassNotification(String publisherId, int eventId);

    void insertEventNotPassNotification(String publisherId, int eventId, String comment);

    void insertReserveSessionNotification(String notifiedUserId, int sessionId);

    void insertAdminNotification(String publishId, String notifiedUserId, String title, String content);

    void insertEventModifyNotification(String userId, int eventId);

    void insertEventCancelNotification(String userId, int eventId, String comment);

    void insertListOfEventSessionsNotification(List<Integer> sessionIds, String title, String content);

    void updateReadStatus(int notificationId, Boolean read);

    JSONArray getAllNotificationsOfOneUser(String userId);

    JSONArray getNotificationsOfOneUserByPage(String userId, int pageNum, int pageSize);

    void deleteNotification(String notificationId);

    void readAll(String uid);
}
