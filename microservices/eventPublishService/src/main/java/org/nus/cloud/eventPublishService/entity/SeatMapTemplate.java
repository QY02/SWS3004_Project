package org.nus.cloud.eventPublishService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatMapTemplate {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String data;
}
