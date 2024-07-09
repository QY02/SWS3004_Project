package org.nus.cloud.orderRecordService.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.nus.cloud.orderRecordService.entity.OrderRecord;
import org.nus.cloud.orderRecordService.exception.ServiceException;
import org.nus.cloud.orderRecordService.mapper.OrderRecordMapper;
import org.nus.cloud.orderRecordService.service.IOrderRecordService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class OrderRecordServiceImpl extends ServiceImpl<OrderRecordMapper, OrderRecord> implements IOrderRecordService {

    @Override
    public List<OrderRecord> get(JSONObject requestData) {
        QueryWrapper<OrderRecord> queryWrapper = new QueryWrapper<>();
        Integer id = requestData.getInteger("id");
        Integer eventId = requestData.getInteger("eventId");
        Integer eventSessionId = requestData.getInteger("eventSessionId");
        String seatId = requestData.getString("seatId");
        Integer searchPriceStart = requestData.getInteger("searchPriceStart");
        Integer searchPriceEnd = requestData.getInteger("searchPriceEnd");
        LocalDateTime searchTimeStart = requestData.getObject("searchTimeStart", LocalDateTime.class);
        LocalDateTime searchTimeEnd = requestData.getObject("searchTimeEnd", LocalDateTime.class);
        if (id != null) {
            queryWrapper.eq("id", id);
        }
        if (eventId != null) {
            queryWrapper.eq("event_id", eventId);
        }
        if (eventSessionId != null) {
            queryWrapper.eq("event_session_id", eventSessionId);
        }
        if (seatId != null) {
            queryWrapper.eq("seat_id", seatId);
        }
        if (searchPriceStart != null) {
            queryWrapper.ge("price", searchPriceStart);
        }
        if (searchPriceEnd != null) {
            queryWrapper.lt("price", searchPriceEnd);
        }
        if (searchTimeStart != null) {
            queryWrapper.ge("submit_time", searchTimeStart);
        }
        if (searchTimeEnd != null) {
            queryWrapper.lt("submit_time", searchTimeEnd);
        }
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public OrderRecord add(OrderRecord orderRecord) {
        orderRecord.setSubmitTime(LocalDateTime.now());
        try {
            baseMapper.insert(orderRecord);
        } catch (DuplicateKeyException e) {
            throw new ServiceException("400", "You have already booked the session");
        }
        return orderRecord;
    }
}