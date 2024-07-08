package org.nus.cloud.eventGlobalDataService.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.nus.cloud.eventGlobalDataService.entity.BriefEvent;

import java.util.List;

public interface IBriefEventService extends IService<BriefEvent> {
    List<BriefEvent> getBriefEventList(JSONObject requestData);
}