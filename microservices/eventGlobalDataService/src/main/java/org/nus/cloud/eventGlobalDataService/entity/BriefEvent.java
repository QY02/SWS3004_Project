package org.nus.cloud.eventGlobalDataService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BriefEvent {

    @TableId(type = IdType.NONE)
    private Integer id;

    private String publisherFullId;

    private LocalDateTime publishDatetime;

    private String name;

    private Integer detailedDataLocation;
}
