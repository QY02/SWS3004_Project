package org.cs304.backend.config;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cs304.backend.constant.constant_User;
import org.cs304.backend.entity.User;
import org.cs304.backend.service.IUserService;
import org.cs304.backend.utils.RedisUtil;
import org.cs304.backend.utils.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class TokenInterceptor implements HandlerInterceptor {
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private IUserService userService;
    @Value("${user-token.file-server:}")
    private String fileServerUserToken;


    @Override
    public boolean preHandle(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        String token = request.getHeader("token");
        if ((token == null) || (token.isBlank())) {
            token = request.getParameter("token");
        }
        if ((!fileServerUserToken.isBlank()) && (Objects.equals(token, fileServerUserToken))) {
            request.setAttribute("loginUserType", constant_User.FILE_SERVER);
            return true;
        }
        String userId = redisUtil.getInterceptor(token, true, false);
        if (userId != null) {
            User dbUser = userService.getById(userId);
            if (dbUser == null) {
                return false;
            }
            request.setAttribute("loginUserId", userId);
            request.setAttribute("loginUserType", dbUser.getType());
            return true;
        } else {
            try {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(JSON.toJSONString(Result.error(response, "401", "Please login")));
            } catch (IOException e) {
                log.warn("Unable to send response message " + e.getMessage());
                return false;
            }
            return false;
        }
    }
}
