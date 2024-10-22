package org.nus.cloud.eventGlobalDataService.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.nus.cloud.eventGlobalDataService.entity.BriefEvent;
import org.nus.cloud.eventGlobalDataService.service.IBriefEventService;
import org.nus.cloud.eventGlobalDataService.utils.Result;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/briefEvent")
public class BriefEventController {
    @Resource
    private IBriefEventService briefEventService;

    @PostMapping("/getList")
    public Result getBriefEventList(@NotNull HttpServletResponse response, @RequestBody JSONObject requestData) {
        return Result.success(response, briefEventService.getBriefEventList(requestData));
    }

    @PostMapping("/add")
    public Result addBriefEvent(@NotNull HttpServletResponse response, @RequestBody BriefEvent briefEvent) {
        try {
            briefEventService.save(briefEvent);
        } catch (DuplicateKeyException e) {
            return Result.error(response, "400", "The event already exists");
        }
        return Result.success(response);
    }

    @RabbitListener(queues = "eventBriefDataQueue-${pod-index}")
    public void addBriefEvent(String briefEventJSONString) {
        BriefEvent briefEvent = JSON.parseObject(briefEventJSONString, BriefEvent.class);
        briefEventService.save(briefEvent);
    }
}