package org.nus.cloud.orderRecordService.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.nus.cloud.orderRecordService.entity.OrderRecord;
import org.nus.cloud.orderRecordService.service.IOrderRecordService;
import org.nus.cloud.orderRecordService.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class OrderRecordController {
    @Resource
    private IOrderRecordService orderRecordService;

    @PostMapping("/get")
    public Result get(@NotNull HttpServletResponse response, @RequestBody JSONObject requestData) {
        return Result.success(response, orderRecordService.get(requestData));
    }

    @PostMapping("/add")
    public Result add(@NotNull HttpServletResponse response, @RequestBody OrderRecord orderRecord) {
        return Result.success(response, orderRecordService.add(orderRecord));
    }
}