package org.cs304.backend;

import com.alibaba.fastjson2.JSONArray;
import org.cs304.backend.entity.UserFavoriteType;
import org.cs304.backend.mapper.EventMapper;
import org.cs304.backend.mapper.UserFavoriteTypeMapper;
import org.cs304.backend.service.impl.UserFavoriteTypeServiceImpl;
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
public class UserFavoriteTypeImplTest {
    @Mock
    private UserFavoriteTypeMapper userFavoriteTypeMapper;
    @InjectMocks
    private UserFavoriteTypeServiceImpl userFavoriteTypeService;

    @Mock
    EventMapper eventMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(userFavoriteTypeService, "baseMapper", userFavoriteTypeMapper);
    }

    @Test
    @DisplayName("Successfully returns all favorite types for a user")
    public void testGetAllType() {
        // Arrange
        String userId = "user123";
        List<UserFavoriteType> userFavoriteTypes = Arrays.asList(
                new UserFavoriteType(1),
                new UserFavoriteType(2)
        );
        when(userFavoriteTypeMapper.selectList(any())).thenReturn(userFavoriteTypes);

        // Act
        JSONArray result = userFavoriteTypeService.getAllType(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(1));
        assertTrue(result.contains(2));
    }

    @Test
    @DisplayName("Successfully changes user's favorite types")
    public void testChangeType() {
        // Arrange
        String userId = "user123";
        String favTypes = "[1, 2, 3]";

        // Stub the delete and insert operations
        when(userFavoriteTypeMapper.delete(any())).thenReturn(1); // Assume deleting old types affects one row
        when(userFavoriteTypeMapper.insert(any(UserFavoriteType.class))).thenReturn(1); // Each insert affects one row

        // Act
        JSONArray result = userFavoriteTypeService.changeType(userId, favTypes);

        // Assert
        verify(userFavoriteTypeMapper).delete(any());
        verify(userFavoriteTypeMapper, times(3)).insert(any(UserFavoriteType.class)); // Ensure insert is called 3 times
        assertEquals(0, result.size()); // Ensure result is empty as per method logic
    }

}
