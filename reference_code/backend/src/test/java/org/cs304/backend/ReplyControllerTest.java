package org.cs304.backend;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.cs304.backend.controller.ReplyController;
import org.cs304.backend.entity.Reply;
import org.cs304.backend.mapper.ReplyMapper;
import org.cs304.backend.service.INotificationService;
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
public class ReplyControllerTest {

    @InjectMocks
    ReplyController replyController;

    @Mock
    ReplyMapper replyMapper;

    @Mock
    INotificationService service;

    MockHttpServletRequest request;
    MockHttpServletResponse response;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("Should return success when adding a reply")
    public void shouldReturnSuccessWhenAddingReply() {
        Reply reply = new Reply();
        reply.setId(1);
        reply.setContent("Test content");

        when(replyMapper.insert(reply)).thenReturn(1);

        Result result = replyController.addReply(response, reply);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return a list of replies when getting replies by comment id")
    public void shouldReturnRepliesWhenGettingRepliesByCommentId() {
        when(replyMapper.selectList(new QueryWrapper<Reply>().eq("comment_id", 1))).thenReturn(new ArrayList<>());

        Result result = replyController.getReplyByComment(response, 1);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return success when deleting a reply")
    public void shouldReturnSuccessWhenDeletingReply() {
        when(replyMapper.deleteById(1)).thenReturn(1);

        Result result = replyController.deleteReply(response, 1);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return success when an admin deletes a reply")
    public void shouldReturnSuccessWhenAdminDeletesReply() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("replyId", 1);
        jsonObject.put("deleteReason", "Test reason");

        request.setAttribute("loginUserType", 0);
        request.setAttribute("loginUserId", "0");

        when(replyMapper.selectById(1)).thenReturn(new Reply());
        when(replyMapper.deleteById(1)).thenReturn(1);

        Result result = replyController.deleteReplyByAdmin(request, response, jsonObject);

        assertEquals("200", result.getCode());
    }
}