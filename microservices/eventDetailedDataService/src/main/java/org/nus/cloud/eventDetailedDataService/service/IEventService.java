package org.nus.cloud.eventDetailedDataService.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.nus.cloud.eventDetailedDataService.entity.Event;

public interface IEventService extends IService<Event> {
    Event getById(JSONObject requestData);
}