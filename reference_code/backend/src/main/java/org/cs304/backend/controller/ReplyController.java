package org.cs304.backend.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cs304.backend.constant.constant_User;
import org.cs304.backend.entity.Reply;
import org.cs304.backend.mapper.ReplyMapper;
import org.cs304.backend.mapper.UserMapper;
import org.cs304.backend.service.INotificationService;
import org.cs304.backend.utils.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reply")
public class ReplyController {

    @Resource
    private ReplyMapper replyMapper;

    @Resource
    private INotificationService notificationService;

    @Resource
    private UserMapper userMapper;

    @PostMapping("/add")
    @Operation(summary = "添加回复", description =
            """
                    ### 参数 ###
                    commentId(Integer): 评论ID
                    publisherId(String): 发布者ID
                    content(String): 回复内容
                    ### 返回值 ###
                    无
                    ### 实现逻辑 ###
                    1. 将回复插入数据库
                    2. 返回成功信息
                    """)
    public Result addReply(HttpServletResponse response, @RequestBody Reply reply) {
        replyMapper.insert(reply);
        return Result.success(response);
    }

    @GetMapping("/getByComment/{commentId}")
    @Operation(summary = "获取一个评论下的回复", description =
            """
                    ### 参数 ###
                    commentId(Integer): 评论ID
                    ### 返回值 ###
                    回复列表
                    ### 实现逻辑 ###
                    1. 根据评论ID查询回复列表
                    2. 返回回复列表
                    """)
    public Result getReplyByComment(HttpServletResponse response, @PathVariable Integer commentId) {
        List<JSONObject> replyList = replyMapper.selectList(new QueryWrapper<Reply>().eq("comment_id", commentId).orderByDesc("publish_date")).stream().map(reply -> {
            JSONObject jsonObject = (JSONObject) JSON.toJSON(reply);
            jsonObject.put("author", userMapper.selectById(reply.getPublisherId()).getName());
            jsonObject.put("avatar", userMapper.selectById(reply.getPublisherId()).getIconId());
            return jsonObject;
        }).toList();
        return Result.success(response, replyList);
    }

    @DeleteMapping("/delete/{replyId}")
    @Operation(summary = "删除一个回复", description =
            """
                    ### 参数 ###
                    replyId(Integer): 回复ID
                    ### 返回值 ###
                    无
                    ### 实现逻辑 ###
                    1. 删除回复
                    2. 返回成功信息
                    """)
    public Result deleteReply(HttpServletResponse response, @PathVariable Integer replyId) {
        replyMapper.deleteById(replyId);
        return Result.success(response);
    }

    @PostMapping("/deleteByAdmin")
    @Operation(summary = "管理员删除一个回复", description =
            """
                    ### 参数 ###
                    replyId(Integer): 回复ID
                    deleteReason(String): 删除原因
                    ### 返回值 ###
                    无
                    ### 实现逻辑 ###
                    1. 删除回复
                    2. 发送通知
                    3. 返回成功信息
                    """)
    public Result deleteReplyByAdmin(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject jsonObject) {
        Integer replyId = jsonObject.getInteger("replyId");
        String deleteReason = jsonObject.getString("deleteReason");
        int userType = (int) request.getAttribute("loginUserType");
        if (userType != constant_User.ADMIN) {
            return Result.error(response, "403", "Only admin can alter");
        }
        Reply reply = replyMapper.selectById(replyId);
        String adminId = request.getAttribute("loginUserId").toString();
        String title = "您的回复被管理员删除";
        String content = "您的回复 \"" + reply.getContent() + "\" 被管理员删除，原因：" + deleteReason;
        notificationService.insertAdminNotification(adminId, reply.getPublisherId(), title, content);
        replyMapper.deleteById(replyId);
        return Result.success(response);
    }
}
