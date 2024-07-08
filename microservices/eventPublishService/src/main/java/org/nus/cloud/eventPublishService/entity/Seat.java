package org.nus.cloud.eventPublishService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    private Integer seatMapId;

    private String seatId;

    private String type;

    private Boolean availability;

    private Integer price;
}
