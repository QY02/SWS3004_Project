package org.cs304.backend.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cs304.backend.entity.Favorite;
import org.cs304.backend.service.IFavoriteService;
import org.cs304.backend.utils.Result;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/favorite")
public class FavoriteController {
    @Resource
    private IFavoriteService favoriteService;

    @PostMapping("/isFavorite")
    @Operation(summary = "判断是否收藏"
            , description =
            """
                    ### 参数 ###
                    userId(String): 用户ID
                    eventId(String): 活动ID
                    ### 返回值 ###
                    1: 存在
                    0: 不存在
                    ### 实现逻辑 ###
                    1. 根据用户ID和活动ID查询数据库，如果存在则返回1，否则返回0
                    2. 返回结果
                    """)
    public Result isFavouriteExists(HttpServletResponse response, @RequestBody Favorite favorite) {
        if (favoriteService.getOne(new QueryWrapper<Favorite>().eq("user_id", favorite.getUserId()).eq("event_id", favorite.getEventId())) != null) {
            return Result.success(response, 1); // 返回数字 1 代表存在
        }
        return Result.success(response, 0); // 返回数字 0 代表不存在
    }


    @PostMapping("/add")
    @Operation(summary = "添加收藏", description =
            """
                    ### 参数 ###
                    userId(String): 用户ID
                    eventId(String): 活动ID
                    ### 返回值 ###
                    无
                    ### 实现逻辑 ###
                    1. 如果数据库中已经存在该收藏记录，则返回错误信息
                    2. 否则，将收藏记录插入数据库
                    3. 返回成功信息
                    """)
    public Result addFavorite(HttpServletResponse response, @RequestBody Favorite favorite) {
//        if (favoriteService.getOne(new QueryWrapper<Favorite>().eq("user_id", favorite.getUserId()).eq("event_id", favorite.getEventId())) != null) {
//            return Result.error(response, "400", "你已经收藏过此活动了"); //  代表存在
//        }
        favoriteService.save(favorite);
        return Result.success(response);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除喜欢", description =
            """
                    ### 参数 ###
                    userId(String): 用户ID
                    eventId(String): 活动ID
                    ### 返回值 ###
                    无
                    ### 实现逻辑 ###
                    1. 根据用户ID和活动ID删除数据库中的收藏记录
                    2. 返回成功信息
                    """)
    public Result delete(HttpServletResponse response, @RequestBody Favorite favorite) {
        favoriteService.deleteFavorite(favorite);
        return Result.success(response);
    }

    @PostMapping("/getByUserId")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("""
            {
              "userId": "12110141"
            }""")))
    @Operation(summary = "获取用户的所有收藏", description =
            """
                    ### 参数 ###
                     userId(String): 用户ID
                     ### 返回值 ###
                     {
                       "code": "200",
                       "msg": "Request Success",
                       "data": [
                         {
                           "id": 1,
                           "userId": "12110141",
                           "eventId": "1",
                           "event": {
                             "id": 1,
                             "title": "活动1",
                             "description": "活动1描述",
                             "startTime": "2024-03-15T19:08:08",
                             "endTime": "2024-03-15T19:08:08",
                             "location": "活动1地点",
                             "type": "活动1类型",
                             "status": 1,
                             "image": "活动1图片",
                             "createTime": "2024-03-15T19:08:08",
                             "updateTime": "2024-03-15T19:08:08"
                           }
                         }
                       ]
                     }
                     ### 实现逻辑 ###
                     1. 根据用户ID查询数据库，获取该用户的所有收藏记录
                     2. 返回结果
                    """)
    public Result getAllFavorite(@NotNull HttpServletResponse response, @RequestBody JSONObject requestBody) {
        String userId = requestBody.getString("userId");
        return Result.success(response, favoriteService.getAllFavorite(userId));
    }
}
