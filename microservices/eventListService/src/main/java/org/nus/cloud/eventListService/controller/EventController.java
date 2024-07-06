package org.nus.cloud.eventListService.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.nus.cloud.eventListService.service.IEventService;
import org.nus.cloud.eventListService.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class EventController {
    @Resource
    private IEventService eventService;

    @PostMapping("/getEventList")
    public Result getEventList(@NotNull HttpServletResponse response, @RequestBody JSONObject requestData) {
        return Result.success(response, eventService.getEventList(requestData));
    }
}