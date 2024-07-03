package org.cs304.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.cs304.backend.entity.Attachment;
import org.cs304.backend.entity.EntityAttachmentRelation;

public interface IEntityAttachmentRelationService extends IService<EntityAttachmentRelation> {

    Attachment getAttachment(int userType,int entity_type, int entity_id, int attachment_type);
}
