package org.cs304.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserInteraction", description = "")
public class UserInteraction {

    private String userId;

    private Integer eventId;

    private Integer updateType;

    private Integer rating;
}
