package org.nus.cloud.eventPublishService.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.nus.cloud.eventPublishService.entity.Event;

import java.util.List;

public interface IEventService extends IService<Event> {
    Event publishEvent(String fullUserId, JSONObject requestData);
}