package org.eventCenter.fileServer.config;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.eventCenter.fileServer.utils.RedisUtil;
import org.eventCenter.fileServer.utils.Result;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class TokenInterceptor implements HandlerInterceptor {
    @Resource
    private RedisUtil redisUtil;

    @Value("${admin-token:}")
    private String adminToken;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        if (adminToken.isBlank()) {
            return false;
        }
        String token = request.getHeader("token");
        if ((token == null) || (token.isBlank())) {
            token = request.getParameter("token");
        }
        if (Objects.equals(token, adminToken)) {
            request.setAttribute("admin", true);
            return true;
        }
        String backendData = redisUtil.getAndDelete(token);
        if (backendData != null) {
            request.setAttribute("backendData", backendData);
            return true;
        } else {
            try {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(JSON.toJSONString(Result.error(response, "401", "Invalid token")));
            } catch (IOException e) {
                log.warn("Unable to send response message " + e.getMessage());
                return false;
            }
            return false;
        }
    }
}
