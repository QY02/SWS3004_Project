package org.cs304.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserFavoriteType", description = "")
public class UserFavoriteType {

    private String userId;

    private Integer eventType;

    public UserFavoriteType(int eventType) {
        this.eventType=eventType;
    }
}
