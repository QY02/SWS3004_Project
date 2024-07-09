package org.nus.cloud.eventDetailedDataService.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.nus.cloud.eventDetailedDataService.entity.EventSession;
import org.nus.cloud.eventDetailedDataService.exception.ServiceException;
import org.nus.cloud.eventDetailedDataService.mapper.EventSessionMapper;
import org.nus.cloud.eventDetailedDataService.service.IEventSessionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EventSessionServiceImpl extends ServiceImpl<EventSessionMapper, EventSession> implements IEventSessionService {

    @Override
    public List<EventSession> getListByEventId(JSONObject requestData) {
        Integer id = requestData.getInteger("id");
        if (id == null) {
            throw new ServiceException("400", "Invalid event id");
        }
        return baseMapper.selectList(new QueryWrapper<EventSession>().eq("event_id", id));
    }

    @Override
    public EventSession getById(JSONObject requestData) {
        Integer id = requestData.getInteger("id");
        if (id == null) {
            throw new ServiceException("400", "Invalid event session id");
        }
        return baseMapper.selectById(id);
    }
}