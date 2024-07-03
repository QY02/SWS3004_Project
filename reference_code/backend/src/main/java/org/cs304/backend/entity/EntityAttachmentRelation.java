package org.cs304.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "EntityAttachmentRelation", description = "")
public class EntityAttachmentRelation {

    private Integer entityType;

    private Integer entityId;

    private Integer attachmentType;

    private Integer attachmentId;

    private String attachmentTitle;
}
