package org.nus.cloud.loginService.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
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
    public Result login(@NotNull HttpServletResponse response, @RequestBody JSONObject requestData) {
        String fullId = requestData.getString("id");
        String password = requestData.getString("password");
        if (fullId == null || fullId.length() < 16 || password == null) {
            return Result.error(response, "400", "Invalid login information");
        }
        User user = new User();
        user.setId(Integer.parseInt(fullId.substring(8)));
        user.setPassword(password);
        user = userService.login(user);
        return Result.success(response, user);
    }
}