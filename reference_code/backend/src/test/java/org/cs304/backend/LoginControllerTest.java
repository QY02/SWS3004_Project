package org.cs304.backend;

import com.alibaba.fastjson2.JSONObject;
import org.cs304.backend.controller.LoginController;
import org.cs304.backend.controller.UserController;
import org.cs304.backend.entity.User;
import org.cs304.backend.exception.ServiceException;
import org.cs304.backend.mapper.UserMapper;
import org.cs304.backend.service.IUserService;
import org.cs304.backend.utils.RedisUtil;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
public class LoginControllerTest {
    MockHttpServletRequest request;

    MockHttpServletResponse response;
    @Mock
    UserMapper userMapper;
    @Mock
    private IUserService userService;

    @Mock
    private RedisUtil redisUtil;
    @InjectMocks
    private UserController userController;
    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("Fail - Invalid Input (Blank ID or Password)")
    public void shouldReturnErrorForInvalidInput() {
        User user = new User();
        user.setId("");
        user.setPassword("");

//        MockHttpServletResponse response = new MockHttpServletResponse();

        Result result = loginController.login(response, user);

        assertEquals("400", result.getCode());
    }

    @Test
    @DisplayName("Fail - Exception on Invalid Credentials")
    public void shouldHandleException() {
        User user = new User();
        user.setId("testUser");
        user.setPassword("testPass");

        when(userService.login(any(User.class))).thenThrow(new RuntimeException("Invalid username or password"));

        Result result = loginController.login(response, user);

        assertEquals("400", result.getCode());
    }

    @Test
    @DisplayName("Fail - Invalid Input (Blank ID or Password)")
    public void shouldReturnErrorForInvalidInput_Register() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        JSONObject data = new JSONObject();
        JSONObject userJson = new JSONObject();
        userJson.put("id", "");
        userJson.put("password", "");
        data.put("user", userJson);

        Result result = loginController.register(response, data);

        assertEquals("400", result.getCode());
    }

    @Test
    @DisplayName("Success - Success on Registration")
    public void shouldHandleRegistrationException() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        JSONObject data = JSONObject.parseObject("{ \"user\": { \"id\": \"testUser\", \"password\": \"testPass\", \"name\": \"John\", \"email\": \"john@example.com\", \"phoneNumber\": \"1234567890\", \"department\": \"IT\", \"twoFactorAuthentication\": true } }");
        doNothing().when(userService).registerSearch(any(User.class));

//        when(userService.registerSearch(any(User.class))).thenThrow(new ServiceException("Invalid registration"));

        Result result = loginController.register(response, data);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Fail - Exception on Email Sending")
    public void shouldHandleEmailException() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        JSONObject data = JSONObject.parseObject("{ \"user\": { \"id\": \"testUser\", \"password\": \"testPass\", \"name\": \"John\", \"email\": \"john@example.com\", \"phoneNumber\": \"1234567890\", \"department\": \"IT\", \"twoFactorAuthentication\": true } }");

        doNothing().when(userService).registerSearch(any(User.class));
        doThrow(new ServiceException("Email service unavailable")).when(userService).sendEmail(anyString());

        Result result = loginController.register(response, data);

        assertEquals("500", result.getCode());
    }

    @Test
    @DisplayName("Success - Complete Registration")
    public void shouldRegisterSuccessfully() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        JSONObject data = JSONObject.parseObject("{ \"user\": { \"id\": \"22222222\", \"password\": \"testPass\", \"name\": \"John\", \"email\": \"john@example.com\", \"phoneNumber\": \"1234567890\", \"department\": \"IT\", \"twoFactorAuthentication\": true } }");

        doNothing().when(userService).registerSearch(any(User.class));
        doNothing().when(userService).sendEmail(anyString());
//        when(redisUtil.add(anyString(), anyString(), anyInt())).thenReturn(true);
        doNothing().when(redisUtil).add(anyString(), anyString(), anyInt());

        Result result = loginController.register(response, data);

        assertEquals("200", result.getCode()); // Assuming Result.SUCCESS is a constant representing success status
    }

    @Test
    public void testTwoFactorAuthentication_True() throws Exception {
        // Mock user data
        User user = new User();
        user.setId("123");
        user.setTwoFactorAuthentication(true);

        // Stubbing the userMapper selectById method
        when(userMapper.selectById(123)).thenReturn(user);

    }

    @Test
    public void testTwoFactorAuthentication_False() throws Exception {
        // Mock user data
        User user = new User();
        user.setId("123");
        user.setTwoFactorAuthentication(false);

        // Stubbing the userMapper selectById method
        when(userMapper.selectById(123)).thenReturn(user);

    }

    @Test
@DisplayName("Should register user error")
public void shouldRegisterUserError() {
    JSONObject emailVerify = new JSONObject();
    emailVerify.put("email", "testEmail");
    emailVerify.put("code", "testCode");

    when(redisUtil.get("testCode", false, true)).thenReturn("testEmail");
    when(redisUtil.get("testEmail", false, false)).thenReturn("{\"user\": \"{\\\"password\\\": \\\"testPassword\\\"}\"}");

    User user = new User();
    user.setPassword("testPassword");
    when(userMapper.insert(any(User.class))).thenReturn(1);

    Result result = loginController.registerEmailVerify(response, emailVerify);

    assertNotNull(result);
    assertEquals("500", result.getCode());
}

@Test
@DisplayName("Should return error when exception is thrown during registration")
public void shouldReturnErrorWhenExceptionIsThrownDuringRegistration() {
    JSONObject emailVerify = new JSONObject();
    emailVerify.put("email", "testEmail");
    emailVerify.put("code", "testCode");

    when(redisUtil.get("testCode", false, true)).thenReturn("testEmail");
    when(redisUtil.get("testEmail", false, false)).thenThrow(new RuntimeException("Registration failed"));

    Result result = loginController.registerEmailVerify(response, emailVerify);

    assertNotNull(result);
    assertEquals("500", result.getCode());
    assertEquals("Registration failed", result.getMsg());
}


}
