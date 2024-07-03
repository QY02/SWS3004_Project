package org.cs304.backend.entity;

/**
 * @author zyp
 * @date 2024/4/21 0:30
 * @description
 **/
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Notification", description = "Entity representing a notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer status;

    private Integer type;

    private String notifiedUserId;

    private String publisherId;

    private Integer eventSessionId;

    private LocalDateTime createTime;

    private LocalDateTime notifyTime;

    private String title;

    private String content;

}
