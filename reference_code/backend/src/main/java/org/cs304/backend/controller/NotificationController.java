package org.cs304.backend.controller;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cs304.backend.constant.constant_User;
import org.cs304.backend.entity.Event;
import org.cs304.backend.entity.EventSession;
import org.cs304.backend.entity.Notification;
import org.cs304.backend.mapper.EventMapper;
import org.cs304.backend.mapper.EventSessionMapper;
import org.cs304.backend.mapper.NotificationMapper;
import org.cs304.backend.service.INotificationService;
import org.cs304.backend.utils.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @author zyp
 * @date 2024/4/21 0:32
 * @description
 **/
@Slf4j
@RestController
@RequestMapping("/notification")
public class NotificationController {
    @Resource
    private INotificationService notificationService;
    @Resource
    private NotificationMapper notificationMapper;

    @Resource
    private EventSessionMapper eventSessionMapper;
    @Resource
    private EventMapper eventMapper;


    //    @PostMapping("/h")
//    @Operation(summary = "发送申请活动成功通知", description = "传入eventId")
//    public Result h(HttpServletResponse response, HttpServletRequest request) {
////        String userId = (String) request.getAttribute("loginUserId");
//        emailService.sendEmail("2826287819@qq.com","test","test", LocalDateTime.now().plusMinutes(2));
//        return Result.success(response);
//    }
    @PostMapping("/eventPass/{eventId}")
    @Operation(summary = "发送申请活动成功通知", description =
            """
                    ### 参数 ###
                     eventId(Integer): 活动ID
                     ### 返回值 ###
                     无
                     ### 实现逻辑 ###
                     1. 将用户ID和活动ID插入数据库
                     2. 返回成功信息
                    """)
    public Result postEventPass(HttpServletResponse response, HttpServletRequest request, @PathVariable int eventId) {
        String userId = (String) request.getAttribute("loginUserId");
        notificationService.insertEventPassNotification(userId, eventId);
        return Result.success(response);
    }


    @PostMapping("/eventNotPass/{eventId}")
    @Operation(summary = "发送申请活动失败通知", description =
            """
                    传入eventId和反馈comment
                    ### 参数 ###
                    eventId(Integer): 活动ID
                    comment(String): 反馈
                    ### 返回值 ###
                    无
                    ### 实现逻辑 ###
                    1. 将用户ID和活动ID插入数据库
                    2. 返回成功信息
                    """)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("""
            {
              "comment": "反馈"
            }
            """)))
    public Result postEventNotPass(HttpServletResponse response, HttpServletRequest request, @PathVariable int eventId, @RequestBody JSONObject data) {
        String userId = (String) request.getAttribute("loginUserId");
        notificationService.insertEventNotPassNotification(userId, eventId, data.getString("comment"));
        return Result.success(response);
    }

    @PostMapping("/sessions")
    @Operation(summary = "给session list中的每一个订阅用户发送通知", description = """
            传入需要发送通知的场次list和标题、内容
            ### 参数 ###
            selectSession(List<Integer>): 场次ID列表
            title(String): 标题
            content(String): 内容
            ### 返回值 ###
            无
            ### 实现逻辑 ###
            1. 从请求中获取用户ID（loginUserId）。
            2. 从请求中获取场次ID列表（selectSession）。
            3. 从请求中获取标题（title）和内容（content）。
            4. 从数据库中获取场次信息，判断用户是否为该活动的创建者。
            5. 如果是，调用notificationService的insertListOfEventSessionsNotification方法，传入场次ID列表、标题和内容，以给每个订阅用户发送通知。
            6. 返回成功信息。
            """)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("""
            {
              "comment": "取消原因"
            }
            """)))
    public Result postEvenCancel(HttpServletResponse response, HttpServletRequest request, @RequestParam List<Integer> selectSession, @RequestParam String title, @RequestParam String content) {
        String userId = (String) request.getAttribute("loginUserId");
        if (!selectSession.isEmpty()) {
            EventSession session = eventSessionMapper.selectById(selectSession.get(0));
            Event event = eventMapper.selectById(session.getEventId());
            String pid = event.getPublisherId();
            if (!Objects.equals(userId, pid)) {
                return Result.error(response, "只有该活动的创建者才能发送通知");
            }
            notificationService.insertListOfEventSessionsNotification(selectSession, title, content);
            return Result.success(response);
        } else return Result.error(response, "场次为空");

    }


//    @PostMapping("/eventCancel/{eventId}")
//    @Operation(summary = "发送活动取消通知(别用)", description = "传入eventId和取消原因comment")
//    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("""
//            {
//              "comment": "取消原因"
//            }
//            """)))
//    public Result postEvenCancel(HttpServletResponse response, HttpServletRequest request, @PathVariable int eventId, @RequestBody JSONObject data) {
//        String userId = (String) request.getAttribute("loginUserId");
//        notificationService.insertEventCancelNotification(userId, eventId, data.getString("comment"));
//        return Result.success(response);
//    }


//    @PostMapping("/eventModify/{eventId}")
//    @Operation(summary = "发送申请修改通知(别用)", description = "传入eventId")
//    public Result postEventModify(HttpServletResponse response, HttpServletRequest request, @PathVariable int eventId) {
//        String userId = (String) request.getAttribute("loginUserId");
//        notificationService.insertEventModifyNotification(userId, eventId);
//        return Result.success(response);
//    }


    @PostMapping("/reserveSession/{eventSessionId}")
    @Operation(summary = "发送预定场次成功通知", description =
            """
                    传入eventSessionId
                    ### 参数 ###
                    eventSessionId(Integer): 场次ID
                    ### 返回值 ###
                    无
                    ### 实现逻辑 ###
                    1. 将用户ID和活动ID插入数据库
                    2. 返回成功信息
                    """)
    public Result postReserveSession(HttpServletResponse response, HttpServletRequest request, @PathVariable int eventSessionId) {
        String userId = (String) request.getAttribute("loginUserId");
        notificationService.insertReserveSessionNotification(userId, eventSessionId);
        return Result.success(response);
    }


    @PostMapping("/admin/{notifiedUserId}")
    @Operation(summary = "管理员发送通知", description = """
            (1)传入notifiedUserId;\n(2)传入包含title和content的RequestBody
            ### 参数 ###
            notifiedUserId(String): 被通知用户ID
            title(String): 标题
            content(String): 内容
            ### 返回值 ###
            无
            ### 实现逻辑 ###
            1. 从请求中获取用户类型（loginUserType）。这个值应该在用户登录时被设置，并在每个请求中传递。
            2. 检查用户类型是否为0（假设0代表管理员）。如果不是，抛出一个ServiceException异常，表示只有管理员可以执行这个操作。
            3. 从请求中获取用户ID（loginUserId）。
            4. 从请求体中获取标题（title）和内容（content）。
            5. 调用notificationService的insertAdminNotification方法，传入用户ID、被通知用户ID、标题和内容，以发送通知。
            6. 返回成功信息。
            """)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("""
            {
              "title":"标题",
              "content":"内容"
            }""")))
    public Result postAdminNotification(HttpServletResponse response, HttpServletRequest request, @PathVariable String notifiedUserId, @RequestBody JSONObject data) {
        int userType = (int) request.getAttribute("loginUserType");
        if (userType != constant_User.ADMIN) {
            log.error("Only admin can send notification");
            return Result.error(response, "403", "Only admin can send notification");
        }
        String publishId = (String) request.getAttribute("loginUserId");
        String title = data.getString("title");
        String content = data.getString("content");
        notificationService.insertAdminNotification(publishId, notifiedUserId, title, content);
        return Result.success(response);
    }

    @DeleteMapping("/delete/{notificationId}")
    @Operation(summary = "删除指定通知", description =
            """
                    传入notifiedUserId,管理员可以删除任意人的通知，普通用户只能删除自己的通知
                    ### 参数 ###
                    notificationId(String): 通知ID
                    ### 返回值 ###
                    无
                    ### 实现逻辑 ###
                    1. 从请求中获取用户ID（loginUserId）。
                    2. 从请求中获取用户类型（loginUserType）。
                    3. 从数据库中获取通知信息，判断用户是否为通知的接收者。
                    4. 如果是，调用notificationService的deleteNotification方法，传入通知ID，以删除通知。
                    5. 返回成功信息。
                    """)
    public Result deleteMyNotification(HttpServletResponse response, HttpServletRequest request, @PathVariable String notificationId) {
        String uid = (String) request.getAttribute("loginUserId");
        int userType = (int) request.getAttribute("loginUserType");
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            log.error("Notification not exist");
            return Result.error(response, "401", "Notification not exist");
        }
        if (userType != constant_User.ADMIN && !Objects.equals(uid, notification.getNotifiedUserId())) {
            log.error(uid + " is trying to delete others' notification");
            return Result.error(response, "403", "You can only delete your own notification");
        }
        notificationService.deleteNotification(notificationId);
        return Result.success(response);
    }

    @PutMapping("/changeStatus")
    @Operation(summary = "修改通知状态", description =
            """
                    传入notificationId和是否read
                    ### 参数 ###
                    notificationId(Integer): 通知ID
                    read(Boolean): 是否已读
                    ### 返回值 ###
                    无
                    ### 实现逻辑 ###
                    1. 从请求中获取用户ID（loginUserId）。
                    2. 从请求中获取用户类型（loginUserType）。
                    3. 从数据库中获取通知信息，判断用户是否为通知的接收者。
                    4. 如果是，调用notificationService的updateReadStatus方法，传入通知ID和是否已读，以修改通知状态。
                    5. 返回成功信息。
                    """)
    public Result updateReadStatus(HttpServletResponse response, HttpServletRequest request, @RequestParam int notificationId, @RequestParam Boolean read) {
        String uid = (String) request.getAttribute("loginUserId");
        int userType = (int) request.getAttribute("loginUserType");
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            log.error("Notification not exist");
            return Result.error(response, "401", "Notification not exist");
        }
        if (userType != constant_User.ADMIN && !Objects.equals(uid, notification.getNotifiedUserId())) {
            log.error(uid + " is trying to change others' notification's status");
            return Result.error(response, "403", "You can only change your own notification's status");
        }
        notificationService.updateReadStatus(notificationId, read);
        return Result.success(response);
    }

    @PutMapping("/readAll")
    @Operation(summary = "修改自己全部未读通知为已读", description =
            """
                    ### 参数 ###
                    无
                    ### 返回值 ###
                    无
                    ### 实现逻辑 ###
                    1. 从请求中获取用户ID（loginUserId）。
                    2. 调用notificationService的readAll方法，传入用户ID，以修改通知状态。
                    3. 返回成功信息。
                    """)
    public Result readAll(HttpServletResponse response, HttpServletRequest request) {
        String uid = (String) request.getAttribute("loginUserId");
        notificationService.readAll(uid);
        return Result.success(response);
    }

    @GetMapping("/all/{userId}")
    @Operation(summary = "返回指定用户的所有通知", description =
            """
                    ### 参数 ###
                    userId(String): 用户ID
                    ### 返回值 ###
                    通知列表
                    ### 实现逻辑 ###
                    1. 从请求中获取用户ID（loginUserId）。
                    2. 从请求中获取用户类型（loginUserType）。
                    3. 如果用户类型不是0（假设0代表管理员），判断用户ID是否与请求中的用户ID相同。如果不同，抛出一个ServiceException异常，表示只有管理员可以获取别人的通知。
                    4. 调用notificationService的getAllNotificationsOfOneUser方法，传入用户ID，以获取该用户的所有通知。
                    5. 返回通知列表。
                    """)
    public Result getAllNotificationsOfOneUser(HttpServletResponse response, HttpServletRequest request, @PathVariable String userId) {
        String uid = (String) request.getAttribute("loginUserId");
        int userType = (int) request.getAttribute("loginUserType");
        if (userType != constant_User.ADMIN) {
            log.error(uid + " is trying to get others' notifications");
            return Result.error(response, "403", "Only admin can get others' notifications");
        }
        return Result.success(response, notificationService.getAllNotificationsOfOneUser(userId));
    }

    @GetMapping("/all")
    @Operation(summary = "返回我的所有通知", description =
            """
                    ### 参数 ###
                    无
                    ### 返回值 ###
                    通知列表
                    ### 实现逻辑 ###
                    1. 从请求中获取用户ID（loginUserId）。
                    2. 调用notificationService的getAllNotificationsOfOneUser方法，传入用户ID，以获取该用户的所有通知。
                    3. 返回通知列表。
                    """)
    public Result getAllMyNotifications(HttpServletResponse response, HttpServletRequest request) {
        String userId = (String) request.getAttribute("loginUserId");
        return Result.success(response, notificationService.getAllNotificationsOfOneUser(userId));
    }

//    @GetMapping("/page")
//    @Operation(summary = "分页返回我的所有通知")
//    public Result getMyNotificationsByPage(HttpServletResponse response, HttpServletRequest request, @RequestParam int pageNum, @RequestParam int pageSize) {
//        if (pageNum < 0) {
//            return Result.error(response, "pageNum不能为负数");
//        } else if (pageSize <= 0) {
//            return Result.error(response, "pageSize应该为正数");
//        } else {
//            String userId = (String) request.getAttribute("loginUserId");
//            return Result.success(response, notificationService.getNotificationsOfOneUserByPage(userId, pageNum, pageSize));
//        }
//
//    }
}
