package org.cs304.backend.controller;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cs304.backend.service.IUserBlogInteractionService;
import org.cs304.backend.utils.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/blog")
public class UserBlogInteractionController {

    @Resource
    private IUserBlogInteractionService userBlogInteractionService;

    @GetMapping("/get/{commentId}")
    @Operation(summary = "获取一个动态的点赞情况", description =
            """
                    ### 参数 ###
                    commentId(Integer): 评论ID
                    ### 返回值 ###
                    动态的点赞情况
                    ### 实现逻辑 ###
                    1. 根据评论ID查询动态的点赞情况
                    2. 返回动态的点赞情况
                    """)
    public Result getBlog(HttpServletRequest request,HttpServletResponse response, @PathVariable("commentId") Integer commentId) {
        String userID = (String) request.getAttribute("loginUserId");
        JSONObject userBlogInteraction = userBlogInteractionService.getBlog(commentId, userID);
        return Result.success(response, userBlogInteraction);
    }

    @GetMapping("/change/{commentId}/{userId}/{voteType}")
    @Operation(summary = "改变一个动态的点赞情况", description =
            """
                    ### 参数 ###
                    commentId(Integer): 评论ID
                    userId(String): 用户ID
                    voteType(Integer): 点赞类型
                    ### 返回值 ###
                    无
                    ### 实现逻辑 ###
                    1. 改变一个动态的点赞情况
                    2. 返回成功信息
                    """)
    public Result changeVote(HttpServletResponse response, @PathVariable("commentId") Integer commentId, @PathVariable("userId") String userId, @PathVariable("voteType") Integer voteType) {
        userBlogInteractionService.changeVote(commentId, userId, voteType);
        return Result.success(response);
    }

}
