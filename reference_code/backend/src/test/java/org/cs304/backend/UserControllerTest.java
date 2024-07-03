package org.cs304.backend;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.cs304.backend.constant.constant_User;
import org.cs304.backend.controller.UserController;
import org.cs304.backend.entity.User;
import org.cs304.backend.mapper.UserMapper;
import org.cs304.backend.service.IUserFavoriteTypeService;
import org.cs304.backend.service.IUserService;
import org.cs304.backend.utils.RedisUtil;
import org.cs304.backend.utils.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Arrays;
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
public class UserControllerTest {
    @InjectMocks
    UserController userController;
//    @InjectMocks
//    UserFavoriteTypeControll userController;

    @Mock
    UserMapper userMapper;

    @Mock
    IUserService userService;

    @Mock
    IUserFavoriteTypeService userFavoriteTypeService;

    @Mock
    private MockMvc mockMvc;

    @Mock
    private RedisUtil redisUtil;


    MockHttpServletRequest request;
    MockHttpServletResponse response;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
//        request.setAttribute("loginUserType", 0); // 模拟管理员
    }

    @Test
    @DisplayName("Should return jsonObject when getting userId")
    public void shouldReturnUserFavoriteTypeWhenGettingById() {
        //TODO: 测试getUserFavoriteType
    }

    @Test
    @DisplayName("Should return jsonObject when change favoriteType")
    public void shouldReturnUserFavoriteTypeWhenChangeFavoriteType() {
        //TODO: 测试changeUserFavoriteType
    }

    @Test
    @DisplayName("success - adding a user")
    public void shouldReturnSuccessWhenAddingUser() {
        User user = new User();
        user.setId("10000000");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setType(1);
        user.setName("Test User");

        request.setAttribute("loginUserType", 0); // 模拟管理员


        when(userMapper.insert(user)).thenReturn(1);

        Result result = userController.add(response, request, user);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("success - non-admin add a new user")
    public void shouldReturnErrorWhenNonAdminTriesToAddNewUser() {
        User user = new User();
        user.setId("10000000");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setType(1);
        user.setName("Test User");

        request.setAttribute("loginUserType", 1); // 非管理员

        Result result = userController.add(response, request, user);

        assertEquals("403", result.getCode());
    }

    @Test
    @DisplayName("success - error when user information is incomplete")
    public void shouldReturnErrorWhenUserInformationIsIncomplete() {
        User user = new User();
        user.setId("10000000");
        // 缺少必要的字段
        user.setEmail("test@example.com");
        request.setAttribute("loginUserType", 0); // 模拟管理员
        Result result = userController.add(response, request, user);
        assertEquals("400", result.getCode());
    }

    @Test
    @DisplayName("success - updating own information without restricted fields")
    public void shouldReturnSuccessWhenUpdatingInformation() {
        User user = new User();
        user.setId("10000000");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setType(1);
        user.setName("Test User");
        request.setAttribute("loginUserType", 0); // 模拟管理员
        when(userMapper.insert(user)).thenReturn(1);
        Result result = userController.add(response, request, user);

        user.setName("Test User Change");
        user.setPassword(null);
        user.setEmail(null);
        request.setAttribute("loginUserId", "10000000");
        when(userMapper.updateById(user)).thenReturn(1);
        result = userController.update(response, request, user);
        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return success when updating an admin user")
    public void shouldReturnSuccessWhenUpdateUserAdmin() {
        //TODO: 检查user/update/admin
    }

    @Test
    @DisplayName("Should return success when updating the password")
    public void shouldReturnSuccessWhenUpdatePassword() {
        //TODO: 检查user/update/pass
    }

    @Test
    @DisplayName("success - success when updating the email")
    public void shouldReturnSuccessWhenUpdateEmail() {
        User user = new User();
        user.setId("10000000");
        user.setName("testUser");
        user.setEmail("test@mail.com");
        user.setDepartment("计算机系");
        user.setPassword("11111111");
        when(userMapper.insert(user)).thenReturn(1);
        user.setEmail("testChange@mail.com");
        request.setAttribute("loginUserId", "10000000");
        Result result = userController.updateEmail(response, request, user);

        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("Should return a user when getting a user by id")
    public void shouldReturnUserWhenGettingUserById() {
        User user = new User();
        user.setId("10000000");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setType(1);
        user.setName("Test User");
        request.setAttribute("loginUserType", 0); // 模拟管理员
        when(userMapper.insert(user)).thenReturn(1);
        Result result = userController.add(response, request, user);

        request.setAttribute("loginUserId", "10000000");

        when(userMapper.selectById("10000000")).thenReturn(new User());

        result = userController.get(response, "10000000");
        // 因为没password
        assertEquals("400", result.getCode());
    }


    @Test
    @DisplayName("success - update Email Verify")
    public void testUpdateEmailVerifySuccess() throws Exception {
        User user = new User();
        user.setId("10000000");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setType(1);
        user.setName("Test User");
        request.setAttribute("loginUserType", 0); // 模拟管理员
        when(userMapper.insert(user)).thenReturn(1);
        Result result = userController.add(response, request, user);

        // 模拟 Redis 返回
        Mockito.when(redisUtil.get(Mockito.eq("valid-code"), Mockito.eq(false), Mockito.eq(true)))
                .thenReturn("test@example.com");

        // 创建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.put("id", "10000000");
        requestBody.put("email", "test@example.com");
        requestBody.put("code", "valid-code");
        request.setAttribute("loginUserId", "10000000"); // 模拟本人
        result = userController.updateEmailVerify(response, request, requestBody);
        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("success - update Email Verify with blank data")
    public void testUpdateEmailVerifyBlank() throws Exception {
        User user = new User();
        user.setId("10000000");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setType(1);
        user.setName("Test User");
        request.setAttribute("loginUserType", 0); // 模拟管理员
        when(userMapper.insert(user)).thenReturn(1);
        Result result = userController.add(response, request, user);

        // 模拟 Redis 返回
        Mockito.when(redisUtil.get(Mockito.eq("valid-code"), Mockito.eq(false), Mockito.eq(true)))
                .thenReturn("test@example.com");

        // 创建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.put("id", "10000000");
//        requestBody.put("email", "test@example.com");
        requestBody.put("code", "valid-code");
        request.setAttribute("loginUserId", "10000000"); // 模拟本人
        result = userController.updateEmailVerify(response, request, requestBody);
        assertEquals("401", result.getCode());
    }

    @Test
    @DisplayName("success - update Email Verify with invalid data")
    public void testUpdateEmailVerifyInvalid() throws Exception {
        User user = new User();
        user.setId("10000000");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setType(1);
        user.setName("Test User");
        request.setAttribute("loginUserType", 0); // 模拟管理员
        when(userMapper.insert(user)).thenReturn(1);
        Result result = userController.add(response, request, user);

        // 模拟 Redis 返回
        Mockito.when(redisUtil.get(Mockito.eq("valid-code"), Mockito.eq(false), Mockito.eq(true)))
                .thenReturn("test@example.com");

        // 创建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.put("id", "10000000");
        requestBody.put("email", "test@example.com");
        requestBody.put("code", "invalid-code");
        request.setAttribute("loginUserId", "10000000"); // 模拟本人
        result = userController.updateEmailVerify(response, request, requestBody);
        assertEquals("401", result.getCode());
    }

    @Test
    @DisplayName("Successfully get user favorite type")
    public void testGetUserFavoriteType_Success() {
        // Arrange
        String userId = "12110141";
//        when(request.getAttribute("loginUserType")).thenReturn(1); // Set user type attribute
        request.setAttribute("loginUserType", 1);
        // Stubbing the service method to return user favorite types
        when(userFavoriteTypeService.getAllType(userId)).thenReturn(new JSONArray());

        // Act
        Result result = userController.getUserFavoriteType(request, response, new JSONObject().fluentPut("userId", userId));

        // Assert
        assertNotNull(result);
        assertEquals("200", result.getCode()); // Check response code
        verify(userFavoriteTypeService).getAllType(userId); // Verify that service method was called
    }

    @Test
    @DisplayName("Successfully change user favorite type")
    public void testChangeUserFavoriteType_Success() {
        // Arrange
        String userId = "12110141";
        JSONArray favType = new JSONArray().fluentAdd(1).fluentAdd(2).fluentAdd(3);
//        when(request.getAttribute("loginUserType")).thenReturn(1); // Set user type attribute
        request.setAttribute("loginUserType", 1);

        // Stubbing the service method to return success
        when(userFavoriteTypeService.changeType(userId, favType.toJSONString())).thenReturn(JSONArray.of(true));

        // Act
        Result result = userController.changeUserFavoriteType(request, response, new JSONObject().fluentPut("userId", userId).fluentPut("favType", favType));

        // Assert
        assertNotNull(result);
        assertEquals("200", result.getCode()); // Check response code
        verify(userFavoriteTypeService).changeType(userId, favType.toJSONString()); // Verify that service method was called
    }

    @Test
    @DisplayName("成功保存用户")
    public void testSaveUser_Success() {
        // 准备测试数据
        User user = new User();
        user.setId("12110141");
        user.setDepartment("IT");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setType(1);
        user.setName("Test User");

        // 模拟UserService的getOne方法返回null，表示用户不存在
        when(userService.getOne(any())).thenReturn(null);
        List<User> users = new ArrayList<>();
        users.add(user);
        // 执行被测试的方法
        userController.saveUser(users);

        // 验证是否成功保存了用户
        verify(userService).save(any());
    }

    @Test
    @DisplayName("保存用户时用户已存在")
    public void testSaveUser_UserExists() {
        // 准备测试数据
        User user = new User();
        user.setId("12110141");
        user.setDepartment("IT");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setType(1);
        user.setName("Test User");
        List<User> users = new ArrayList<>();
        users.add(user);
        // 执行被测试的方法
        userController.saveUser(users);
        // 模拟UserService的getOne方法返回一个用户对象，表示用户已存在
        when(userService.getOne(any())).thenReturn(new User());

        // 执行被测试的方法，验证是否会抛出DuplicateKeyException
        assertThrows(DuplicateKeyException.class, () -> userController.saveUser(users));
    }

    @Test
    @DisplayName("成功更新管理员用户")
    public void testUpdateAdmin_Success() {
        // 准备测试数据
        User user = new User();
        user.setId("12110141");
        user.setName("admin");
        user.setPassword("newpassword");
        user.setEmail("admin@example.com");

        // 设置HttpServletRequest中的登录用户类型为管理员
        request.setAttribute("loginUserType", constant_User.ADMIN);

        // 模拟UserService的updateById方法执行成功
        when(userService.updateById(any())).thenReturn(Boolean.TRUE);

        // 执行被测试的方法
        Result result = userController.updateAdmin(response, request, user);

        // 验证是否成功更新管理员用户
        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("非管理员用户无法更新用户信息")
    public void testUpdateAdmin_NonAdminUser() {
        // 准备测试数据
        User user = new User();
        user.setId("12110141");
        user.setName("admin");
        user.setPassword("newpassword");
        user.setEmail("admin@example.com");

        // 设置HttpServletRequest中的登录用户类型为非管理员
        request.setAttribute("loginUserType", constant_User.USER);

        // 执行被测试的方法
        Result result = userController.updateAdmin(response, request, user);

        // 验证是否返回了403错误代码和相应的错误信息
        assertEquals("403", result.getCode());
        assertEquals("Only admin can alter", result.getMsg());
    }

    @Test
    @DisplayName("更新管理员用户失败时返回400错误")
    public void testUpdateAdmin_Failed() {
        // 准备测试数据
        User user = new User();
        user.setId("12110141");
        user.setName("admin");
        user.setPassword("newpassword");
        user.setEmail("admin@example.com");

        // 设置HttpServletRequest中的登录用户类型为管理员
        request.setAttribute("loginUserType", constant_User.ADMIN);

        // 模拟UserService的updateById方法执行失败
        doThrow(new RuntimeException("Update failed")).when(userService).updateById(any());

        // 执行被测试的方法
        Result result = userController.updateAdmin(response, request, user);

        // 验证是否返回了400错误代码和相应的错误信息
        assertEquals("400", result.getCode());
        assertEquals("Alter user failed", result.getMsg());
    }


    @Test
    @DisplayName("成功更新密码")
    public void testUpdatePass_Success() {
        // 准备测试数据
        JSONObject user = new JSONObject();
        user.put("id", "user123");
        user.put("old_password", "oldpass");
        user.put("new_password", "newpass");

        // 设置HttpServletRequest中的登录用户ID
        request.setAttribute("loginUserId", "user123");

        // 模拟UserService的getOne和updateById方法执行成功
        when(userService.getOne(any())).thenReturn(new User());
        when(userService.updateById(any())).thenReturn(Boolean.TRUE);


        // 执行被测试的方法
        Result result = userController.updatePass(response, request, user);

        // 验证是否成功更新密码
        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("更新邮箱失败")
    public void testUpdateEmail_Failed() {
        // 准备测试数据
        User user = new User();
        user.setId("user123");
        user.setEmail("newemail@example.com");

        // 设置HttpServletRequest中的登录用户ID
        request.setAttribute("loginUserId", "user123");

        // 模拟UserService的getOne和sendEmail方法执行成功
        when(userService.getOne(any())).thenReturn(new User());
        doNothing().when(userService).sendEmail(anyString());

        // 执行被测试的方法
        Result result = userController.updateEmail(response, request, user);

        // 验证是否成功更新邮箱
        assertEquals("401", result.getCode());
    }

    @Test
    @DisplayName("成功发送密码重置邮件")
    public void testForgetPass_Success() {
        // 准备测试数据
        User user = new User();
        user.setEmail("user@example.com");

        // 模拟UserService的getOne和sendEmail方法执行成功
        when(userService.getOne(any())).thenReturn(new User());
        doNothing().when(userService).sendEmail(anyString());

        // 执行被测试的方法
        Result result = userController.forgetPass(response, user);

        // 验证是否成功发送密码重置邮件
        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("成功验证忘记密码邮件")
    public void testForgetPassEmailVerify_Success() {
        // 准备测试数据
        JSONObject emailVerify = new JSONObject();
        emailVerify.put("email", "user@example.com");
        emailVerify.put("code", "verificationCode");
        emailVerify.put("password", "newPassword");

        // 模拟RedisUtil的get方法执行成功
        when(redisUtil.get(anyString(), anyBoolean(), anyBoolean())).thenReturn("user@example.com");

        // 模拟UserService的getOne和updateById方法执行成功
        when(userService.getOne(any())).thenReturn(new User());
//        doNothing().when(userService).updateById(any());
        when(userService.updateById(any())).thenReturn(Boolean.TRUE);


        // 执行被测试的方法
        Result result = userController.forgetPassEmailVerify(response, emailVerify);

        // 验证是否成功验证忘记密码邮件
        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("成功验证更新邮箱邮件")
    public void testUpdateEmailVerify_Success() {
        // 准备测试数据
        JSONObject emailVerify = new JSONObject();
        emailVerify.put("email", "newEmail@example.com");
        emailVerify.put("code", "verificationCode");
        emailVerify.put("id", "user123");

        // 设置HttpServletRequest中的登录用户ID
        request.setAttribute("loginUserId", "user123");

        // 模拟RedisUtil的get方法执行成功
        when(redisUtil.get(anyString(), anyBoolean(), anyBoolean())).thenReturn("newEmail@example.com");

        // 模拟UserService的updateById方法执行成功
//        doNothing().when(userService).updateById(any());
        when(userService.updateById(any())).thenReturn(Boolean.TRUE);

        // 执行被测试的方法
        Result result = userController.updateEmailVerify(response, request, emailVerify);

        // 验证是否成功验证更新邮箱邮件
        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("成功删除用户")
    public void testDelete_Success() {
        // 准备测试数据
        String userId = "user123";

        // 设置HttpServletRequest中的登录用户类型
        request.setAttribute("loginUserType", constant_User.ADMIN);

        // 模拟UserService的deleteUser方法执行成功
        doNothing().when(userService).deleteUser(anyString());

        // 执行被测试的方法
        Result result = userController.delete(request, response, userId);

        // 验证是否成功删除用户
        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("成功获取所有用户")
    public void testListAll_Success() {
        // 设置HttpServletRequest中的登录用户类型为管理员
        request.setAttribute("loginUserType", constant_User.ADMIN);

        // 模拟UserService的list方法执行成功并返回用户列表
        when(userService.list()).thenReturn(getMockUserList());

        // 执行被测试的方法
        Result result = userController.listAll(response, request);

        // 验证是否成功获取所有用户，并且过滤掉了密码字段
        assertEquals("200", result.getCode());
        assertNotNull(result.getData());
        assertTrue(result.getData() instanceof List);
        List<User> userList = (List<User>) result.getData();
        assertEquals(2, userList.size());
        assertEquals("1001", userList.get(0).getId());
        assertEquals("user1@example.com", userList.get(0).getEmail());
        assertNull(userList.get(0).getPassword());
        assertEquals("1002", userList.get(1).getId());
        assertEquals("user2@example.com", userList.get(1).getEmail());
        assertNull(userList.get(1).getPassword());
    }

    @Test
    @DisplayName("非管理员无法获取所有用户")
    public void testListAll_Fail_NotAdmin() {
        // 设置HttpServletRequest中的登录用户类型为普通用户
        request.setAttribute("loginUserType", constant_User.USER);

        // 执行被测试的方法
        Result result = userController.listAll(response, request);

        // 验证是否返回了权限错误信息
        assertEquals("403", result.getCode());
        assertEquals("仅管理员可操作", result.getMsg());
    }

    private List<User> getMockUserList() {
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setId("1001");
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        user1.setType(1);
        user1.setIconId(1);

        User user2 = new User();
        user2.setId("1002");
        user2.setEmail("user2@example.com");
        user2.setPassword("password2");
        user2.setType(2);
        user2.setIconId(1);

        userList.add(user1);
        userList.add(user2);

        return userList;
    }

    @Test
@DisplayName("Should delete user successfully when user is admin")
public void shouldDeleteUserSuccessfullyWhenUserIsAdmin() {
    String userId = "testUserId";

    request.setAttribute("loginUserType", constant_User.ADMIN);

    doNothing().when(userService).deleteUser(userId);

    Result result = userController.delete(request, response, userId);

    assertNotNull(result);
    assertEquals("200", result.getCode());
    verify(userService, times(1)).deleteUser(userId);
}

@Test
@DisplayName("Should not delete user when user is not admin")
public void shouldNotDeleteUserWhenUserIsNotAdmin() {
    String userId = "testUserId";

    request.setAttribute("loginUserType", constant_User.USER);

    Result result = userController.delete(request, response, userId);

    assertNotNull(result);
    assertEquals("403", result.getCode());
    verify(userService, times(0)).deleteUser(userId);
}

@Test
@DisplayName("Should return error when exception is thrown during deletion")
public void shouldReturnErrorWhenExceptionIsThrownDuringDeletion() {
    String userId = "testUserId";

    request.setAttribute("loginUserType", constant_User.ADMIN);

    doThrow(new RuntimeException("Delete user failed")).when(userService).deleteUser(userId);

    Result result = userController.delete(request, response, userId);

    assertNotNull(result);
    assertEquals("400", result.getCode());
    assertEquals("Delete user failed", result.getMsg());
}
@Test
@DisplayName("Should return user successfully when user is found")
public void shouldReturnUserSuccessfullyWhenUserIsFound() {
    String userId = "testUserId";
    User user = new User();
    user.setId(userId);
    user.setEmail("test@example.com");
    user.setIconId(1);

    when(userService.getById(userId)).thenReturn(user);

    Result result = userController.get(response, userId);

    assertNotNull(result);
    assertEquals("200", result.getCode());
    assertNull(((User)result.getData()).getPassword());
}
@Test
@DisplayName("Should return user list successfully when user ids are provided")
public void shouldReturnUserListSuccessfullyWhenUserIdsAreProvided() {
    List<String> userIds = Arrays.asList("testUserId1", "testUserId2");

    User user1 = new User();
    user1.setId("testUserId1");
    user1.setEmail("test1@example.com");
    user1.setIconId(1);

    User user2 = new User();
    user2.setId("testUserId2");
    user2.setEmail("test2@example.com");
    user2.setIconId(2);

    List<User> users = Arrays.asList(user1, user2);

    when(userService.listByIds(userIds)).thenReturn(users);

    Result result = userController.list(response, userIds);

    assertNotNull(result);
    assertEquals("200", result.getCode());
    List<User> resultList = (List<User>) result.getData();
    assertEquals(2, resultList.size());
    assertNull(resultList.get(0).getPassword());
    assertNull(resultList.get(1).getPassword());
}

@Test
@DisplayName("Should return error when exception is thrown during listing all users")
public void shouldReturnErrorWhenExceptionIsThrownDuringListingAllUsers() {
    request.setAttribute("loginUserType", constant_User.ADMIN);

    doThrow(new RuntimeException("Get users failed")).when(userService).list();

    Result result = userController.listAll(response, request);

    assertNotNull(result);
    assertEquals("400", result.getCode());
    assertEquals("获取用户失败", result.getMsg());
}
@Test
@DisplayName("Should return error when exception is thrown during listing users")
public void shouldReturnErrorWhenExceptionIsThrownDuringListingUsers() {
    List<String> userIds = Arrays.asList("testUserId1", "testUserId2");

    doThrow(new RuntimeException("Get users failed")).when(userService).listByIds(userIds);

    Result result = userController.list(response, userIds);

    assertNotNull(result);
    assertEquals("400", result.getCode());
    assertEquals("获取用户失败", result.getMsg());
}
@Test
@DisplayName("Should delete users successfully when user is admin")
public void shouldDeleteUsersSuccessfullyWhenUserIsAdmin() {
    List<String> userIds = Arrays.asList("testUserId1", "testUserId2");

    request.setAttribute("loginUserType", constant_User.ADMIN);

    doNothing().when(userService).deleteUsers(userIds);

    Result result = userController.deleteBatch(request, response, userIds);

    assertNotNull(result);
    assertEquals("200", result.getCode());
    verify(userService, times(1)).deleteUsers(userIds);
}

@Test
@DisplayName("Should not delete users when user is not admin")
public void shouldNotDeleteUsersWhenUserIsNotAdmin() {
    List<String> userIds = Arrays.asList("testUserId1", "testUserId2");

    request.setAttribute("loginUserType", constant_User.USER);

    Result result = userController.deleteBatch(request, response, userIds);

    assertNotNull(result);
    assertEquals("403", result.getCode());
    verify(userService, times(0)).deleteUsers(userIds);
}

@Test
@DisplayName("Should return error when exception is thrown during deletion")
public void shouldReturnErrorWhenExceptionIsThrownDuringDeletion2() {
    List<String> userIds = Arrays.asList("testUserId1", "testUserId2");

    request.setAttribute("loginUserType", constant_User.ADMIN);

    doThrow(new RuntimeException("Delete users failed")).when(userService).deleteUsers(userIds);

    Result result = userController.deleteBatch(request, response, userIds);

    assertNotNull(result);
    assertEquals("400", result.getCode());
    assertEquals("删除失败", result.getMsg());
}

@Test
@DisplayName("Should return success when no user ids are provided")
public void shouldReturnSuccessWhenNoUserIdsAreProvided() {
    List<String> userIds = new ArrayList<>();

    request.setAttribute("loginUserType", constant_User.ADMIN);

    Result result = userController.deleteBatch(request, response, userIds);

    assertNotNull(result);
    assertEquals("200", result.getCode());
    verify(userService, times(0)).deleteUsers(userIds);
}


}
