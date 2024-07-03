package org.cs304.backend.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cs304.backend.constant.constant_User;
import org.cs304.backend.entity.User;
import org.cs304.backend.entity.UserFavoriteType;
import org.cs304.backend.exception.ServiceException;
import org.cs304.backend.mapper.UserFavoriteTypeMapper;
import org.cs304.backend.mapper.UserMapper;
import org.cs304.backend.service.IUserService;
import org.cs304.backend.utils.Encryption;
import org.cs304.backend.utils.RedisUtil;
import org.cs304.backend.utils.Result;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;


/**
 * @author: TaoYiCheng
 * @author: QY
 * @createDate: 2024.3.9
 * @description: 用户登录注册控制器
 */
@Slf4j
@RestController
public class LoginController {
    @Resource
    private IUserService userService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserFavoriteTypeMapper userFavoriteTypeMapper;

    @Resource
    private RedisUtil redisUtil;

    /**
     * 登录
     *
     * @param user 用户信息
     * @return 成功
     */
    @PostMapping("/login")
    @Operation(summary = "登录",
            description = """
                    ### 参数 ###
                    id(String): 用户ID
                    password(String): 用户密码
                    ### 返回值 ###
                    1. 二次验证状态
                    2. 用户信息
                    ### 实现逻辑 ###
                    1. 根据用户ID和密码查询数据库，如果不存在则返回错误信息
                    2. 返回用户信息
                    3. 如果开启了二次验证，则返回二次验证状态
                    """)
    public Result login(@NotNull HttpServletResponse response, @RequestBody User user) {
        try {
            if (StrUtil.isBlank(user.getId()) || StrUtil.isBlank(user.getPassword())) {
                log.error("Invalid Input");
                return Result.error(response, "400", "非法输入");
            }
            user = userService.login(user);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.error(response, "400", "用户名或密码错误");
        }
        if (user.getTwoFactorAuthentication()) {
            return Result.success(response);
        } else return Result.success(response, user);
    }

    /**
     * 二次验证
     *
     * @param user 用户信息(包含id)
     * @return 是否开启二次验证
     */
    @PostMapping("/twoFactorAuthentication")
    @Operation(summary = "二次验证",
            description = """
                    ### 参数 ###
                    id(String): 用户ID
                    ### 返回值 ###
                    1. 二次验证状态
                    ### 实现逻辑 ###
                    1. 根据用户ID查询数据库，返回二次验证状态
                    """)
    public Result twoFactorAuthentication(@NotNull HttpServletResponse response, @RequestBody User user) {
        user = userMapper.selectById(user.getId());
        if (user.getTwoFactorAuthentication()) {
            return Result.success(response, true);
        } else return Result.success(response, false);
    }

    /**
     * 注册
     *
     * @param data 用户信息
     * @return 成功
     */
    @PostMapping("/register")
    @Operation(summary = "注册",
            description = """
                    ### 参数 ###
                    user(Object): 用户信息
                    favType(String): 用户喜好类型
                    ### 返回值 ###
                    无
                    ### 实现逻辑 ###
                    1. 检查用户ID和密码是否为空，如果为空则返回错误信息
                    2. 查询是否重复注册，如果重复注册则返回错误信息
                    3. 发送邮箱验证码
                    """)
    public Result register(@NotNull HttpServletResponse response, @RequestBody JSONObject data) {
        JSONObject userData = data.getJSONObject("user");
//        String favType = data.getString("favType");
//        System.out.println("FAVTYPE:"+favType);

        User user = new User();
        user.setId(userData.getString("id"));
        user.setName(userData.getString("name"));
        user.setEmail(userData.getString("email"));
        user.setPassword(userData.getString("password"));
        user.setPhoneNumber(userData.getString("phoneNumber"));
        user.setDepartment(userData.getString("department"));
        user.setIconId(1);
        user.setTwoFactorAuthentication(userData.getBoolean("twoFactorAuthentication"));

        if (StrUtil.isBlank(user.getId()) || StrUtil.isBlank(user.getPassword())) {
            log.error("Invalid Input");
            return Result.error(response, "400", "非法输入");
        }
        try {
            userService.registerSearch(user);
        } catch (ServiceException e) {
            log.error(e.getMessage());
            return Result.error(response, "400", e.getMessage());
        }
        try {
            userService.sendEmail(user.getEmail());
            user.setTwoFactorAuthentication(false);//暂且设置成false
            redisUtil.add(user.getEmail(), String.valueOf(data), 300);
        } catch (ServiceException e) {
            log.error(e.getMessage());
            return Result.error(response, "500", e.getMessage());
        }
        return Result.success(response);
    }

    /**
     * 验证注册邮箱
     * {
     * "email":"1223@qq.com",
     * "code": "123456"
     * }
     *
     * @param emailVerify 邮箱和验证码
     * @return user 用户信息
     */
    @PostMapping("/registerEmailVerify")
    @Operation(summary = "验证注册邮箱",
            description = """
                    ### 参数 ###
                    email(String): 邮箱
                    code(String): 验证码
                    ### 返回值 ###
                    1. 用户信息
                    ### 实现逻辑 ###
                    1. 检查邮箱和验证码是否为空，如果为空则返回错误信息
                    2. 检查验证码是否正确，如果不正确则返回错误信息
                    3. 将用户信息插入数据库
                    4. 返回用户信息
                    """)
    public Result registerEmailVerify(@NotNull HttpServletResponse response, @RequestBody JSONObject emailVerify) {
        try {
            if (StrUtil.isBlank(emailVerify.getString("email")) || StrUtil.isBlank(emailVerify.getString("code"))) {
                log.error("Invalid Input");
                return Result.error(response, "401", "验证错误，请重试");
            }
            if (!Objects.equals(redisUtil.get(emailVerify.getString("code"), false, true), emailVerify.getString("email"))) {
//                System.out.println("seee  " + redisUtil.get(emailVerify.getString("email"), false, true));
//                System.out.println("seee  "+String.valueOf(JSON.parseObject(redisUtil.get(emailVerify.getString("email"), false, true)).get("user")));
                log.error("Verification error, please try again");
                return Result.error(response, "401", "验证错误，请重试");
            }
            User user;
            user = JSON.parseObject(String.valueOf(JSON.parseObject(redisUtil.get(emailVerify.getString("email"), false, false)).get("user")), User.class);
//            System.out.println(user);
            user.setType(constant_User.USER);
            user.setPassword(Encryption.encrypt(user.getPassword()));
            user.setIconId(1);
            userMapper.insert(user);
            user.setPassword(null);

            String favType = String.valueOf(JSON.parseObject(redisUtil.get(emailVerify.getString("email"), false, true)).get("favType"));
            favType = favType.substring(1, favType.length() - 1);
            // 分割字符串
            String[] strArray = favType.split(",");
            // 创建一个整型数组，长度与字符串数组相同
            for (int i = 0; i < strArray.length; i++) {
                UserFavoriteType userFavoriteType = new UserFavoriteType();
                userFavoriteType.setUserId(user.getId());
                userFavoriteType.setEventType(Integer.parseInt(strArray[i].trim()));
//                System.out.println(userFavoriteType);
                userFavoriteTypeMapper.insert(userFavoriteType);
//                intArray[i] = Integer.parseInt(strArray[i].trim()); // trim()移除可能的空白字符
            }

            return Result.success(response, user);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.error(response, "500", e.getMessage());
        }
    }


    /**
     * 通过邮箱登录
     * {
     * "email":"1223@qq.com",
     * "code": "123456"
     * }
     *
     * @param emailVerify 邮箱和验证码
     * @return user 用户信息
     */
    @PostMapping("/loginWithEmail")
    @Operation(summary = "通过邮箱登录",
            description = """
                    ### 参数 ###
                    email(String): 邮箱
                    code(String): 验证码
                    ### 返回值 ###
                    1. 用户信息
                    ### 实现逻辑 ###
                    1. 检查邮箱和验证码是否为空，如果为空则返回错误信息
                    2. 检查验证码是否正确，如果不正确则返回错误信息
                    3. 返回用户信息
                    """)
    public Result loginWithEmail(@NotNull HttpServletResponse response, @RequestBody JSONObject emailVerify) {
        if (StrUtil.isBlank(emailVerify.getString("email")) || StrUtil.isBlank(emailVerify.getString("code"))) {
            log.error("Invalid Input");
            return Result.error(response, "401", "验证错误，请重试");
        }
        User user = userService.loginWithEmail(emailVerify);
//        user.setPassword(null);
        return Result.success(response, user);
    }

    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱
     * @return 成功或失败
     */
    @PostMapping("/sendEmail/{email}")
    @Operation(summary = "发送邮箱验证码",
            description = """
                    ### 参数 ###
                    email(String): 邮箱
                    ### 返回值 ###
                    无
                    ### 实现逻辑 ###
                    1. 检查邮箱是否为空，如果为空则返回错误信息
                    2. 发送邮箱验证码
                    """)
    public Result sendEmail(HttpServletResponse response, @PathVariable("email") String email) {
        if (StrUtil.isBlank(email)) {
            log.error("Invalid Input");
            return Result.error(response, "400", "无效输入");
        }
        try {
            userService.verifyAndSendEmail(email);
        } catch (ServiceException e) {
            log.error(e.getMessage());
            return Result.error(response, "500", e.getMessage());
        }
        return Result.success(response);
    }

}