package org.cs304.backend;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.cs304.backend.constant.constant_User;
import org.cs304.backend.controller.SeatMapController;
import org.cs304.backend.entity.SeatMap;
import org.cs304.backend.service.ISeatMapService;
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
import static org.mockito.ArgumentMatchers.anyInt;
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
public class SeatMapControllerTest {
    @InjectMocks
    SeatMapController seatMapController;

    @Mock
    ISeatMapService seatMapService;

    MockHttpServletRequest request;
    MockHttpServletResponse response;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("Test getSeatMapWithSeatsById")
    public void testGetSeatMapWithSeatsById() {
        request.setAttribute("loginUserType", constant_User.USER);
        JSONObject requestBody = new JSONObject();
        requestBody.put("seatMapId", 1);
        when(seatMapService.getSeatMapWithSeatsById(anyInt(), anyInt())).thenReturn(new SeatMap());
        Result result = seatMapController.getSeatMapWithSeatsById(response, request, requestBody);
        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Test getAllSeatMapTemplate")
    public void getAllSeatMapTemplate() {
        when(seatMapService.getAllSeatMapTemplate()).thenReturn(new JSONArray());
        Result result = seatMapController.getAllSeatMapTemplate(response);
        assertEquals("200", result.getCode());
    }
}
