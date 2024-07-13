package org.nus.cloud.loginService.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.nus.cloud.loginService.entity.User;
import org.nus.cloud.loginService.service.IUserService;
import org.nus.cloud.loginService.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class LoginController {
    @Resource
    private IUserService userService;

    @PostMapping("/login")
    public Result login(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @RequestBody JSONObject requestData) {
        String fullUserId = request.getHeader("fullUserId");
        String email = request.getHeader("email");
        String password = requestData.getString("password");
        if (((fullUserId == null || fullUserId.length() < 16) && (email == null)) || password == null) {
            return Result.error(response, "400", "Invalid login information");
        }
        User user = new User();
        if ((fullUserId != null) && (fullUserId.length() >= 16)) {
            user.setId(Integer.parseInt(fullUserId.substring(8)));
        } else {
            user.setEmail(email);
        }
        user.setPassword(password);
        user = userService.login(user);
        return Result.success(response, user);
    }
}