package org.cs304.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "User", description = "")
public class User {

    private String id;

    private String name;

    private Integer type;

    private Boolean twoFactorAuthentication;

    private String password;

    private String email;

    private String phoneNumber;

    private String department;

    private Integer iconId;
}
