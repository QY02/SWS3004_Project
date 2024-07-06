package org.nus.cloud.eventListService.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.nus.cloud.eventListService.entity.Event;

import java.util.ArrayList;
import java.util.List;

public interface IEventService extends IService<Event> {
    List<Event> getEventList(JSONObject requestData);
}