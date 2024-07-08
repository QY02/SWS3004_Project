package org.nus.cloud.eventGlobalDataService.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.nus.cloud.eventGlobalDataService.entity.SeatMapTemplate;

public interface ISeatMapTemplateService extends IService<SeatMapTemplate> {
    JSONArray getAllSeatMapTemplateName();

    JSONObject getSeatMapTemplateById(JSONObject requestData);
}