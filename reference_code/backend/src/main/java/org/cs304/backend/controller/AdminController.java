package org.cs304.backend.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cs304.backend.constant.constant_CommentType;
import org.cs304.backend.constant.constant_EventStatus;
import org.cs304.backend.entity.Comment;
import org.cs304.backend.entity.Event;
import org.cs304.backend.exception.ServiceException;
import org.cs304.backend.mapper.CommentMapper;
import org.cs304.backend.mapper.EventMapper;
import org.cs304.backend.mapper.OrderRecordMapper;
import org.cs304.backend.mapper.UserMapper;
import org.cs304.backend.service.IEventService;
import org.cs304.backend.utils.Result;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private IEventService eventService;

    @Resource
    private EventMapper eventMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private OrderRecordMapper orderRecordMapper;

    @GetMapping("/getAuditList/{eventStatus}")
    @Operation(summary = "获取审核列表",
            description = """
                    ### 参数 ###
                    eventStatus(String): 事件状态列表, 逗号分开, 0表示未审核，1表示审核通过，2表示审核未通过
                    ### 返回值 ###
                    {
                        "code": "200",
                        "msg": "Request Success",
                        "data": [
                          {
                            "content": "活动1的内容",
                            "id": 1,
                            "lowestPrice": 0,
                            "name": "活动1",
                            "publishDate": "2024-03-15T19:08:08",
                            "publisherId": "12112003",
                            "status": 0,
                            "type": 0,
                            "startTime": "2024-03-16T19:18:34",
                            "location": "活动1的地址"
                          }
                        ]
                      }
                    ### 注意事项 ###
                    无
                    ### 实现逻辑 ###
                    1. 从请求中获取用户类型（loginUserType）。这个值应该在用户登录时被设置，并在每个请求中传递。 \s
                    2. 检查用户类型是否为0（假设0代表管理员）。如果不是，抛出一个ServiceException异常，表示只有管理员可以执行这个操作。 \s
                    3. 从请求中获取事件状态列表（eventStatus）。 \s
                    4. 调用eventService的getAuditList方法，传入事件状态列表，以获取符合条件的事件列表。 \s
                    5. 返回一个成功的响应，包含事件列表。
                    """)
    public Result getAuditList(@NotNull HttpServletRequest request, HttpServletResponse response, @PathVariable("eventStatus") String eventStatus) {
        int userType = (int) request.getAttribute("loginUserType");
        if (userType != 0) {
            throw new ServiceException("403", "Only admin can alter");
        }
        return Result.success(response, eventService.getAuditList(eventStatus));
    }


    @PostMapping("/changeAudit")
    @Operation(summary = "修改审核状态",
            description = """
                    ### 参数 ###
                    eventId(Integer): 事件ID
                    status(Integer): 审核状态，0表示未审核，1表示审核通过，2表示审核未通过
                    ### 返回值 ###
                    {
                        "code": "200",
                        "msg": "Request Success"
                    }
                    ### 注意事项 ###
                    无
                    ### 实现逻辑 ###
                    1. 从请求中获取用户类型（loginUserType）。这个值应该在用户登录时被设置，并在每个请求中传递。 \s
                    2. 检查用户类型是否为0（假设0代表管理员）。如果不是，抛出一个ServiceException异常，表示只有管理员可以执行这个操作。 \s
                    3. 从请求中获取用户ID（loginUserId）。这个值也应该在用户登录时被设置，并在每个请求中传递。 \s
                    4. 从请求体中获取事件ID（eventId）、审核状态（status）和原因（reason）。 \s
                    5. 调用eventService的changeAudit方法，传入用户ID、事件ID、审核状态和原因，以改变事件的审核状态。 \s
                    6. 最后，返回一个成功的响应。
                    """)
    public Result changeAudit(@NotNull HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject requestBody) {
        int userType = (int) request.getAttribute("loginUserType");
        if (userType != 0) {
            throw new ServiceException("403", "Only admin can alter");
        }
        String userId = (String) request.getAttribute("loginUserId");
        Integer eventId = requestBody.getInteger("eventId");
        Integer status = requestBody.getInteger("status");
        String reason = requestBody.getString("reason");
        eventService.changeAudit(userId, eventId, status, reason);
        return Result.success(response);
    }

    @GetMapping("/homepage")
    @Operation(summary = "获取首页信息",
            description = """
                    ### 参数 ###
                    无
                    ### 返回值 ###
                    {
                      "code": "200",
                      "msg": "Request Success",
                      "data": {
                        "event": 28,
                        "audit": 4,
                        "user": 12,
                        "comment": 19,
                        "order": 27
                      }
                    }
                    ### 实现逻辑 ###
                    1. 验证用户类型是否为管理员。
                    2. 创建一个新的JSONObject，用于存储返回的数据。
                    3. 从不同的数据源获取数据，包括事件总数、正在审核的事件数、用户总数、博客评论数和订单记录数。
                    4. 将这些数据添加到result对象中。
                    5. 返回一个成功的响应，包含result对象。
                    """)
    public Result getHomepage(@NotNull HttpServletRequest request, HttpServletResponse response) {
        int userType = (int) request.getAttribute("loginUserType");
        if (userType != 0) {
            throw new ServiceException("403", "Only admin can alter");
        }
        JSONObject result = new JSONObject();
        result.put("event", eventMapper.selectCount(null));
        result.put("audit", eventMapper.selectCount(new QueryWrapper<Event>().eq("status", constant_EventStatus.AUDITING)));
        result.put("user", userMapper.selectCount(null));
        result.put("comment", commentMapper.selectCount(new QueryWrapper<Comment>().eq("type", constant_CommentType.BLOG)));
        result.put("order", orderRecordMapper.selectCount(null));
        return Result.success(response, result);
    }
}
