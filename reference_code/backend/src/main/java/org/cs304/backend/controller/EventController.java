package org.cs304.backend.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cs304.backend.entity.Event;
import org.cs304.backend.entity.EventSession;
import org.cs304.backend.entity.OrderRecord;
import org.cs304.backend.service.IEventService;
import org.cs304.backend.utils.Result;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/event")
public class EventController {

    @Resource
    private IEventService eventService;

    @PostMapping("/add")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("""
            {
             "event":{
             "name": "zyp",
             "content": "",
             "type": 0,
             "publisher_id": "12112003",
             "poster": [ { "url": "https://tdesign.gtimg.com/site/avatar.jpg" } ]
             } ,
             "sessions":[
             { "key": 1, "registration_required": false, "registration_time_range": [], "event_time_range": [ "2024-03-25 11:36:11", "2024-03-31 11:36:11" ], "count_range_of_people": [ "1", "11" ], "seat_map_id": "", "venue": "12", "location": "啊大苏打", "visible": false } ]
             }""")))
    @Operation(summary = "发布新活动", description =
            """
                    ### 参数 ###
                    {
                     "event":{
                     "name": "zyp",
                     "content": "",
                     "type": 0,
                     "publisher_id": "12112003",
                     "poster": [ { "url": "https://tdesign.gtimg.com/site/avatar.jpg" } ]
                     } ,
                     "sessions":[
                     { "key": 1, "registration_required": false, "registration_time_range": [], "event_time_range": [ "2024-03-25 11:36:11", "2024-03-31 11:36:11" ], "count_range_of_people": [ "1", "11" ], "seat_map_id": "", "venue": "12", "location": "啊大苏打", "visible": false } ]
                     }
                    ### 返回值 ###
                    {
                        "code": "200",
                        "msg": "Request Success",
                        "data": {
                            "url": "https://tdesign.gtimg.com/site/avatar.jpg"
                        }
                    }
                    ### 注意事项 ###
                    无
                    ### 实现逻辑 ###
                    1. 通过eventService.createEventStart(event)方法发布新活动
                    2. 返回发布的活动的海报url
                    """)
    public Result postNewEvent(HttpServletResponse response, @RequestBody JSONObject event) {
        JSONObject fileUrl = eventService.createEventStart(event);
//        System.out.println(fileUrl);
        return Result.success(response, fileUrl);
    }

    @PostMapping("/update")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("""
            {
             "event":{
             "name": "zyp",
             "content": "",
             "type": 0,
             "publisher_id": "12112003",
             "poster": [ { "url": "https://tdesign.gtimg.com/site/avatar.jpg" } ]
             } ,
             "sessions":[
             { "key": 1, "registration_required": false, "registration_time_range": [], "event_time_range": [ "2024-03-25 11:36:11", "2024-03-31 11:36:11" ], "count_range_of_people": [ "1", "11" ], "seat_map_id": "", "venue": "12", "location": "啊大苏打", "visible": false } ]
             }""")))
    @Operation(summary = "更新活动", description =
            """
                    ### 参数 ###
                    {
                     "event":{
                     "name": "zyp",
                     "content": "",
                     "type": 0,
                     "publisher_id": "12112003",
                     "poster": [ { "url": "https://tdesign.gtimg.com/site/avatar.jpg" } ]
                     } ,
                     "sessions":[
                     { "key": 1, "registration_required": false, "registration_time_range": [], "event_time_range": [ "2024-03-25 11:36:11", "2024-03-31 11:36:11" ], "count_range_of_people": [ "1", "11" ], "seat_map_id": "", "venue": "12", "location": "啊大苏打", "visible": false } ]
                     }
                    ### 返回值 ###
                    {
                        "code": "200",
                        "msg": "Request Success",
                        "data": {
                            "url": "https://tdesign.gtimg.com/site/avatar.jpg"
                        }
                    }
                    ### 注意事项 ###
                    无
                    ### 实现逻辑 ###
                    1. 通过eventService.updateEventStart(event)方法更新活动
                    2. 返回更新的活动的海报url
                    """)
    public Result updateEvent(HttpServletResponse response, @RequestBody JSONObject event) {
        JSONObject fileUrl = eventService.updateEventStart(event);
//        System.out.println(fileUrl);
        return Result.success(response, fileUrl);
    }

    @PostMapping("/getEventSessionsByEventId")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("{\"eventId\": 1}")))
    @Operation(summary = "获取活动的场次", description =
            """
                    ### 参数 ###
                    {
                        "eventId": 1
                    }
                    ### 返回值 ###
                    {
                        "code": "200",
                        "msg": "Request Success",
                        "data": [
                          {
                            "id": 1,
                            "eventId": 1,
                            "startTime": "2024-03-15T19:08:08",
                            "endTime": "2024-03-15T19:08:08",
                            "location": "活动1的地址",
                            "venue": "活动1的场馆",
                            "visible": false,
                            "registrationRequired": false,
                            "registrationTimeRange": [],
                            "countRangeOfPeople": [
                              1,
                              11
                            ],
                            "seatMapId": 1
                          }
                        ]
                      }
                    ### 注意事项 ###
                    无
                    ### 实现逻辑 ###
                    1. 从请求体中获取活动ID。
                    2. 调用eventService的getEventSessionsByEventId方法，传入活动ID，以获取活动的场次。
                    3. 返回一个成功的响应，包含活动的场次。
                    """)
    public Result getEventSessionsByEventId(HttpServletResponse response, HttpServletRequest request, @RequestBody JSONObject requestBody) {
        int userType = (int) request.getAttribute("loginUserType");
        Integer eventId = requestBody.getInteger("eventId");
        return Result.success(response, eventService.getEventSessionsByEventId(userType, eventId));
    }

    @PostMapping("/submitBookingData")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("""
            {
              "eventId": 0,
              "eventSessionId": 0,
              "seatId": "string",
              "additionalInformation": "string"
            }""")))
    @Operation(summary = "提交预订数据", description =
            """
                    ### 参数 ###
                    {
                      "eventId": 0,
                      "eventSessionId": 0,
                      "seatId": "string",
                      "additionalInformation": "string"
                    }
                    ### 返回值 ###
                    {
                        "code": "200",
                        "msg": "Request Success"
                    }
                    ### 注意事项 ###
                    无
                    ### 实现逻辑 ###
                    1. 从请求中获取用户类型（loginUserType）和用户ID（loginUserId）。
                    2. 从请求体中获取预订数据。
                    3. 调用eventService的submitBookingData方法，传入用户类型、用户ID和预订数据，以提交预订数据。
                    4. 返回一个成功的响应。
                    """)
    public Result submitBookingData(HttpServletResponse response, HttpServletRequest request, @RequestBody OrderRecord orderRecord) {
        int userType = (int) request.getAttribute("loginUserType");
        String userId = (String) request.getAttribute("loginUserId");
        eventService.submitBookingData(userType, userId, orderRecord);
        return Result.success(response);
    }

    @PostMapping("/getAllEvents")
    @Operation(summary = "获取所有活动", description =
            """
            ### 参数 ###
             无
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
                   }
                 ]
               }
             ### 注意事项 ###
             无
                ### 实现逻辑 ###
                1. 从请求中获取用户类型（loginUserType）。这个值应该在用户登录时被设置，并在每个请求中传递。
                2. 检查用户类型是否为-1（假设-1代表未登录）。如果是，抛出一个ServiceException异常，表示只有登录用户可以执行这个操作。
                3. 调用eventService的getAllEvents方法，以获取所有活动列表。
                4. 返回一个成功的响应，包含活动列表。
            """)
    public Result getAllEvents(@NotNull HttpServletRequest request, HttpServletResponse response) {
//        int userType = (int) request.getAttribute("loginUserType");
//        System.out.println(userType+"666666666666666666");
//        if (userType == -1) {
//            throw new ServiceException("403", "Permission denied");
//        }
        return Result.success(response, eventService.getAllEvents());
    }

    @PostMapping("/getRecommendEvents")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("{\"userId\": \"12110141\"}")))
    @Operation(summary = "获取推荐活动", description =
            """
                    ### 参数 ###
                    {
                        "userId": "12110141"
                    }
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
                          }
                        ]
                      }
                    ### 注意事项 ###
                    无
                    ### 实现逻辑 ###
                    1. 从请求体中获取用户ID。
                    2. 调用eventService的getRecommendEvents方法，传入用户ID，以获取推荐活动列表。
                    3. 返回一个成功的响应，包含推荐活动列表。
                    """)
    public Result getRecommendEvents(@NotNull HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject requestBody) {
        return Result.success(response, eventService.getRecommendEvents(requestBody.getString("userId")));
    }

    @PostMapping("/getHotEvents")//按照热度从大大小返回
    @Operation(summary = "获取热门活动", description =
            """
                    ### 参数 ###
                    无
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
                          }
                        ]
                      }
                    ### 注意事项 ###
                    无
                    ### 实现逻辑 ###
                    1. 调用eventService的getHotValue方法，以获取热门活动列表。
                    2. 返回一个成功的响应，包含热门活动列表。
                    """)

    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject()))
    public Result getHotEvents(@NotNull HttpServletRequest request, HttpServletResponse response) {
        return Result.success(response, eventService.getHotValue());
    }


    //获得我发布的活动
    @GetMapping("/getMyPost/{publisherId}")
    @Operation(summary = "获取我发布的活动", description =
            """
                    ### 参数 ###
                    publisherId(Integer): 发布者ID
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
                          }
                        ]
                      }
                    ### 注意事项 ###
                    无
                    ### 实现逻辑 ###
                    1. 从请求中获取用户类型（loginUserType）。这个值应该在用户登录时被设置，并在每个请求中传递。
                    2. 从请求中获取发布者ID（publisherId）。
                    3. 调用eventService的getEventByPublisher方法，传入用户类型和发布者ID，以获取发布者发布的活动列表。
                    4. 返回一个成功的响应，包含发布者发布的活动列表。
                    """)
    public Result getMyPost(@NotNull HttpServletRequest request, HttpServletResponse response, @PathVariable("publisherId") int publisherId) {
//        System.out.println(publisherId+"hhhhhhhhh");
        int userType = (int) request.getAttribute("loginUserType");
        return Result.success(response, eventService.getEventByPublisher(userType, publisherId));
    }

    @GetMapping("/getEventDetail/{eventId}")
    @Operation(summary = "获取活动详情", description =
            """
                    ### 参数 ###
                    eventId(Integer): 活动ID
                    ### 返回值 ###
                    {
                        "code": "200",
                        "msg": "Request Success",
                        "data": {
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
                    }
                    ### 注意事项 ###
                    无
                    ### 实现逻辑 ###
                    1. 从请求中获取活动ID（eventId）。
                    2. 调用eventService的getById方法，传入活动ID，以获取活动详情。
                    3. 返回一个成功的响应，包含活动详情。
                    """)
    public Result getEventDetail(@NotNull HttpServletRequest request, HttpServletResponse response, @PathVariable("eventId") int eventId) {
        QueryWrapper<Event> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", eventId);
        if (!eventService.exists(queryWrapper)) {
            return Result.error(response, "404", "不存在这样的活动");
//            throw new ServiceException("404", "No such event");
        }
        return Result.success(response, eventService.getById(eventId));
    }


    @PostMapping("/getSeatPriceByEventId/{eventId}")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("{\"seatMapId\": 1}")))
    @Operation(summary = "获取一个事件的座位价格", description =
            """
                    ### 参数 ###
                    {
                        "seatMapId": 1
                    }
                    ### 返回值 ###
                    {
                        "code": "200",
                        "msg": "Request Success",
                        "data": {
                            "seatMap": {
                                "id": 1,
                                "name": "座位图1",
                                "seatList": [
                                    {
                                        "id": 1,
                                        "price": 100
                                    }
                                ]
                            }
                        }
                    }
                    ### 注意事项 ###
                    无
                    ### 实现逻辑 ###
                    1. 从请求中获取用户类型（loginUserType）和座位图ID（seatMapId）。
                    2. 调用seatMapService的getSeatMapWithSeatsById方法，传入用户类型和座位图ID，以获取座位图及座位价格。
                    3. 返回一个成功的响应，包含座位图及座位价格。
                    """)
    public Result getSeatPriceByEventId(HttpServletResponse response, HttpServletRequest request, @PathVariable("eventId") int eventId) {
        int userType = (int) request.getAttribute("loginUserType");
        List<EventSession> event = eventService.getEventSessionsByEventId(userType, eventId);

//        int userType = (int) request.getAttribute("loginUserType");
//        Integer seatMapId = requestBody.getInteger("seatMapId");
//        return Result.success(response, seatMapService.getSeatMapWithSeatsById(userType, seatMapId));
        return Result.success(response);
    }

    @GetMapping("/getPhotoById")
    @Operation(summary = "获取一个事件的图片", description =
            """
                    ### 参数 ###
                    eventId(Integer): 事件ID
                    ### 返回值 ###
                    {
                        "code": "200",
                        "msg": "Request Success",
                        "data": "https://tdesign.gtimg.com/site/avatar.jpg"
                    }
                    ### 注意事项 ###
                    无
                    ### 实现逻辑 ###
                    1. 从请求中获取事件ID（eventId）。
                    2. 调用eventService的getAttachment方法，传入事件ID，以获取事件的图片。
                    3. 返回一个成功的响应，包含事件的图片。
                    """)
    public Result getPhotoById(HttpServletResponse response, @RequestParam Integer eventId) {
        String attachmentPath = eventService.getAttachment(eventId);
        return Result.success(response, attachmentPath);
    }
}
