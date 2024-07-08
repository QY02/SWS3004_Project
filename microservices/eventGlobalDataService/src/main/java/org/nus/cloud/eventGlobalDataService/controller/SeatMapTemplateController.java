package org.nus.cloud.eventGlobalDataService.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.nus.cloud.eventGlobalDataService.service.ISeatMapTemplateService;
import org.nus.cloud.eventGlobalDataService.utils.Result;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/seatMapTemplate")
public class SeatMapTemplateController {
    @Resource
    private ISeatMapTemplateService seatMapTemplateService;

    @GetMapping("/getAllSeatMapTemplateName")
    public Result getAllSeatMapTemplateName(@NotNull HttpServletResponse response) {
        return Result.success(response, seatMapTemplateService.getAllSeatMapTemplateName());
    }

    @PostMapping("/getSeatMapTemplateById")
    public Result getSeatMapTemplateById(@NotNull HttpServletResponse response, @RequestBody JSONObject requestData) {
        return Result.success(response, seatMapTemplateService.getSeatMapTemplateById(requestData));
    }
}