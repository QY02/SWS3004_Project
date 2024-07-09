package org.nus.cloud.eventDetailedDataService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSession {

    @TableId(type = IdType.AUTO)
    private Integer eventSessionId;

    private Integer eventId;

    private LocalDateTime registrationStartTime;

    private LocalDateTime registrationEndTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer seatMapId;

    private String venue;
}
