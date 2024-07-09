package org.nus.cloud.eventDetailedDataService.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatMap {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String data;

    @TableField(exist = false)
    private JSONObject dataJson;

    @TableField(exist = false)
    private JSONObject detailedData;
}
