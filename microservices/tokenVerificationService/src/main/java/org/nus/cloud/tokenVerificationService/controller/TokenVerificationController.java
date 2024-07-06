package org.nus.cloud.tokenVerificationService.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.nus.cloud.tokenVerificationService.utils.RedisUtil;
import org.nus.cloud.tokenVerificationService.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class TokenVerificationController {
    @Resource
    private RedisUtil redisUtil;

    @PostMapping("/tokenVerification")
    public Result tokenVerification(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) {
        String token = request.getHeader("token");
        if ((token == null) || (token.isBlank())) {
            token = request.getParameter("token");
        }
        if ((token == null) || (token.isBlank())) {
            return Result.error(response, "401", "Invalid token");
        }
        String fullUserId = request.getHeader("fullUserId");
        if ((fullUserId == null) || (fullUserId.isBlank())) {
            fullUserId = request.getParameter("fullUserId");
        }
        if ((fullUserId == null) || (fullUserId.isBlank())) {
            return Result.error(response, "401", "Invalid fullUserId");
        }
        String dbFullUserId = redisUtil.verifyToken(token, true, false);
        if (dbFullUserId == null || !dbFullUserId.equals(fullUserId)) {
            return Result.error(response, "401", "Verification failed");
        }
        else {
            return Result.success(response);
        }
    }
}