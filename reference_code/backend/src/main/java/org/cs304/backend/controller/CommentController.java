package org.cs304.backend.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cs304.backend.constant.constant_User;
import org.cs304.backend.entity.Comment;
import org.cs304.backend.entity.User;
import org.cs304.backend.exception.ServiceException;
import org.cs304.backend.mapper.CommentMapper;
import org.cs304.backend.service.ICommentService;
import org.cs304.backend.service.INotificationService;
import org.cs304.backend.service.IUserService;
import org.cs304.backend.utils.Result;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Resource
    private CommentMapper commentMapper;

    @Resource
    private ICommentService commentService;

    @Resource
    private INotificationService notificationService;

    @Resource
    IUserService userService;


    @PostMapping("/add")
    @Operation(summary = "添加评论", description = """
            ### 参数 ###
            {
                "eventId": "1",
                "type": "0",
                "content": "评论内容",
                "publisherId": "12112003",
                "replyId": "0"
            }
            ### 返回值 ###
            {
                "code": "200",
                "msg": "Request Success",
                "data": null
            }
            ### 实现逻辑 ###
            1. 设置评论的发布时间为当前时间
            2. 插入评论
            3. 返回成功
            """)
    public Result postNewComment(HttpServletResponse response, @RequestBody Comment comment) {
        comment.setPublishDate(LocalDateTime.now());
        commentMapper.insert(comment);
        return Result.success(response);
    }

    @PostMapping("/createMoment")
    @Operation(summary = "创建动态", description = """
            传入comment结构内容，外加fileList，为图片的名称列表
            ### 参数 ###
            {
                "eventId": "1",
                "type": "1",
                "content": "动态内容",
                "publisherId": "12112003",
                "replyId": "0"
            }
            ### 返回值 ###
            {
                "code": "200",
                "msg": "Request Success",
                "data": {
                    "fileUrl": [
                        "file1",
                        "file2"
                    ]
                }
            }
            ### 实现逻辑 ###
            1. 调用commentService的createMomentStart方法，传入comment和userId，获取fileUrl
            2. 返回成功
            """)
    public Result createMoment(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject comment) {
        String userId = request.getAttribute("loginUserId").toString();
        JSONObject fileUrl = commentService.createMomentStart(comment, userId);
        return Result.success(response, fileUrl);
    }


    @PostMapping("/getByEvent")
    @Operation(summary = "获取一个事件下的评论或动态", description = """
            传入eventId和type，type为0时获取评论，为1时获取动态
            ### 参数 ###
            {
                "eventId": "1",
                "type": "0"
            }
            ### 返回值 ###
            {
                "code": "200",
                "msg": "Request Success",
                "data": [
                    {
                        "id": 1,
                        "eventId": 1,
                        "type": 0,
                        "content": "评论内容",
                        "publisherId": "12112003",
                        "publishDate": "2024-03-15T19:08:08",
                        "replyId": 0
                    }
                ]
            }
            ### 实现逻辑 ###
            1. 查询comment表，获取eventId和type相同的评论
            2. 遍历评论，获取发布者的名字
            3. 返回评论列表
            """)
    public Result getEventComment(HttpServletResponse response, @RequestBody JSONObject jsonObject) {
        Integer eventId = jsonObject.getInteger("eventId");
        if (eventId == null) {
            throw new ServiceException("eventId不能为空");
        }
        Integer type = jsonObject.getInteger("type");
        if (type == null) {
            throw new ServiceException("type不能为空");
        }
        List<Comment> commentList = commentMapper.selectList(new QueryWrapper<Comment>().eq("event_id", eventId).eq("type", type));
        List<JSONObject> jsonObjects = new ArrayList<>();

        if (!commentList.isEmpty()) {
            for (Comment comment : commentList) {
                JSONObject object = (JSONObject) JSON.toJSON(comment);
                User user = userService.getById(comment.getPublisherId());
                object.put("publisherNames", user.getName());
                jsonObjects.add(object);
            }
        }
        return Result.success(response, jsonObjects);
    }

    @GetMapping("/getById")
    @Operation(summary = "获取一个评论", description =
            """
                    传入commentId
                    ### 参数 ###
                    commentId(Integer): 评论ID
                    ### 返回值 ###
                    {
                        "code": "200",
                        "msg": "Request Success",
                        "data": {
                            "id": 1,
                            "eventId": 1,
                            "type": 0,
                            "content": "评论内容",
                            "publisherId": "12112003",
                            "publishDate": "2024-03-15T19:08:08",
                            "replyId": 0
                        }
                    }
                    ### 实现逻辑 ###
                    1. 查询comment表，获取commentId对应的评论
                    2. 返回评论
                    """
    )
    public Result getCommentById(HttpServletResponse response, @RequestParam Integer commentId) {
        return Result.success(response, commentMapper.selectById(commentId));
    }

    @GetMapping("/getMomentById")
    @Operation(summary = "获取一个动态", description =
            """
                    传入commentId
                    ### 参数 ###
                    commentId(Integer): 动态ID
                    ### 返回值 ###
                    {
                        "code": "200",
                        "msg": "Request Success",
                        "data": {
                            "id": 1,
                            "eventId": 1,
                            "type": 1,
                            "content": "动态内容",
                            "publisherId": "12112003",
                            "publishDate": "2024-03-15T19:08:08",
                            "replyId": 0
                        }
                    }
                    ### 实现逻辑 ###
                    1. 查询comment表，获取commentId对应的动态
                    2. 返回动态
                    """
    )
    public Result getMomentById(HttpServletResponse response, @RequestParam Integer commentId) {
        JSONObject moment = commentService.getMomentById(commentId);
        return Result.success(response, moment);
    }

    @GetMapping("/delete/{commentId}")
    @Operation(summary = "删除一个评论", description =
            """
                    传入commentId
                    ### 参数 ###
                    commentId(Integer): 评论ID
                    ### 返回值 ###
                    {
                        "code": "200",
                        "msg": "Request Success",
                        "data": null
                    }
                    ### 实现逻辑 ###
                    1. 删除commentId对应的评论
                    2. 返回成功
                    ### 注意事项 ###
                    不判断是否有delete按钮 所以要在前端控制
                    """
    )
    public Result deleteComment(HttpServletResponse response, @PathVariable("commentId") Integer commentId) {
        return Result.success(response, commentMapper.deleteById(commentId));
    }

    @GetMapping("/getMomentBatch/{momentId}/{viewType}")
    @Operation(summary = "获取所有动态", description = """
            传入path的momentId，为之前传递的最后一个momentId，返回20个；第一次传-1。viewType为1时获取所有动态，为2时获取我的动态
            ### 参数 ###
            momentId(Integer): 最后一个momentId
            viewType(Integer): 1为所有动态，2为我的动态
            ### 返回值 ###
            {
                "code": "200",
                "msg": "Request Success",
                "data": [
                    {
                        "id": 1,
                        "eventId": 1,
                        "type": 1,
                        "content": "动态内容",
                        "publisherId": "12112003",
                        "publishDate": "2024-03-15T19:08:08",
                        "replyId": 0
                    }
                ]
            }
            ### 实现逻辑 ###
            1. 调用commentService的getAllMoment方法，传入momentId和viewType，获取动态列表
            2. 返回动态列表
            """
    )
    public Result getAllMoment(HttpServletRequest request, HttpServletResponse response, @PathVariable("momentId") Integer momentId, @PathVariable("viewType") Integer viewType) {
        String userId = request.getAttribute("loginUserId").toString();
        List<JSONObject> momentList = commentService.getAllMoment(momentId, viewType, userId);
        return Result.success(response, momentList);
    }

    @GetMapping("/getEventMoment/{eventId}")
    @Operation(summary = "获取一个事件下的动态"
            , description = """
            传入eventId
            ### 参数 ###
            eventId(Integer): 事件ID
            ### 返回值 ###
            {
                "code": "200",
                "msg": "Request Success",
                "data": [
                    {
                        "id": 1,
                        "eventId": 1,
                        "type": 1,
                        "content": "动态内容",
                        "publisherId": "12112003",
                        "publishDate": "2024-03-15T19:08:08",
                        "replyId": 0
                    }
                ]
            }
            ### 实现逻辑 ###
            1. 查询comment表，获取eventId对应的动态
            2. 返回动态列表
            """
    )
    public Result getEventMoment(HttpServletRequest request, HttpServletResponse response, @PathVariable("eventId") Integer eventId) {
        List<JSONObject> momentList = commentService.getEventMoment(eventId);
        return Result.success(response, momentList);
    }

    @DeleteMapping("/deleteMoment/{momentId}")
    @Operation(summary = "删除一个动态", description =
            """
                    传入momentId
                    ### 参数 ###
                    momentId(Integer): 动态ID
                    ### 返回值 ###
                    {
                        "code": "200",
                        "msg": "Request Success",
                        "data": null
                    }
                    ### 实现逻辑 ###
                    1. 删除momentId对应的动态
                    2. 返回成功
                    ### 注意事项 ###
                    不判断是否有delete按钮 所以要在前端控制
                    """
    )
    public Result deleteMoment(HttpServletResponse response, @PathVariable("momentId") Integer momentId) {
        commentService.deleteMoment(momentId);
        return Result.success(response);
    }

    @PostMapping("/deleteMomentByAdmin")
    @Operation(summary = "管理员删除一个动态", description =
            """
                    传入momentId和deleteReason
                    ### 参数 ###
                    {
                        "momentId": "1",
                        "deleteReason": "删除原因"
                    }
                    ### 返回值 ###
                    {
                        "code": "200",
                        "msg": "Request Success",
                        "data": null
                    }
                    ### 实现逻辑 ###
                    1. 检查用户类型是否为0（假设0代表管理员）。如果不是，抛出一个ServiceException异常，表示只有管理员可以执行这个操作。
                    2. 查询momentId对应的动态
                    3. 发送通知给动态发布者
                    4. 删除momentId对应的动态
                    5. 返回成功
                    """
    )
    public Result deleteReplyByAdmin(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject jsonObject) {
        Integer momentId = jsonObject.getInteger("momentId");
        String deleteReason = jsonObject.getString("deleteReason");
        int userType = (int) request.getAttribute("loginUserType");
        if (userType != constant_User.ADMIN) {
            return Result.error(response, "403", "Only admin can alter");
        }
        Comment moment = commentMapper.selectById(momentId);
        String adminId = request.getAttribute("loginUserId").toString();
        String title = "您的动态被管理员删除";
        String content = "您的动态 \"" + moment.getTitle() + "\" 被管理员删除，原因：" + deleteReason;
        notificationService.insertAdminNotification(adminId, moment.getPublisherId(), title, content);
        commentService.deleteMoment(momentId);
        return Result.success(response);
    }
}
