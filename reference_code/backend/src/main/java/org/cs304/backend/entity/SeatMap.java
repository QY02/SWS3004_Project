package org.cs304.backend.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "SeatMap", description = "")
public class SeatMap {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer type;

    private String name;

    private String description;

    private String data;

    @TableField(exist = false)
    private JSONObject detailedData;
}
