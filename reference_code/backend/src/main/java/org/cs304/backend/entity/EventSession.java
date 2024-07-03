package org.cs304.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "EventSession")
public class EventSession {

    @TableId(type = IdType.AUTO)
    private Integer eventSessionId;

    private Integer eventId;

    private Boolean registrationRequired;

    private LocalDateTime registrationStartTime;

    private LocalDateTime registrationEndTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer minSize;

    private Integer maxSize;

    private Integer currentSize;

    private Integer seatMapId;

    private String venue;

    private String location;

    private String additionalInformationRequired;

    private Boolean visible;

    private Integer price;
}
