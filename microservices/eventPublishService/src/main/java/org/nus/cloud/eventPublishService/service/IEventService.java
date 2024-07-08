package org.nus.cloud.eventPublishService.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.nus.cloud.eventPublishService.entity.Event;
import org.springframework.transaction.annotation.Transactional;

public interface IEventService extends IService<Event> {
    @Transactional(rollbackFor = {Exception.class})
    JSONObject publishEvent(String fullUserId, JSONObject requestData);
}