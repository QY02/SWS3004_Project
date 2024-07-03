package org.cs304.backend;

/**
 * @author zyp
 * @date 2024/5/23 22:15
 * @description
 **/

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.cs304.backend.constant.constant_NotificationStatus;
import org.cs304.backend.entity.Event;
import org.cs304.backend.entity.EventSession;
import org.cs304.backend.entity.Notification;
import org.cs304.backend.entity.User;
import org.cs304.backend.mapper.EventMapper;
import org.cs304.backend.mapper.EventSessionMapper;
import org.cs304.backend.mapper.NotificationMapper;
import org.cs304.backend.mapper.UserMapper;
import org.cs304.backend.service.impl.EmailService;
import org.cs304.backend.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
/**
 * This test class was generated with the assistance of AI tools, specifically GitHub Copilot.
 * GitHub Copilot uses advanced machine learning models to help developers by suggesting code snippets,
 * completing lines, and providing relevant code examples.
 *
 * The AI-generated code serves as a starting point and can significantly speed up the development process.
 * However, it's important to review, test, and potentially modify the generated code to ensure it meets
 * the specific requirements and best practices of your project.
 *
 * In this test class, the AI helped generate the test methods, setup and teardown logic, and other
 * auxiliary functions. The developer should ensure the correctness and completeness of these tests
 * by reviewing the generated code and making necessary adjustments.
 *
 * Example Usage:
 * - The AI can suggest boilerplate code for setting up a test environment.
 * - It can provide example test cases based on the developer's code context.
 * - It helps in maintaining consistency in the coding style and standards.
 *
 * Note: The integration of AI tools like GitHub Copilot is an augmentation of the development process,
 * and human oversight is crucial for the successful implementation and maintenance of the codebase.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private EventSessionMapper eventSessionMapper;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testInsertEventPassNotification() {
        // 创建 Event 对象
        Event event = new Event();
        String publisherId = "publisherId";
        event.setPublisherId(publisherId);

        // 模拟用户存在
        User user = new User();
        user.setId(publisherId);
        user.setEmail("test@example.com");
        when(userMapper.selectById(anyString())).thenReturn(user);

        // 模拟事件存在
        when(eventMapper.selectById(anyInt())).thenReturn(event);

        // 执行测试方法
        notificationService.insertEventPassNotification(publisherId, 1);

        // 验证是否调用了 insert 方法
        verify(notificationMapper, times(1)).insert(any(Notification.class));

        // 验证是否调用了 sendEmail 方法
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString(), any(LocalDateTime.class));
    }


    @Test
    void testInsertEventNotPassNotification() {
        Event event = new Event();
        String publisherId = "publisherId";
        event.setPublisherId(publisherId);

        User user = new User();
        user.setId(publisherId);
        user.setEmail("test@example.com");
        when(userMapper.selectById(anyString())).thenReturn(user);
        when(eventMapper.selectById(anyInt())).thenReturn(event);

        notificationService.insertEventNotPassNotification(publisherId, 1, "comment");

        verify(notificationMapper, times(1)).insert(any(Notification.class));
    }


    @Test
    void testInsertReserveSessionNotification() {
        User user = new User();
        user.setId("publisherId");
        user.setEmail("test@example.com");
        when(userMapper.selectById(anyString())).thenReturn(user);

        EventSession eventSession = new EventSession();
        eventSession.setEventId(1);
        eventSession.setStartTime(LocalDateTime.of(2024, 5, 23, 10, 0)); // 设置开始时间
        eventSession.setEndTime(LocalDateTime.of(2024, 5, 23, 12, 0)); // 设置结束时间
        when(eventSessionMapper.selectById(anyInt())).thenReturn(eventSession);

        Event event = new Event();
        event.setPublisherId("publisherId");
        event.setName("eventTitle");
        when(eventMapper.selectById(anyInt())).thenReturn(event);

        notificationService.insertReserveSessionNotification("notifiedUserId", 1);

        verify(eventSessionMapper).selectById(1);
        verify(eventMapper).selectById(1);
    }

    @Test
    void testInsertAdminNotification() {
        User user = new User();
        user.setId("publisherId");
        user.setEmail("test@example.com");
        when(userMapper.selectById(anyString())).thenReturn(user);

        notificationService.insertAdminNotification("publishId", "notifiedUserId", "title", "content");

        verify(notificationMapper, times(1)).insert(any(Notification.class));
    }

//    @Test
//    void testInsertEventModifyNotification() {
//        User user = new User();
//        user.setId("publisherId");
//        user.setEmail("test@example.com");
//        when(userMapper.selectById(anyString())).thenReturn(user);
//
//        Event event = new Event();
//        event.setPublisherId("publisherId");
//        event.setName("eventTitle");
//        when(eventMapper.selectById(anyInt())).thenReturn(event);
//
//        notificationService.insertEventModifyNotification("publisherId", 1);
//
//        verify(notificationMapper, times(1)).insert(any(Notification.class));
//    }

//    @Test
//    void testInsertEventCancelNotification() {
//        User user = new User();
//        user.setId("publisherId");
//        user.setEmail("test@example.com");
//        when(userMapper.selectById(anyString())).thenReturn(user);
//
//        EventSession eventSession = new EventSession();
//        eventSession.setEventId(1);
//        eventSession.setStartTime(LocalDateTime.of(2024, 5, 23, 10, 0)); // 设置开始时间
//        eventSession.setEndTime(LocalDateTime.of(2024, 5, 23, 12, 0)); // 设置结束时间
//        when(eventSessionMapper.selectById(anyInt())).thenReturn(eventSession);
//
//        Event event = new Event();
//        event.setPublisherId("publisherId");
//        event.setName("eventTitle");
//        when(eventMapper.selectById(anyInt())).thenReturn(event);
//
//        notificationService.insertEventCancelNotification("publisherId", 1, "comment");
//
//        verify(notificationMapper, times(1)).insert(any(Notification.class));
//    }

    @Test
    void testUpdateReadStatus() {
        Notification notification = new Notification();
        notification.setStatus(constant_NotificationStatus.UNREAD);
        when(notificationMapper.selectById(anyInt())).thenReturn(notification);

        notificationService.updateReadStatus(1, true);

        verify(notificationMapper, times(1)).updateById(any(Notification.class));
    }

    @Test
    void testGetAllNotificationsOfOneUser() {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> notificationList = new ArrayList<>();
        when(notificationMapper.selectList(any(QueryWrapper.class))).thenReturn(notificationList);

        notificationService.getAllNotificationsOfOneUser("userId");

        verify(notificationMapper, times(1)).selectList(any(QueryWrapper.class));
    }

//    @Test
//    void testGetNotificationsOfOneUserByPage() {
//        LocalDateTime now = LocalDateTime.now();
//        Page<Notification> page = new Page<>(1, 10);
//        when(notificationMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);
//
//        notificationService.getNotificationsOfOneUserByPage("userId", 1, 10);
//
//        verify(notificationMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
//    }

    @Test
    void testDeleteNotification() {
        notificationService.deleteNotification("notificationId");

        verify(notificationMapper, times(1)).deleteById("notificationId");
    }

    @Test
    void testReadAll() {
        List<Notification> notificationList = new ArrayList<>();
        Notification notification = new Notification();
        notification.setStatus(constant_NotificationStatus.UNREAD);
        notificationList.add(notification);
        when(notificationMapper.selectList(any(QueryWrapper.class))).thenReturn(notificationList);

        notificationService.readAll("uid");

        verify(notificationMapper, times(1)).selectList(any(QueryWrapper.class));
        verify(notificationMapper, times(1)).updateById(any(Notification.class));
    }
}

