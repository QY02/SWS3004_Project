package org.nus.cloud.registerService.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.nus.cloud.registerService.service.IUserService;
import org.nus.cloud.registerService.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class RegisterController {
    @Resource
    private IUserService userService;

    @PostMapping("/register")
    public Result register(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @RequestBody JSONObject requestData) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", userService.register(request, requestData));
        return Result.success(response, jsonObject);
    }
}