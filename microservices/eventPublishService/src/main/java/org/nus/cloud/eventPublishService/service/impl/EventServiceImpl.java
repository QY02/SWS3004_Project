package org.nus.cloud.eventPublishService.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.nus.cloud.eventPublishService.entity.Event;
import org.nus.cloud.eventPublishService.exception.ServiceException;
import org.nus.cloud.eventPublishService.mapper.EventMapper;
import org.nus.cloud.eventPublishService.service.IEventService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class EventServiceImpl extends ServiceImpl<EventMapper, Event> implements IEventService {

    @Override
    public Event publishEvent(String fullUserId, JSONObject requestData) {
        String name = requestData.getString("name");
        if (name == null || name.isBlank()) {
            throw new ServiceException("400", "Invalid event name");
        }
        String content = requestData.getString("content");
        Event event = new Event();
        event.setPublisherFullId(fullUserId);
        event.setPublishDatetime(LocalDateTime.now());
        event.setName(name);
        event.setContent(content);
        baseMapper.insert(event);
        return event;
    }
}