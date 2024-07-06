package org.nus.cloud.eventListService.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.nus.cloud.eventListService.entity.Event;
import org.nus.cloud.eventListService.mapper.EventMapper;
import org.nus.cloud.eventListService.service.IEventService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class EventServiceImpl extends ServiceImpl<EventMapper, Event> implements IEventService {

    @Override
    public List<Event> getEventList(JSONObject requestData) {
        QueryWrapper<Event> queryWrapper = new QueryWrapper<>();
        Integer id = requestData.getInteger("id");
        String publisherFullId = requestData.getString("publisherFullId");
        LocalDateTime searchTimeStart = requestData.getObject("searchTimeStart", LocalDateTime.class);
        LocalDateTime searchTimeEnd = requestData.getObject("searchTimeEnd", LocalDateTime.class);
        String name = requestData.getString("name");
        String content = requestData.getString("content");
        if (id != null) {
            queryWrapper.eq("id", id);
        }
        if (publisherFullId != null) {
            queryWrapper.eq("publisher_full_id", publisherFullId);
        }
        if (searchTimeStart != null) {
            queryWrapper.ge("publish_datetime", searchTimeStart);
        }
        if (searchTimeEnd != null) {
            queryWrapper.lt("publish_datetime", searchTimeEnd);
        }
        if (name != null) {
            queryWrapper.like("name", name);
        }
        if (content != null) {
            queryWrapper.like("content", content);
        }
        return baseMapper.selectList(queryWrapper);
    }
}