package org.cs304.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Seat", description = "")
public class Seat {

    private Integer seatMapId;

    private String seatId;

    private String type;

    private Boolean availability;

    private Integer price;
}
