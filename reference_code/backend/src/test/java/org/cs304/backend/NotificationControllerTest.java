package org.cs304.backend;

/**
 * @author zyp
 * @date 2024/5/23 23:04
 * @description
 **/

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cs304.backend.constant.constant_User;
import org.cs304.backend.controller.NotificationController;
import org.cs304.backend.entity.Notification;
import org.cs304.backend.mapper.NotificationMapper;
import org.cs304.backend.service.INotificationService;
import org.cs304.backend.utils.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
public class NotificationControllerTest {

    @InjectMocks
    NotificationController notificationController;

    @Mock
    INotificationService notificationService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;
    @Mock
    NotificationMapper notificationMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return success when posting event pass notification")
    public void shouldReturnSuccessWhenPostingEventPassNotification() {
        int eventId = 123; // 设置事件ID

        doNothing().when(notificationService).insertEventPassNotification("testUserId", eventId);

        when(request.getAttribute("loginUserId")).thenReturn("testUserId");

        Result result = notificationController.postEventPass(response, request, eventId);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return success when posting event not pass notification")
    public void shouldReturnSuccessWhenPostingEventNotPassNotification() {
        int eventId = 123; // 设置事件ID
        JSONObject data = new JSONObject();
        data.put("comment", "Test comment"); // 设置反馈评论

        doNothing().when(notificationService).insertEventNotPassNotification("testUserId", eventId, "Test comment");
        when(request.getAttribute("loginUserId")).thenReturn("testUserId");

        Result result = notificationController.postEventNotPass(response, request, eventId, data);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return success when posting reserve session notification")
    public void shouldReturnSuccessWhenPostingReserveSessionNotification() {
        int eventSessionId = 123; // 设置事件场次ID

        doNothing().when(notificationService).insertReserveSessionNotification("testUserId", eventSessionId);
        when(request.getAttribute("loginUserId")).thenReturn("testUserId");

        Result result = notificationController.postReserveSession(response, request, eventSessionId);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return success when posting admin notification")
    public void shouldReturnSuccessWhenPostingAdminNotification() {
        JSONObject data = new JSONObject();
        data.put("title", "Test title");
        data.put("content", "Test content");

        when(request.getAttribute("loginUserId")).thenReturn("testUserId");
        when(request.getAttribute("loginUserType")).thenReturn(constant_User.ADMIN);

        Result result = notificationController.postAdminNotification(response, request, "testNotifiedUserId", data);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return success when posting admin notification")
    public void shouldReturnErrWhenPostingAdminNotification() {
        JSONObject data = new JSONObject();
        data.put("title", "Test title");
        data.put("content", "Test content");

        when(request.getAttribute("loginUserId")).thenReturn("testUserId");
        when(request.getAttribute("loginUserType")).thenReturn(constant_User.USER);

        Result result = notificationController.postAdminNotification(response, request, "testNotifiedUserId", data);

        assertEquals("403", result.getCode());
    }

    @Test
    public void shouldReturnAllNotificationsOfOneUserForAdmin() throws InstantiationException, IllegalAccessException {
        String userId = "testUserId";
        String adminId = "adminId";
        int adminUserType = constant_User.ADMIN;

        when(request.getAttribute("loginUserId")).thenReturn(adminId);
        when(request.getAttribute("loginUserType")).thenReturn(adminUserType);
        when(notificationService.getAllNotificationsOfOneUser(userId)).thenReturn(JSONArray.of(JSONArray.class));

        Result result = notificationController.getAllNotificationsOfOneUser(response, request, userId);

        assertEquals("200", result.getCode());
        verify(notificationService).getAllNotificationsOfOneUser(userId);
    }

    @Test
    public void shouldReturnErrorWhenNonAdminTriesToGetOthersNotifications() {
        String userId = "testUserId";
        String nonAdminId = "nonAdminId";
        int nonAdminUserType = constant_User.USER; // assuming USER is a non-admin constant

        when(request.getAttribute("loginUserId")).thenReturn(nonAdminId);
        when(request.getAttribute("loginUserType")).thenReturn(nonAdminUserType);

        Result result = notificationController.getAllNotificationsOfOneUser(response, request, userId);

        assertEquals("403", result.getCode());
        assertEquals("Only admin can get others' notifications", result.getMsg());
    }


    @Test
    public void shouldReturnAllMyNotifications() {
        String userId = "testUserId";

        when(request.getAttribute("loginUserId")).thenReturn(userId);
        when(notificationService.getAllNotificationsOfOneUser(userId)).thenReturn(JSONArray.of(JSONArray.class));

        Result result = notificationController.getAllMyNotifications(response, request);

        assertEquals("200", result.getCode());
        verify(notificationService).getAllNotificationsOfOneUser(userId);
    }

    @Test
    public void testDeleteMyNotification_Admin_Success() {
        String notificationId = "notificationId";
        String uid = "adminId";
        int userType = constant_User.ADMIN;
        Notification notification = new Notification();
        notification.setNotifiedUserId("testUserId");

        when(request.getAttribute("loginUserId")).thenReturn(uid);
        when(request.getAttribute("loginUserType")).thenReturn(userType);
        when(notificationMapper.selectById(notificationId)).thenReturn(notification);

        Result result = notificationController.deleteMyNotification(response, request, notificationId);

        assertEquals("200", result.getCode());
        verify(notificationService).deleteNotification(notificationId);
    }

    @Test
    public void testDeleteMyNotification_Admin_Err() {
        String notificationId = "notificationId";
        String uid = "adminId";
        int userType = constant_User.USER;
        Notification notification = new Notification();
        notification.setNotifiedUserId("testUserId");

        when(request.getAttribute("loginUserId")).thenReturn(uid);
        when(request.getAttribute("loginUserType")).thenReturn(userType);
        when(notificationMapper.selectById(notificationId)).thenReturn(notification);

        Result result = notificationController.deleteMyNotification(response, request, notificationId);

        assertEquals("403", result.getCode());
    }

    @Test
    public void testDeleteMyNotification_Admin_Null() {
        String notificationId = "notificationId";
        String uid = "adminId";
        int userType = constant_User.USER;

        when(request.getAttribute("loginUserId")).thenReturn(uid);
        when(request.getAttribute("loginUserType")).thenReturn(userType);
        when(notificationMapper.selectById(notificationId)).thenReturn(null);

        Result result = notificationController.deleteMyNotification(response, request, notificationId);

        assertEquals("401", result.getCode());
    }

    @Test
    public void testUpdateReadStatus_Admin_Success() {
        int notificationId = 123;
        boolean read = true;
        String uid = "adminId";
        int userType = constant_User.ADMIN;
        Notification notification = new Notification();
        notification.setNotifiedUserId(uid);

        when(request.getAttribute("loginUserId")).thenReturn(uid);
        when(request.getAttribute("loginUserType")).thenReturn(userType);
        when(notificationMapper.selectById(notificationId)).thenReturn(notification);

        Result result = notificationController.updateReadStatus(response, request, notificationId, read);

        assertEquals("200", result.getCode());
        verify(notificationService).updateReadStatus(notificationId, read);
    }

    @Test
    public void testUpdateReadStatus_Admin_Err() {
        int notificationId = 123;
        boolean read = true;
        String uid = "adminId";
        int userType = constant_User.USER;
        Notification notification = new Notification();
        notification.setNotifiedUserId("123");

        when(request.getAttribute("loginUserId")).thenReturn(uid);
        when(request.getAttribute("loginUserType")).thenReturn(userType);
        when(notificationMapper.selectById(notificationId)).thenReturn(notification);

        Result result = notificationController.updateReadStatus(response, request, notificationId, read);

        assertEquals("403", result.getCode());
    }

    @Test
    public void testUpdateReadStatus_Admin_Null() {
        int notificationId = 123;
        boolean read = true;
        String uid = "adminId";
        int userType = constant_User.ADMIN;

        when(request.getAttribute("loginUserId")).thenReturn(uid);
        when(request.getAttribute("loginUserType")).thenReturn(userType);
        when(notificationMapper.selectById(notificationId)).thenReturn(null);

        Result result = notificationController.updateReadStatus(response, request, notificationId, read);

        assertEquals("401", result.getCode());
    }


    @Test
    public void testReadAll_Success() {
        String uid = "testUserId";

        when(request.getAttribute("loginUserId")).thenReturn(uid);

        Result result = notificationController.readAll(response, request);

        assertEquals("200", result.getCode());
        verify(notificationService).readAll(uid);
    }
}

