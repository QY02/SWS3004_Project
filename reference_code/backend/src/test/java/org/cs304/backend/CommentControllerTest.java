package org.cs304.backend;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.cs304.backend.controller.CommentController;
import org.cs304.backend.entity.Comment;
import org.cs304.backend.entity.User;
import org.cs304.backend.mapper.CommentMapper;
import org.cs304.backend.service.ICommentService;
import org.cs304.backend.service.INotificationService;
import org.cs304.backend.service.IUserService;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
public class CommentControllerTest {

    @InjectMocks
    CommentController commentController;

    @Mock
    CommentMapper commentMapper;

    @Mock
    ICommentService commentService;

    @Mock
    INotificationService notificationService;

    @Mock
    IUserService userService;

    MockHttpServletRequest request;
    MockHttpServletResponse response;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("Should return success when posting a new comment")
    public void shouldReturnSuccessWhenPostingNewComment() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setContent("Test content");

        when(commentMapper.insert(comment)).thenReturn(1);

        Result result = commentController.postNewComment(response, comment);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return a comment when getting a comment by id")
    public void shouldReturnCommentWhenGettingCommentById() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setContent("Test content");

        when(commentMapper.selectById(1)).thenReturn(new Comment());

        Result result = commentController.getCommentById(response, 1);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return success when deleting a comment")
    public void shouldReturnSuccessWhenDeletingComment() {


        Result result = commentController.deleteComment(response, 1);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return success when creating a moment")
    public void shouldReturnSuccessWhenCreatingMoment() {
        request.setAttribute("loginUserId", "0");
        JSONObject comment = new JSONObject();
        comment.put("id", 1);
        comment.put("content", "Test content");

        when(commentService.createMomentStart(comment, "0")).thenReturn(new JSONObject());

        Result result = commentController.createMoment(request, response, comment);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return a list of comments when getting event comments")
    public void shouldReturnEventComments() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("eventId", 1);
        jsonObject.put("type", 0);

        when(commentMapper.selectList(new QueryWrapper<Comment>().eq("event_id", 1).eq("type", 0))).thenReturn(new ArrayList<>());

        Result result = commentController.getEventComment(response, jsonObject);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return a moment when getting a moment by id")
    public void shouldReturnMomentWhenGettingMomentById() {
        JSONObject moment = new JSONObject();
        moment.put("id", 1);
        moment.put("content", "Test content");

        when(commentService.getMomentById(1)).thenReturn(moment);

        Result result = commentController.getMomentById(response, 1);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return success when deleting a moment")
    public void shouldReturnSuccessWhenDeletingMoment() {

        Result result = commentController.deleteMoment(response, 1);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return a list of moments when getting all moments")
    public void shouldReturnAllMoments() {
        request.setAttribute("loginUserId", "0");
        when(commentService.getAllMoment(-1, 1, "0")).thenReturn(new ArrayList<>());

        Result result = commentController.getAllMoment(request, response, -1, 1);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return success when an admin deletes a moment")
    public void shouldReturnSuccessWhenAdminDeletesMoment() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("momentId", 1);
        jsonObject.put("deleteReason", "Test reason");

        request.setAttribute("loginUserType", 0);
        request.setAttribute("loginUserId", "0");

        when(commentMapper.selectById(1)).thenReturn(new Comment());
        when(commentMapper.deleteById(1)).thenReturn(1);

        Result result = commentController.deleteReplyByAdmin(request, response, jsonObject);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should get event moments and return list of JSONObjects with correct data")
    public void shouldGetEventMoment() {
        // Arrange
        Integer eventId = 1;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", 1);
        jsonObject.put("eventId", eventId);
        jsonObject.put("type", 1);
        jsonObject.put("content", "Test content");
        jsonObject.put("publisherId", "12112003");
        jsonObject.put("publishDate", "2024-03-15T19:08:08");
        jsonObject.put("replyId", 0);

        List<JSONObject> expectedMomentList = List.of(jsonObject);

        when(commentService.getEventMoment(eventId)).thenReturn(expectedMomentList);

        // Act
        Result result = commentController.getEventMoment(request, response, eventId);

        // Assert
        assertEquals("200", result.getCode());
        assertEquals(expectedMomentList, result.getData());
    }

    @Test
    @DisplayName("Should get event comments and correctly process each comment")
    public void shouldGetEventComment() {
        // Arrange
        Integer eventId = 1;
        Integer type = 1;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("eventId", eventId);
        jsonObject.put("type", type);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setPublisherId("1");

        User user = new User();
        user.setId("1");
        user.setName("Test User");

        when(commentMapper.selectList(any())).thenReturn(List.of(comment));
        when(userService.getById(anyString())).thenReturn(user);

        // Act
        Result result = commentController.getEventComment(response, jsonObject);

        // Assert
        assertEquals("200", result.getCode());
        assertEquals(1, ((List<?>) result.getData()).size());
        JSONObject returnedJsonObject = ((List<JSONObject>) result.getData()).get(0);
        assertEquals(comment.getId(), returnedJsonObject.getInteger("id"));
        assertEquals(user.getName(), returnedJsonObject.getString("publisherNames"));
    }
}