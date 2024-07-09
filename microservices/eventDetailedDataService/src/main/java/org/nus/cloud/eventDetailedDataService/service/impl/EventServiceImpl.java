package org.nus.cloud.eventDetailedDataService.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.nus.cloud.eventDetailedDataService.entity.Event;
import org.nus.cloud.eventDetailedDataService.exception.ServiceException;
import org.nus.cloud.eventDetailedDataService.mapper.EventMapper;
import org.nus.cloud.eventDetailedDataService.service.IEventService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventServiceImpl extends ServiceImpl<EventMapper, Event> implements IEventService {

    @Override
    public Event getById(JSONObject requestData) {
        Integer id = requestData.getInteger("id");
        if (id == null) {
            throw new ServiceException("400", "Invalid event id");
        }
        return baseMapper.selectById(id);
    }
}