package org.nus.cloud.bookingService.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.nus.cloud.bookingService.service.IEventService;
import org.nus.cloud.bookingService.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class BookingController {
    @Resource
    private IEventService eventService;

    @PostMapping("/book")
    public Result book(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @RequestBody JSONObject requestData) {
        String userRoutingIndex = request.getHeader("routingIndex");
        if ((userRoutingIndex == null) || (userRoutingIndex.isBlank())) {
            return Result.error(response, "500", "Invalid userRoutingIndex");
        }
        String fullUserId = request.getHeader("fullUserId");
        if ((fullUserId == null) || (fullUserId.isBlank())) {
            fullUserId = request.getParameter("fullUserId");
        }
        if ((fullUserId == null) || (fullUserId.isBlank())) {
            return Result.error(response, "401", "Invalid fullUserId");
        }
        String eventRoutingIndex = request.getHeader("eventRoutingIndex");
        if ((eventRoutingIndex == null) || (eventRoutingIndex.isBlank())) {
            return Result.error(response, "401", "Invalid eventRoutingIndex");
        }
        return Result.success(response, eventService.book(userRoutingIndex, fullUserId, eventRoutingIndex, requestData));
    }
}