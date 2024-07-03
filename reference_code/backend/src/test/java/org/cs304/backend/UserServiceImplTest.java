package org.cs304.backend;

/**
 * @author zyp
 * @date 2024/5/23 23:23
 * @description
 **/

import com.alibaba.fastjson2.JSONObject;
import org.cs304.backend.entity.User;
import org.cs304.backend.exception.ServiceException;
import org.cs304.backend.mapper.UserMapper;
import org.cs304.backend.service.impl.UserServiceImpl;
import org.cs304.backend.utils.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
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
public class UserServiceImplTest {

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(userService, "baseMapper", userMapper);

    }

    @Test
    @DisplayName("Should throw ServiceException when login with incorrect credentials")
    public void shouldThrowServiceExceptionWhenLoginWithIncorrectCredentials() {

        User user = new User();
        user.setId("testUserId");
        user.setPassword("wrongPassword");


        when(userMapper.selectOne(any())).thenReturn(null);


        assertThrows(ServiceException.class, () -> userService.login(user));
    }

    @Test
    @DisplayName("Should throw ServiceException when login with deactivated account")
    public void shouldThrowServiceExceptionWhenLoginWithDeactivatedAccount() {

        User user = new User();
        user.setId("testUserId");
        user.setPassword("correctPassword");
        user.setIconId(0);


        when(userMapper.selectOne(any())).thenReturn(user);


        assertThrows(ServiceException.class, () -> userService.login(user));
    }

    @Test
    @DisplayName("Should throw ServiceException when registering with duplicate ID")
    public void shouldThrowServiceExceptionWhenRegisteringWithDuplicateId() {

        User user = new User();
        user.setId("testUserId");
        when(userMapper.selectById(anyString())).thenReturn(new User());


        assertThrows(ServiceException.class, () -> userService.registerSearch(user));
    }

    @Test
    @DisplayName("Should throw ServiceException when registering with duplicate email")
    public void shouldThrowServiceExceptionWhenRegisteringWithDuplicateEmail() {

        User user = new User();
        user.setId("publisherId");
        user.setEmail("test@example.com");
        when(userMapper.selectById(anyString())).thenReturn(user);


        assertThrows(ServiceException.class, () -> userService.registerSearch(user));
    }

    @Test
    @DisplayName("Should throw ServiceException when login with incorrect email verification code")
    public void shouldThrowServiceExceptionWhenLoginWithIncorrectEmailVerificationCode() {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "123456");
        jsonObject.put("email", "test@example.com");


        when(redisUtil.get("123456", false, true)).thenReturn("wrong@example.com");


        assertThrows(ServiceException.class, () -> userService.loginWithEmail(com.alibaba.fastjson2.JSONObject.from(jsonObject)));
    }

    @Test
    @DisplayName("Should throw ServiceException when sending email fails")
    public void shouldThrowServiceExceptionWhenSendingEmailFails() {

        String email = "test@example.com";


        doThrow(new RuntimeException("Sending email failed")).when(mailSender).send(any(SimpleMailMessage.class));


        assertThrows(ServiceException.class, () -> userService.sendEmail(email));
    }

    @Test
    @DisplayName("Should throw ServiceException when user is not found for email verification")
    public void shouldThrowServiceExceptionWhenUserIsNotFoundForEmailVerification() {

        String email = "test@example.com";


        when(userMapper.selectOne(any())).thenReturn(null);


        assertThrows(ServiceException.class, () -> userService.verifyAndSendEmail(email));
    }

    @Test
    @DisplayName("Should throw ServiceException when deleting user with deactivated account")
    public void shouldThrowServiceExceptionWhenDeletingUserWithDeactivatedAccount() {

        String userId = "testUserId";
        User user = new User();
        user.setId(userId);
        user.setIconId(0);


        when(userMapper.selectOne(any())).thenReturn(user);


        assertThrows(ServiceException.class, () -> userService.deleteUser(userId));
    }

    @Test
    @DisplayName("Should throw ServiceException when deleting users with deactivated accounts")
    public void shouldThrowServiceExceptionWhenDeletingUsersWithDeactivatedAccounts() {

        String userId1 = "testUserId1";
        String userId2 = "testUserId2";
        User user1 = new User();
        user1.setId(userId1);
        user1.setIconId(0);
        User user2 = new User();
        user2.setId(userId2);
        user2.setIconId(0);


        when(userMapper.selectOne(any())).thenReturn(user1).thenReturn(user2);


        assertThrows(ServiceException.class, () -> userService.deleteUsers(List.of(userId1, userId2)));
    }
    @Test
@DisplayName("Should delete user successfully")
public void shouldDeleteUserSuccessfully() {
    String userId = "testUserId";
    User user = new User();
    user.setId(userId);
    user.setIconId(1);

    when(userMapper.selectOne(any())).thenReturn(user);

    userService.deleteUser(userId);

    verify(userMapper, times(1)).updateById(any());
}

@Test
@DisplayName("Should throw ServiceException when deleting already deactivated user")
public void shouldThrowServiceExceptionWhenDeletingAlreadyDeactivatedUser() {
    String userId = "testUserId";
    User user = new User();
    user.setId(userId);
    user.setIconId(0);

    when(userMapper.selectOne(any())).thenReturn(user);

    assertThrows(ServiceException.class, () -> userService.deleteUser(userId));
}

@Test
@DisplayName("Should delete multiple users successfully")
public void shouldDeleteMultipleUsersSuccessfully() {
    String userId1 = "testUserId1";
    String userId2 = "testUserId2";
    User user1 = new User();
    user1.setId(userId1);
    user1.setIconId(1);
    User user2 = new User();
    user2.setId(userId2);
    user2.setIconId(1);

    when(userMapper.selectOne(any())).thenReturn(user1).thenReturn(user2);

    userService.deleteUsers(List.of(userId1, userId2));

    verify(userMapper, times(2)).updateById(any());
}

@Test
@DisplayName("Should throw ServiceException when deleting multiple users with at least one already deactivated user")
public void shouldThrowServiceExceptionWhenDeletingMultipleUsersWithAtLeastOneAlreadyDeactivatedUser() {
    String userId1 = "testUserId1";
    String userId2 = "testUserId2";
    User user1 = new User();
    user1.setId(userId1);
    user1.setIconId(1);
    User user2 = new User();
    user2.setId(userId2);
    user2.setIconId(0);

    when(userMapper.selectOne(any())).thenReturn(user1).thenReturn(user2);

    assertThrows(ServiceException.class, () -> userService.deleteUsers(List.of(userId1, userId2)));
}

@Test
@DisplayName("Should send email successfully when user is found and account is active")
public void shouldSendEmailSuccessfullyWhenUserIsFoundAndAccountIsActive() {
    String email = "test@example.com";
    User user = new User();
    user.setEmail(email);
    user.setIconId(1);

    when(userMapper.selectOne(any())).thenReturn(user);

    userService.verifyAndSendEmail(email);

}

@Test
@DisplayName("Should throw ServiceException when user is not found")
public void shouldThrowServiceExceptionWhenUserIsNotFound() {
    String email = "test@example.com";

    when(userMapper.selectOne(any())).thenReturn(null);

    assertThrows(ServiceException.class, () -> userService.verifyAndSendEmail(email));
}

@Test
@DisplayName("Should throw ServiceException when user account is already deactivated")
public void shouldThrowServiceExceptionWhenUserAccountIsAlreadyDeactivated() {
    String email = "test@example.com";
    User user = new User();
    user.setEmail(email);
    user.setIconId(0);

    when(userMapper.selectOne(any())).thenReturn(user);

    assertThrows(ServiceException.class, () -> userService.verifyAndSendEmail(email));
}
@Test
@DisplayName("Should login with email successfully when verification code is correct and account is active")
public void shouldLoginWithEmailSuccessfullyWhenVerificationCodeIsCorrectAndAccountIsActive() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("code", "123456");
    jsonObject.put("email", "test@example.com");
    User user = new User();
    user.setEmail("test@example.com");
    user.setIconId(1);

    when(redisUtil.get("123456", false, true)).thenReturn("test@example.com");
    when(userMapper.selectOne(any())).thenReturn(user);
    when(redisUtil.generateToken(user)).thenReturn(user);

    User result = userService.loginWithEmail(jsonObject);
    assertNotNull(result);
}

@Test
@DisplayName("Should throw ServiceException when verification code is incorrect")
public void shouldThrowServiceExceptionWhenVerificationCodeIsIncorrect() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("code", "123456");
    jsonObject.put("email", "test@example.com");

    when(redisUtil.get("123456", false, true)).thenReturn("wrong@example.com");

    assertThrows(ServiceException.class, () -> userService.loginWithEmail(jsonObject));
}

@Test
@DisplayName("Should throw ServiceException when registering with duplicate email")
public void shouldThrowServiceExceptionWhenRegisteringWithDuplicateEmail2() {
    User user = new User();
    user.setId("testUserId");
    user.setEmail("test@example.com");

    when(userMapper.selectOne(any())).thenReturn(user);

    assertThrows(ServiceException.class, () -> userService.registerSearch(user));
}

}

