package org.nus.cloud.eventGlobalDataService.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.nus.cloud.eventGlobalDataService.entity.BriefEvent;
import org.nus.cloud.eventGlobalDataService.mapper.BriefEventMapper;
import org.nus.cloud.eventGlobalDataService.service.IBriefEventService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class BriefEventServiceImpl extends ServiceImpl<BriefEventMapper, BriefEvent> implements IBriefEventService {

    @Override
    public List<BriefEvent> getBriefEventList(JSONObject requestData) {
        QueryWrapper<BriefEvent> queryWrapper = new QueryWrapper<>();
        Integer id = requestData.getInteger("id");
        Integer detailedDataLocation = requestData.getInteger("detailedDataLocation");
        String publisherFullId = requestData.getString("publisherFullId");
        LocalDateTime searchTimeStart = requestData.getObject("searchTimeStart", LocalDateTime.class);
        LocalDateTime searchTimeEnd = requestData.getObject("searchTimeEnd", LocalDateTime.class);
        String name = requestData.getString("name");
        if (id != null) {
            queryWrapper.eq("id", id);
        }
        if (detailedDataLocation != null) {
            queryWrapper.eq("detailed_data_location", detailedDataLocation);
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
        return baseMapper.selectList(queryWrapper);
    }
}