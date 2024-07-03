package org.cs304.backend;

import com.alibaba.fastjson2.JSONObject;
import org.cs304.backend.controller.UserBlogInteractionController;
import org.cs304.backend.service.IUserBlogInteractionService;
import org.cs304.backend.utils.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

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
public class UserBlogInteractionControllerTest {

    @InjectMocks
    UserBlogInteractionController userBlogInteractionController;

    @Mock
    IUserBlogInteractionService userBlogInteractionService;

    MockHttpServletResponse response;

    MockHttpServletRequest request;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();
    }

    @Test
    @DisplayName("Should return a blog when getting a blog by comment id")
    public void shouldReturnBlogWhenGettingBlogByCommentId() {
        JSONObject blog = new JSONObject();
        blog.put("commentId", 1);

        when(userBlogInteractionService.getBlog(1,"1")).thenReturn(blog);

        Result result = userBlogInteractionController.getBlog(request,response, 1);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return success when changing a vote")
    public void shouldReturnSuccessWhenChangingVote() {

        Result result = userBlogInteractionController.changeVote(response, 1, "0", 1);

        assertEquals("200", result.getCode());
    }
}