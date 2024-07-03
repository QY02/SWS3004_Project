package org.cs304.backend;

/**
 * @author zyp
 * @date 2024/5/23 19:17
 * @description
 **/

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.cs304.backend.constant.constant_User;
import org.cs304.backend.entity.*;
import org.cs304.backend.exception.ServiceException;
import org.cs304.backend.mapper.*;
import org.cs304.backend.service.IAttachmentService;
import org.cs304.backend.service.IEventSessionService;
import org.cs304.backend.service.INotificationService;
import org.cs304.backend.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.*;
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
public class EventServiceImplTest {

    @Spy
    @InjectMocks
    private EventServiceImpl eventService;

    @Mock
    private IEventSessionService eventSessionService;
    @Mock
    private IAttachmentService attachmentService;
    @Mock
    EventMapper eventMapper;
    @Mock
    private EventSessionMapper eventSessionMapper;
    @Mock
    private EntityAttachmentRelationMapper entityAttachmentRelationMapper;
    @Mock
    private SeatMapper seatMapper;
    @Mock
    private OrderRecordMapper orderRecordMapper;
    @Mock
    private UserInteractionMapper userInteractionMapper;
    @Mock
    private UserFavoriteTypeMapper userFavoriteTypeMapper;
    @Mock
    private HistoryMapper historyMapper;
    @Mock
    private INotificationService notificationService;
    @Mock
    private Lock submitBookingDataLock;
    @Mock
    private UserMapper userMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(eventService, "baseMapper", eventMapper);

    }


    @Test
    @DisplayName("Should get attachment for given event ID")
    public void shouldGetAttachment() {
        int eventId = 1;
        EntityAttachmentRelation relation = new EntityAttachmentRelation();
        relation.setAttachmentId(1);
        when(entityAttachmentRelationMapper.selectOne(any())).thenReturn(relation);

        Attachment attachment = new Attachment();
        attachment.setFilePath("path/to/attachment");
        when(attachmentService.getById(anyInt(), anyInt())).thenReturn(attachment);

        String result = eventService.getAttachment(eventId);
        assertEquals("path/to/attachment", result);
    }

    @Test
    @DisplayName("Should create event start and return JSON object")
    public void shouldCreateEventStart() {
        JSONObject data = new JSONObject();
        JSONObject eventData = new JSONObject();
        eventData.put("name", "Test Event");
        data.put("event", eventData);

        JSONArray sessionData = new JSONArray();
        data.put("sessions", sessionData);

        JSONArray posterData = new JSONArray();
        posterData.add("poster1");
        data.put("poster", posterData);

        when(attachmentService.uploadBatchStart(anyInt(), anyList(), anyList(), any())).thenReturn(new JSONObject());

        JSONObject result = eventService.createEventStart(data);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should create event finish and return event ID")
    public void shouldCreateEventFinish() {
        JSONObject requestData = new JSONObject();
        requestData.put("name", "test");

        JSONArray sessionData = new JSONArray();
        JSONObject session1 = new JSONObject();
        session1.put("sessionId", 1);
        sessionData.add(session1);
        requestData.put("eventSessionData", sessionData);

        List<Attachment> attachmentList = new ArrayList<>();
        Attachment attachment = new Attachment();
        attachment.setId(1);
        attachmentList.add(attachment);
        requestData.put("fileInfoList", attachmentList);

        when(eventMapper.insert(any(Event.class))).thenAnswer(invocation -> {
            Event arg = invocation.getArgument(0);
            arg.setId(1);  // Set ID after insert
            return 1;
        });
        doNothing().when(eventService).insertSessions(anyInt(), any(JSONArray.class));

        JSONObject result = eventService.createEventFinish(requestData);
        assertNotNull(result);
        assertEquals(1, result.getInteger("eventId"));
    }

    @Test
    @DisplayName("Should throw exception for invalid event session data")
    public void shouldThrowExceptionForInvalidEventSessionData() {
        int eventId = 1;
        JSONArray sessionData = new JSONArray();

        Exception exception = assertThrows(ServiceException.class, () -> {
            ReflectionTestUtils.invokeMethod(eventService, "insertSessions", eventId, sessionData);
        });

        assertEquals("eventSessionData is null or empty.", exception.getMessage());
    }

    @Test
    @DisplayName("Should get event sessions by event ID")
    public void shouldGetEventSessionsByEventId() {
        int userType = constant_User.USER;
        int eventId = 1;

        Exception exception = assertThrows(ServiceException.class, () -> {
            eventService.getEventSessionsByEventId(userType, null);
        });
        assertEquals("Invalid event id", exception.getMessage());

        when(eventMapper.selectById(eventId)).thenReturn(null);

        exception = assertThrows(ServiceException.class, () -> {
            eventService.getEventSessionsByEventId(userType, 1);
        });
        assertEquals("Event not exist", exception.getMessage());

        Event event = new Event();
        event.setId(eventId);
        event.setStatus(1);
        event.setVisible(false);
        when(eventMapper.selectById(eventId)).thenReturn(event);

        exception = assertThrows(ServiceException.class, () -> {
            eventService.getEventSessionsByEventId(userType, 1);
        });
        assertEquals("Event not exist", exception.getMessage());

        event = new Event();
        event.setId(eventId);
        event.setStatus(1);
        event.setVisible(true);
        when(eventMapper.selectById(eventId)).thenReturn(event);

        List<EventSession> sessionList = new ArrayList<>();
        EventSession session = new EventSession();
        session.setEventId(eventId);
        session.setVisible(true);
        sessionList.add(session);
        when(eventSessionMapper.selectList(any())).thenReturn(sessionList);

        List<EventSession> result = eventService.getEventSessionsByEventId(userType, eventId);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should submit booking data successfully")
    public void shouldSubmitBookingData() {
        int userType = 1;
        String userId = "user123";
        OrderRecord orderRecord = new OrderRecord();
        orderRecord.setEventId(1);
        orderRecord.setEventSessionId(1);

        Event event = new Event();
        event.setId(1);
        event.setStatus(1);
        event.setVisible(true);
        when(eventMapper.selectById(anyInt())).thenReturn(event);

        EventSession eventSession = new EventSession();
        eventSession.setVisible(true);
        eventSession.setEventId(1);
        eventSession.setSeatMapId(1);
        eventSession.setRegistrationRequired(true);
        eventSession.setRegistrationStartTime(LocalDateTime.now().minusDays(1));
        eventSession.setRegistrationEndTime(LocalDateTime.now().plusDays(1));
        eventSession.setCurrentSize(10);
        eventSession.setMaxSize(20);
        when(eventSessionMapper.selectById(anyInt())).thenReturn(eventSession);

        Seat seat = new Seat();
        seat.setSeatMapId(1);
        seat.setSeatId("A1");
        seat.setAvailability(true);
        seat.setPrice(100);
        when(seatMapper.selectOne(any())).thenReturn(seat);

        when(orderRecordMapper.insert(any(OrderRecord.class))).thenReturn(1);

        assertDoesNotThrow(() -> eventService.submitBookingData(userType, userId, orderRecord));
    }

    @Test
    @DisplayName("Should get all events ordered by publish date")
    public void shouldGetAllEvents() {
        List<Event> eventList = new ArrayList<>();
        Event event = new Event();
        event.setId(1);
        eventList.add(event);

        when(eventMapper.selectList(any())).thenReturn(eventList);

        JSONArray result = eventService.getAllEvents();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should change audit status and notify")
    public void shouldChangeAudit() {
        Integer eventId = 1;
        Integer status = 1;
        String reason = "Reason";
        String publisherId = "publisher123";

        Event event = new Event();
        event.setId(eventId);
        when(eventMapper.selectById(eventId)).thenReturn(event);

        doNothing().when(notificationService).insertEventPassNotification(anyString(), anyInt());
//        doNothing().when(notificationService).insertEventNotPassNotification(anyString(), anyInt(), anyString());

        assertDoesNotThrow(() -> eventService.changeAudit(publisherId, eventId, status, reason));
    }

    @Test
    @DisplayName("Should return an empty list if no favorite types are found")
    public void testGetRecommendEvents_NoFavorites() {
        when(userFavoriteTypeMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        List<Event> result = eventService.getRecommendEvents("userId");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return recommended events including user interactions")
    public void testGetRecommendEvents_WithInteractions() {
        List<UserFavoriteType> favoriteTypes = Arrays.asList(new UserFavoriteType("12110141", 1), new UserFavoriteType("12110141", 2));
        Event e1 = new Event();
        e1.setId(1);
        Event e2 = new Event();
        e2.setId(2);
        List<Event> events = Arrays.asList(e1, e2);
        UserInteraction ui1 = new UserInteraction();
        ui1.setEventId(1);
        UserInteraction ui2 = new UserInteraction();
        ui2.setEventId(2);
        List<UserInteraction> interactions = Arrays.asList(ui1, ui2);

        when(userFavoriteTypeMapper.selectList(any())).thenReturn(favoriteTypes);
        when(userInteractionMapper.selectList(any())).thenReturn(interactions);
        // Assume method listByIds and other mocks setup appropriately
        when(eventService.list(any(QueryWrapper.class))).thenReturn(events);

        List<Event> result = eventService.getRecommendEvents("userId");

        assertFalse(result.isEmpty());
        assertEquals(2, result.size()); // Assuming listByIds merges without duplicates
    }

    @Test
    @DisplayName("Should return an empty JSONArray if no events are available")
    public void testGetHotValue_NoEvents() {
        when(eventService.list(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        JSONArray result = eventService.getHotValue();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should calculate and sort events by heat correctly")
    public void testGetHotValue_WithEvents() {
        // Create events and manually set properties
        Event event1 = new Event();
        event1.setId(1);
        event1.setPublishDate(LocalDate.now().minusDays(2).atStartOfDay());
        Event event2 = new Event();
        event2.setId(2);
        event2.setPublishDate(LocalDate.now().minusDays(10).atStartOfDay());
        Event event3 = new Event();
        event3.setId(3);
        event3.setPublishDate(LocalDate.now().minusDays(100).atStartOfDay());

        List<Event> events = Arrays.asList(event1, event2, event3);

        // Create histories and manually set properties
        History history1 = new History();
        history1.setEventId(1);
//        history1.setCount(10); // Assuming History has a count field
        History history2 = new History();
        history2.setEventId(2);
//        history2.setCount(20);
        History history3 = new History();
        history3.setEventId(3);
//        history3.setCount(5);

        List<History> histories = Arrays.asList(history1, history2, history3);

        when(eventMapper.selectList(any())).thenReturn(events);
        when(historyMapper.selectList(any())).thenReturn(histories);

        JSONArray result = eventService.getHotValue();

        assertFalse(result.isEmpty());
        assertEquals(3, result.size());
        // Verify that the first element in the array has the highest heat
//        System.out.println(result);
        assertEquals(3, result.getJSONObject(0).get("id"));  // Assuming JSONObjects include an 'eventId'
        assertTrue(result.getJSONObject(0).getDouble("heat") > result.getJSONObject(1).getDouble("heat"));
    }

    @Test
    @DisplayName("Should return a list of events for a valid publisher ID")
    public void testGetEventByPublisher_ValidId() {
        // Arrange
        Integer publisherId = 1;
        List<Event> expectedEvents = Arrays.asList(new Event(), new Event());
        when(eventMapper.selectList(any())).thenReturn(expectedEvents);

        // Act
        List<Event> events = eventService.getEventByPublisher(1, publisherId);

        // Assert
        assertNotNull(events);
        assertEquals(2, events.size());
        verify(eventMapper).selectList(any());
    }

    @Test
    @DisplayName("Should return an empty list when no IDs are provided")
    public void testGetBatchByIds_EmptyList() {
        // Arrange
        List<Integer> ids = new ArrayList<>();

        // Act
        List<Event> results = eventService.getBatchByIds(1, ids);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Test getAuditList with valid event status")
    public void getAuditList_ValidEventStatus() {
        String eventStatus = "1,2,3";
        when(eventMapper.selectList(any())).thenReturn(new ArrayList<>());

        JSONArray result = eventService.getAuditList(eventStatus);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test getAuditList with null event list")
    public void getAuditList_NullEventList() {
        String eventStatus = "1,2,3";
        when(eventMapper.selectList(any())).thenReturn(null);

        JSONArray result = eventService.getAuditList(eventStatus);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test getAuditList with empty event list")
    public void getAuditList_EmptyEventList() {
        String eventStatus = "1,2,3";
        when(eventMapper.selectList(any())).thenReturn(new ArrayList<>());

        JSONArray result = eventService.getAuditList(eventStatus);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    @Test
@DisplayName("Test getAuditList with valid event sessions")
public void getAuditList_ValidEventSessions() {
    String eventStatus = "1,2,3";
    List<Event> eventList = new ArrayList<>();
    Event event = new Event();
    event.setId(1);
    event.setPublisherId("publisher123");
    eventList.add(event);
    when(eventMapper.selectList(any())).thenReturn(eventList);

    List<EventSession> eventSessionList = new ArrayList<>();
    EventSession eventSession = new EventSession();
    eventSession.setEventId(1);
    eventSession.setStartTime(LocalDateTime.now());
    eventSession.setVenue("Test Venue");
    eventSessionList.add(eventSession);
    when(eventSessionService.list((Wrapper<EventSession>) any())).thenReturn(eventSessionList);

    User user = new User();
    user.setIconId(1);
    when(userMapper.selectById(anyString())).thenReturn(user);

    JSONArray result = eventService.getAuditList(eventStatus);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Test Venue", result.getJSONObject(0).get("location"));
    assertEquals(1, result.getJSONObject(0).get("avatar"));
}
}