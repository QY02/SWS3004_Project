package org.cs304.backend; /**
 * @author zyp
 * @date 2024/5/23 23:14
 * @description
 **/

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.cs304.backend.constant.constant_User;
import org.cs304.backend.controller.AdminController;
import org.cs304.backend.controller.EventController;
import org.cs304.backend.entity.Event;
import org.cs304.backend.entity.OrderRecord;
import org.cs304.backend.mapper.EventMapper;
import org.cs304.backend.service.IEventService;
import org.cs304.backend.utils.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
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
public class EventControllerTest {

    @Mock
    private IEventService eventService;

    @InjectMocks
    private EventController eventController;
    @InjectMocks
    private AdminController adminController;

    @Mock
    EventMapper eventMapper;

    //    @Mock
//    private HttpServletRequest request;
//
//    @Mock
//    private HttpServletResponse response;

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("Should return success when posting new event")
    public void shouldReturnSuccessWhenPostingNewEvent() {
        JSONObject event = new JSONObject();
        // 设置事件的相关属性

        when(eventService.createEventStart(com.alibaba.fastjson2.JSONObject.from(event))).thenReturn(com.alibaba.fastjson2.JSONObject.from(new JSONObject())); // 设置mock返回值

        Result result = eventController.postNewEvent(response, com.alibaba.fastjson2.JSONObject.from(event));

        assertEquals("200", result.getCode()); // 验证结果
    }

    @Test
    @DisplayName("success - get not exist event detail")
    public void shouldReturnEventDetail() {
        Event event = new Event();
        event.setName("Test Event");
        event.setStatus(1);
        event.setId(1);

        when(eventMapper.insert(event)).thenReturn(1);

        when(eventService.createEventStart(com.alibaba.fastjson2.JSONObject.from(event))).thenReturn(com.alibaba.fastjson2.JSONObject.from(new JSONObject())); // 设置mock返回值
//        Result result = eventController.postNewEvent(response, (com.alibaba.fastjson2.JSONObject) JSON.toJSON(event));
//        assertEquals("200", result.getCode()); // 验证结果
        Result result = eventController.getEventDetail(request, response, 1);
        // not exist
        assertEquals("404", result.getCode()); // 验证结果
    }

    @Test
    @DisplayName("Should return a event when getting an event by user id")
    public void shouldReturnEventWhenGettingEventByUserId() {
//        JSONObject jsonObject = new JSONObject();
        request.setAttribute("loginUserType", 0);
        when(eventMapper.selectList(new QueryWrapper<Event>().eq("publisher_id", 12110141))).thenReturn(new ArrayList<>());
        Result result = eventController.getMyPost(request, response, 12110141);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should return a hot events when getting hot event list")
    public void shouldReturnHotEventWhenGettingHotEvent() {

//        when(eventMapper.selectList(new QueryWrapper<Event>().eq("publisher_id", 12110141))).thenReturn(new ArrayList<>());
        Result result = eventController.getHotEvents(request, response);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should return a event when getting Recommend events by user id")
    public void shouldReturnRecommendEventWhenGettingEvents() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", "12110141");
//        request.setAttribute("loginUserType", 0);
//        when(eventMapper.selectList(new QueryWrapper<Event>().eq("publisher_id", 12110141))).thenReturn(new ArrayList<>());
        Result result = eventController.getRecommendEvents(request, response, com.alibaba.fastjson2.JSONObject.from(jsonObject));

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return a list of events when getting event")
    public void shouldReturnEvents() {
        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("eventId", 1);
        jsonObject.put("type", 0);
        when(eventMapper.selectList(new QueryWrapper<Event>().eq("type", 0))).thenReturn(new ArrayList<>());

        Result result = eventController.getAllEvents(request, response);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return success when submitting booking data")
    public void shouldReturnSuccessWhenSubmittingBookingData() {
        request.setAttribute("loginUserType", constant_User.USER);
        request.setAttribute("loginUserId", "12111111");
        OrderRecord orderRecord = new OrderRecord();
        doNothing().when(eventService).submitBookingData(anyInt(), anyString(), any(OrderRecord.class));
        Result result = eventController.submitBookingData(response, request, orderRecord);
        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return a list of event sessions when getting event sessions by event id")
    public void shouldReturnEventSessionsWhenGettingEventSessionsByEventId() {
        request.setAttribute("loginUserType", constant_User.USER);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("eventId", 1);
        when(eventService.getEventSessionsByEventId(constant_User.USER, 1)).thenReturn(new ArrayList<>());
        Result result = eventController.getEventSessionsByEventId(response, request, jsonObject);
        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return attachment path when getting photo by id")
    public void shouldReturnAttachmentPathWhenGettingPhotoById() {
        when(eventService.getAttachment(1)).thenReturn("");
        Result result = eventController.getPhotoById(response, 1);
        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Test getEventDetail")
    public void testGetEventDetail() {
        when(eventService.exists(any())).thenReturn(false);
        Result result = eventController.getEventDetail(request, response, 1);
        assertEquals("404", result.getCode());
        when(eventService.exists(any())).thenReturn(true);
        result = eventController.getEventDetail(request, response, 1);
        assertEquals("200", result.getCode());
    }
}

