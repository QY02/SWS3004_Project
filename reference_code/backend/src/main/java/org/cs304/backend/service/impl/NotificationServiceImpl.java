package org.cs304.backend.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.cs304.backend.constant.constant_NotificationStatus;
import org.cs304.backend.constant.constant_NotificationType;
import org.cs304.backend.entity.*;
import org.cs304.backend.exception.ServiceException;
import org.cs304.backend.mapper.*;
import org.cs304.backend.service.INotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * @author zyp
 * @date 2024/4/21 0:36
 * @description
 **/
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements INotificationService {

    @Resource
    private NotificationMapper notificationMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private EventMapper eventMapper;

    @Resource
    private EventSessionMapper eventSessionMapper;

    @Resource
    OrderRecordMapper orderRecordMapper;

    @Resource
    private EmailService emailService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss EEEE", Locale.CHINESE);

    private static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }



    private void insertNotification(String publisherId, String notifiedUserId, String title, String content, LocalDateTime date, int type) {
        User user = userMapper.selectById(notifiedUserId);
        if (user == null) {
            throw new ServiceException("User not found");
        } else {
            String toEmail = user.getEmail();
            Notification notification = new Notification();
            notification.setStatus(constant_NotificationStatus.UNREAD);
            notification.setType(type);
            notification.setNotifiedUserId(notifiedUserId);
            notification.setPublisherId(publisherId);
            notification.setCreateTime(date);
            notification.setNotifyTime(date);
            notification.setTitle(title);
            notification.setContent(content);

            notificationMapper.insert(notification);
            emailService.sendEmail(toEmail, title, content, date);
        }
    }
    private void insertImmediateNotification(String publisherId, String notifiedUserId, String title, String content, int type) {
        insertNotification(publisherId, notifiedUserId, title, content, LocalDateTime.now(), type);
    }
    private void insertHalfHourNotification(String publisherId, String notifiedUserId, String eventTitle, LocalDateTime startTime, LocalDateTime endTime, int type) {
        LocalDateTime notificationTime = startTime.minusMinutes(30);
        String title = String.format("活动'%s'将于30分钟后开始", eventTitle);
        String content = String.format("您好！\n您参加的活动：\n\n活动名称：'%s'\n场次：%s ~ %s\n\n将于30分钟后开始，请准时参加。", eventTitle, formatDateTime(startTime), formatDateTime(endTime));
        if (LocalDateTime.now().isBefore(notificationTime)) {
            insertNotification(publisherId, notifiedUserId, title, content, notificationTime, type);
        }
    }
    private void insert15MinNotification(String publisherId, String notifiedUserId, String eventTitle, LocalDateTime startTime, LocalDateTime endTime, int type) {
        LocalDateTime notificationTime = startTime.minusMinutes(15);
        String title = String.format("活动'%s'将于15分钟后开始", eventTitle);
        String content = String.format("您好！\n您参加的活动：\n\n活动名称：'%s'\n场次：%s ~ %s\n\n将于15分钟后开始，请准时参加。", eventTitle, formatDateTime(startTime), formatDateTime(endTime));
        if (LocalDateTime.now().isBefore(notificationTime)) {
            insertNotification(publisherId, notifiedUserId, title, content, notificationTime, type);
        }
    }
    private void insert0MinNotification(String publisherId, String notifiedUserId, String eventTitle, LocalDateTime startTime, LocalDateTime endTime, int type) {
        String title = String.format("活动'%s'已经开始", eventTitle);
        String content = String.format("您好！\n您参加的活动：\n\n活动名称：'%s'\n场次：%s ~ %s\n\n已经开始。", eventTitle, formatDateTime(startTime), formatDateTime(endTime));
        if (LocalDateTime.now().isBefore(startTime)) {
            insertNotification(publisherId, notifiedUserId, title, content, startTime, type);
        }
    }


    private JSONArray convertNotificationListToJsonArray(List<Notification> notificationList) {
        if (notificationList != null && !notificationList.isEmpty()) {
            // 将查询到的数据列表转换为 JSONArray
            return (JSONArray) JSON.toJSON(notificationList);
        } else {
            // 返回空的 JSONArray，表示没有找到符合条件的通知
            return new JSONArray();
        }
    }

    @Override
    public void insertEventPassNotification(String publisherId, int eventId) {
        Event event = eventMapper.selectById(eventId);
        if (event == null) {
            throw new ServiceException("Event not found");
        } else {
            String notifiedUserId = event.getPublisherId();
            String eventTitle = event.getName();
            String title = "活动申请通过";
            String content = String.format("活动'%s'申请已通过！\n请注意查看。", eventTitle);
            insertImmediateNotification(publisherId, notifiedUserId, title, content, constant_NotificationType.PASS);
        }
    }

    @Override
    public void insertEventNotPassNotification(String publisherId, int eventId, String comment) {
        Event event = eventMapper.selectById(eventId);
        if (event == null) {
            throw new ServiceException("Event not found");
        } else {
            String notifiedUserId = event.getPublisherId();
            String eventTitle = event.getName();
            String title = "活动申请未通过";
            String content = String.format("您好！\n很遗憾地通知您,您申请的活动'%s'申请未通过。\n\n审核意见如下：\n%s", eventTitle, comment);
            insertImmediateNotification(publisherId, notifiedUserId, title, content, constant_NotificationType.NOTPASS);

        }
    }

    @Override
    public void insertReserveSessionNotification(String notifiedUserId, int sessionId) {
        EventSession eventSession = eventSessionMapper.selectById(sessionId);
        if (eventSession == null) {
            throw new ServiceException("Event session not found");
        } else {
            int eventId = eventSession.getEventId();
            Event event = eventMapper.selectById(eventId);
            if (event == null) {
                throw new ServiceException("Event not found");
            } else {
                String publisherId = event.getPublisherId();
                String eventTitle = event.getName();
                LocalDateTime startTime = eventSession.getStartTime();
                LocalDateTime endTime = eventSession.getEndTime();
                String title = "成功参加活动";
                String content = String.format("您好！\n您已成功参加活动'%s'的'%s ~ %s'场次。", eventTitle, formatDateTime(startTime), formatDateTime(endTime));
                insertImmediateNotification(publisherId, notifiedUserId, title, content, constant_NotificationType.RESERVE);
                insertHalfHourNotification(publisherId,notifiedUserId,eventTitle,startTime,endTime,constant_NotificationType.RESERVE);
                insert15MinNotification(publisherId,notifiedUserId,eventTitle,startTime,endTime,constant_NotificationType.RESERVE);
                insert0MinNotification(publisherId,notifiedUserId,eventTitle,startTime,endTime,constant_NotificationType.RESERVE);
            }
        }
    }

    @Override
    public void insertAdminNotification(String publishId, String notifiedUserId, String title, String content) {
        insertImmediateNotification(publishId, notifiedUserId, title, content, constant_NotificationType.OTHER);
    }

    @Override
    public void insertEventModifyNotification(String publisherId, int eventId) {
        Event event = eventMapper.selectById(eventId);
        if (event == null) {
            throw new ServiceException("Event not found");
        } else {
            String notifiedUserId = event.getPublisherId();
            String eventTitle = event.getName();
            String title = "活动修改通知";
            String content = String.format("您好！\n您参加的活动'%s'信息发生了变化！", eventTitle);
            insertImmediateNotification(publisherId, notifiedUserId, title, content, constant_NotificationType.MODIFY);
        }
    }

    @Override
    public void insertEventCancelNotification(String userId, int sessionId, String comment) {
        EventSession eventSession = eventSessionMapper.selectById(sessionId);
        if (eventSession == null) {
            throw new ServiceException("Event session not found");
        } else {
            int eventId = eventSession.getEventId();
            Event event = eventMapper.selectById(eventId);
            if (event == null) {
                throw new ServiceException("Event not found");
            } else {
                String publisherId = event.getPublisherId();
                String eventTitle = event.getName();
                LocalDateTime startTime = eventSession.getStartTime();
                LocalDateTime endTime = eventSession.getEndTime();
                String title = "活动取消通知";
                String content = String.format("您好！\n遗憾地通知您，您参加的活动'%s'的‘%s ~ %s'场次取消。\n\n具体原因如下：\n%s",
                        eventTitle, formatDateTime(startTime), formatDateTime(endTime), comment);
                insertImmediateNotification(publisherId, userId, title, content, constant_NotificationType.CANCEL);

            }
        }
    }
    @Override
    public void insertListOfEventSessionsNotification(List<Integer> sessionIds, String title, String content) {
            for(Integer o : sessionIds){
                insertEventSessionNotification(o,title,content);
            }
    }

     private void insertEventSessionNotification(int sessionId, String title,String content) {
        EventSession eventSession = eventSessionMapper.selectById(sessionId);
        if (eventSession == null) {
            throw new ServiceException("Event session not found");
        } else {
            QueryWrapper<OrderRecord> queryWrapper = new QueryWrapper<OrderRecord>().eq("event_session_id", sessionId);
            List<OrderRecord> orderRecordList = orderRecordMapper.selectList(queryWrapper);
            for (OrderRecord order : orderRecordList){
                String userId=order.getUserId();
                int eventId = eventSession.getEventId();
                Event event = eventMapper.selectById(eventId);
                if (event == null) {
                    throw new ServiceException("Event not found");
                } else {
                    String publisherId = event.getPublisherId();
                    String eventTitle = event.getName();
                    String content_sent = String.format("您好！\n以下是来自活动'%s'发布者的通知：\n\n%s",
                            eventTitle, content);
                    insertImmediateNotification(publisherId, userId, title, content_sent, constant_NotificationType.MODIFY);
                }
            }
        }
    }

    @Override
    public void updateReadStatus(int notificationId, Boolean read) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new ServiceException("Notification not found.");
        } else {
            int status = constant_NotificationStatus.UNREAD;
            if (read) {
                status = constant_NotificationStatus.READ;
            }
            notification.setStatus(status);
            notificationMapper.updateById(notification);
        }
    }

    @Override
    public JSONArray getAllNotificationsOfOneUser(String userId) {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> notificationList = notificationMapper.selectList(new QueryWrapper<Notification>().eq("notified_user_id", userId)
                .lt("notify_time", now)
                .orderByDesc("notify_time"));
        return convertNotificationListToJsonArray(notificationList);
    }

    @Override
    public JSONArray getNotificationsOfOneUserByPage(String userId, int pageNum, int pageSize) {
        LocalDateTime now = LocalDateTime.now();
        // 创建分页对象，指定当前页和每页记录数
        Page<Notification> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("notified_user_id", userId)
                .lt("notify_time", now)
                .orderByDesc("notify_time");

        // 执行分页查询
        IPage<Notification> notificationPage = notificationMapper.selectPage(page, queryWrapper);

        List<Notification> notificationList = notificationPage.getRecords();

        return convertNotificationListToJsonArray(notificationList);
    }

    @Override
    public void deleteNotification(String notificationId) {
        notificationMapper.deleteById(notificationId);
    }

    @Override
    public void readAll(String uid) {
        List<Notification> notificationList=notificationMapper.selectList(new QueryWrapper<Notification>().eq("notified_user_id",uid));
        if (!notificationList.isEmpty()){
            for (Notification notification :notificationList){
                if (notification.getStatus()==constant_NotificationStatus.UNREAD){
                    notification.setStatus(constant_NotificationStatus.READ);
                    notificationMapper.updateById(notification);
                }
            }
        }
    }
}
