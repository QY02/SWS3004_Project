package org.cs304.backend.controller;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cs304.backend.service.IHistoryService;
import org.cs304.backend.service.IUserInteractionService;
import org.cs304.backend.utils.Result;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/history")
public class HistoryController {
    @Resource
    private IHistoryService historyService;

    @Resource
    private IUserInteractionService userInteractionService;

    @PostMapping("/add")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("""
            {
              "eventId": 0,
              "userId": "12110141"
            }""")))
    @Operation(summary = "添加历史记录",
            description = """
                    ### 参数 ###
                    eventId(Integer): 活动ID
                    userId(String): 用户ID
                    ### 返回值 ###
                    无
                    ### 实现逻辑 ###
                    1. 将用户ID和活动ID插入数据库
                    2. 返回成功信息
                    """)
    public Result addEventHistory(HttpServletResponse response, @RequestBody JSONObject requestBody) {
        String userId = requestBody.getString("userId");
        Integer eventId = requestBody.getInteger("eventId");
//        LocalDateTime visitTime=requestBody.getObject("visit_time");
        historyService.addEventHistory(userId, eventId);
        userInteractionService.changeUserInteraction(userId, eventId, 1, 2);
        return Result.success(response);
    }

    @PostMapping("/getByUserId")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("""
            {
              "userId": "12110141"
            }""")))
    @Operation(summary = "获取用户的历史记录",
            description = """
                    ### 参数 ###
                    userId(String): 用户ID
                    ### 返回值 ###
                    {
                        "code": "200",
                        "msg": "Request Success",
                        "data": [
                            {
                                "eventId": 0,
                                "visitTime": "2024-03-15T19:08:08"
                            }
                        ]
                    }
                    ### 实现逻辑 ###
                    1. 根据用户ID查询数据库，获取用户的历史记录
                    2. 返回结果
                    """)
    public Result getAllHistory(@NotNull HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject requestBody) {
        String userId = requestBody.getString("userId");
        return Result.success(response, historyService.getAllHistory(userId));
    }
}
