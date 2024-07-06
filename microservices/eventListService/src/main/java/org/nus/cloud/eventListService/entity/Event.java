package org.nus.cloud.eventListService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String publisherFullId;

    private LocalDateTime publishDatetime;

    private String name;

    private String content;
}
