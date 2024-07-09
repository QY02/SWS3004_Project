package org.nus.cloud.eventDetailedDataService.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.nus.cloud.eventDetailedDataService.entity.SeatMap;

public interface ISeatMapService extends IService<SeatMap> {

    SeatMap getById(JSONObject requestData);

    SeatMap getWithSeatsById(JSONObject requestData);
}