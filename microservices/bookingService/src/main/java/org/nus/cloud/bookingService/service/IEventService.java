package org.nus.cloud.bookingService.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.nus.cloud.bookingService.entity.Event;
import org.springframework.transaction.annotation.Transactional;

public interface IEventService extends IService<Event> {
    @Transactional(rollbackFor = {Exception.class})
    JSONObject book(String userRoutingIndex, String fullUserId, JSONObject requestData);
}