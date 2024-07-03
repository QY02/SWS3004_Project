package org.cs304.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Event")
public class Event {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String publisherId;

    private LocalDateTime publishDate;

    private String name;

    private String content;

    private Integer type;

    private Integer status;

    private Integer highestPrice;

    private Integer lowestPrice;

    private String eventPolicy;

    private Boolean visible;

    private String auditRecord;
}
