package org.cs304.backend;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.cs304.backend.controller.FavoriteController;
import org.cs304.backend.entity.Favorite;
import org.cs304.backend.mapper.FavoriteMapper;
import org.cs304.backend.service.IFavoriteService;
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
public class FavoriteControllerTest {
    @InjectMocks
    FavoriteController favoriteController;

    @Mock
    FavoriteMapper favoriteMapper;

    @Mock
    IFavoriteService favoriteService;

    MockHttpServletRequest request;
    MockHttpServletResponse response;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("success - should return favorite result")
    public void shouldReturnFavoriteResult() {
        Favorite favorite = new Favorite();
        favorite.setUserId("10000000");
        favorite.setEventId(1);

        when(favoriteMapper.selectOne(new QueryWrapper<Favorite>().eq("user_id", "10000000").eq("event_id", 1))).thenReturn(new Favorite());

        Result result = favoriteController.isFavouriteExists(response, favorite);

        assertEquals("200", result.getCode());
    }
}
