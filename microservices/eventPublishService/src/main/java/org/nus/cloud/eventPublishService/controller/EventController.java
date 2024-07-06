package org.nus.cloud.eventPublishService.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.nus.cloud.eventPublishService.service.IEventService;
import org.nus.cloud.eventPublishService.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class EventController {
    @Resource
    private IEventService eventService;

    @PostMapping("/publishEvent")
    public Result publishEvent(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @RequestBody JSONObject requestData) {
        String fullUserId = request.getHeader("fullUserId");
        if ((fullUserId == null) || (fullUserId.isBlank())) {
            fullUserId = request.getParameter("fullUserId");
        }
        if ((fullUserId == null) || (fullUserId.isBlank())) {
            return Result.error(response, "401", "Invalid fullUserId");
        }
        return Result.success(response, eventService.publishEvent(fullUserId, requestData));
    }
}