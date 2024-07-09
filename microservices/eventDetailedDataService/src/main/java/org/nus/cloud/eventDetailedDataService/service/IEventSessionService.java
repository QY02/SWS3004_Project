package org.nus.cloud.eventDetailedDataService.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.nus.cloud.eventDetailedDataService.entity.EventSession;

import java.util.List;

public interface IEventSessionService extends IService<EventSession> {

    List<EventSession> getListByEventId(JSONObject requestData);

    EventSession getById(JSONObject requestData);
}