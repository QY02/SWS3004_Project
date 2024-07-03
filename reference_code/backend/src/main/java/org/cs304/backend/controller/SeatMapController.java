package org.cs304.backend.controller;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cs304.backend.service.ISeatMapService;
import org.cs304.backend.utils.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seatMap")
public class SeatMapController {
    @Resource
    private ISeatMapService seatMapService;

    @PostMapping("/getSeatMapWithSeatsById")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("{\"seatMapId\": 1}")))
    @Operation(summary = "根据座位图ID获取座位图信息",
            description = """
                    ### 参数 ###
                    seatMapId(Integer): 座位图ID
                    ### 返回值 ###
                    座位图信息
                    ### 实现逻辑 ###
                    1. 根据座位图ID查询座位图信息
                    2. 返回座位图信息
                    """)
    public Result getSeatMapWithSeatsById(HttpServletResponse response, HttpServletRequest request, @RequestBody JSONObject requestBody) {
        int userType = (int) request.getAttribute("loginUserType");
        Integer seatMapId = requestBody.getInteger("seatMapId");
        return Result.success(response, seatMapService.getSeatMapWithSeatsById(userType, seatMapId));
    }

    @GetMapping("/getAllSeatMapTemplate")
    @Operation(summary = "获取所有座位图模板",
            description = """
                    ### 参数 ###
                    无
                    ### 返回值 ###
                    座位图模板列表
                    ### 实现逻辑 ###
                    1. 查询所有座位图模板
                    2. 返回座位图模板列表
                    """)
    public Result getAllSeatMapTemplate(HttpServletResponse response) {
        return Result.success(response, seatMapService.getAllSeatMapTemplate());
    }
}
