package org.cs304.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.cs304.backend.entity.UserInteraction;
import org.springframework.transaction.annotation.Transactional;

public interface IUserInteractionService extends IService<UserInteraction> {

    @Transactional(rollbackFor = Exception.class)
    public void changeUserInteraction(String userId, Integer eventId , Integer type, Integer action);
}
