package org.cs304.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserBlogInteraction", description = "")
public class UserBlogInteraction {

    private String userId;

    private Integer commentId;

    private Boolean voteType;//true:点赞 false:点踩
}
