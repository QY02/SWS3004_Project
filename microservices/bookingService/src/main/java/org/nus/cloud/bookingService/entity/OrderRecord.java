package org.nus.cloud.bookingService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRecord {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String fullUserId;

    private Integer eventId;

    private Integer detailedDataLocation;

    private Integer eventSessionId;

    private String seatId;

    private String additionalInformation;

    private Integer price;

    private LocalDateTime submitTime;
}
