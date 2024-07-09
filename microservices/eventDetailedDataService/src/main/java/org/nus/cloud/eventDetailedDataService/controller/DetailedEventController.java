package org.nus.cloud.eventDetailedDataService.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.nus.cloud.eventDetailedDataService.service.IEventService;
import org.nus.cloud.eventDetailedDataService.service.IEventSessionService;
import org.nus.cloud.eventDetailedDataService.service.ISeatMapService;
import org.nus.cloud.eventDetailedDataService.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/detailedEvent")
public class DetailedEventController {
    @Resource
    private IEventService eventService;

    @Resource
    private IEventSessionService eventSessionService;

    @Resource
    private ISeatMapService seatMapService;

    @PostMapping("/getEventByEventId")
    public Result getEventByEventId(@NotNull HttpServletResponse response, @RequestBody JSONObject requestData) {
        return Result.success(response, eventService.getById(requestData));
    }

    @PostMapping("/getEventSessionListByEventId")
    public Result getEventSessionListByEventId(@NotNull HttpServletResponse response, @RequestBody JSONObject requestData) {
        return Result.success(response, eventSessionService.getListByEventId(requestData));
    }

    @PostMapping("/getEventSessionByEventSessionId")
    public Result getEventSessionByEventSessionId(@NotNull HttpServletResponse response, @RequestBody JSONObject requestData) {
        return Result.success(response, eventSessionService.getById(requestData));
    }

    @PostMapping("/getSeatMapBriefDataBySeatMapId")
    public Result getSeatMapBriefDataBySeatMapId(@NotNull HttpServletResponse response, @RequestBody JSONObject requestData) {
        return Result.success(response, seatMapService.getById(requestData));
    }

    @PostMapping("/getSeatMapDataWithSeatsBySeatMapId")
    public Result getSeatMapDataWithSeatsBySeatMapId(@NotNull HttpServletResponse response, @RequestBody JSONObject requestData) {
        return Result.success(response, seatMapService.getWithSeatsById(requestData));
    }
}