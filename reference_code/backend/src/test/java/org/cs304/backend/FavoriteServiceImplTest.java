package org.cs304.backend;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.cs304.backend.entity.Event;
import org.cs304.backend.entity.Favorite;
import org.cs304.backend.exception.ServiceException;
import org.cs304.backend.mapper.EventMapper;
import org.cs304.backend.mapper.FavoriteMapper;
import org.cs304.backend.service.impl.FavoriteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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
public class FavoriteServiceImplTest {
    @Mock
    private FavoriteMapper favoriteMapper;
    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    @Mock
    EventMapper eventMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(favoriteService, "baseMapper", favoriteMapper);
    }

    @Test
    @DisplayName("Successfully deletes a favorite entry")
    public void testDeleteFavorite_Success() {
        // Arrange
        Favorite favorite = new Favorite();
        favorite.setUserId("user123");
        favorite.setEventId(1);

        // Stubbing the delete operation to return an affected row count
        when(favoriteMapper.delete((Wrapper<Favorite>) any(QueryWrapper.class))).thenReturn(1);

        // Act
        favoriteService.deleteFavorite(favorite);

        // Assert
        verify(favoriteMapper).delete((Wrapper<Favorite>) any(QueryWrapper.class));  // Verify that delete was actually called
    }

    @Test
    @DisplayName("Throws ServiceException when deleting with invalid data")
    public void testDeleteFavorite_InvalidData() {
        // Arrange
        Favorite favorite = new Favorite(); // Missing userId and eventId

        // Act & Assert
        assertThrows(ServiceException.class, () -> favoriteService.deleteFavorite(favorite));
    }

    @Test
    @DisplayName("Successfully retrieves all favorite events for a user")
    public void testGetAllFavorite_Success() {
        // Arrange
        String userId = "user123";
        List<Favorite> favorites = Arrays.asList(new Favorite(userId, 1), new Favorite(userId, 2));
        Event event1 = new Event();
        event1.setId(1);
        Event event2 = new Event();
        event2.setId(2);

        when(favoriteMapper.selectList(any())).thenReturn(favorites);
        when(eventMapper.selectById(1)).thenReturn(event1);
        when(eventMapper.selectById(2)).thenReturn(event2);

        // Act
        JSONArray jsonArray = favoriteService.getAllFavorite(userId);

        // Assert
        assertNotNull(jsonArray);
        assertEquals(2, jsonArray.size()); // Assuming that there are no duplicate eventIds
        verify(eventMapper).selectById(1);
        verify(eventMapper).selectById(2);
    }

    @Test
    @DisplayName("Returns an empty JSONArray when there are no favorites")
    public void testGetAllFavorite_NoFavorites() {
        // Arrange
        String userId = "user123";

        when(favoriteMapper.selectList(any())).thenReturn(Arrays.asList());

        // Act
        JSONArray jsonArray = favoriteService.getAllFavorite(userId);

        // Assert
        assertNotNull(jsonArray);
        assertTrue(jsonArray.isEmpty());
    }

}
