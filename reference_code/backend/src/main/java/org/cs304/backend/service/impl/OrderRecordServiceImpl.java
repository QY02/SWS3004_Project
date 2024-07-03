package org.cs304.backend.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.cs304.backend.entity.OrderRecord;
import org.cs304.backend.exception.ServiceException;
import org.cs304.backend.mapper.EventMapper;
import org.cs304.backend.mapper.EventSessionMapper;
import org.cs304.backend.mapper.OrderRecordMapper;
import org.cs304.backend.mapper.UserMapper;
import org.cs304.backend.service.IOrderRecordService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.cs304.backend.constant.constant_OrderRecordStatus.*;

@Service
public class OrderRecordServiceImpl extends ServiceImpl<OrderRecordMapper, OrderRecord> implements IOrderRecordService {

    @Resource
    private EventMapper eventMapper;
    @Resource
    private EventSessionMapper eventSessionMapper;
    @Resource
    private OrderRecordMapper orderRecordMapper;
    @Resource
    private UserMapper userMapper;

    @Override
    public Object getMyOrderRecord(String userId, Integer eventId, Integer mode) {
        if ((mode == null) || ((mode != 0) && (mode != 1) && (mode != 2) && (mode != 3))) {
            throw new ServiceException("400", "Invalid data");
        }
        List<Integer> status = Arrays.asList(PAID, SUBMITTED);
        QueryWrapper<OrderRecord> queryWrapper = new QueryWrapper<OrderRecord>().eq("user_id", userId).in("status", status);
        if (eventId != null) {
            queryWrapper.eq("event_id", eventId);
        }
        List<OrderRecord> orderRecordList = baseMapper.selectList(queryWrapper);
        return orderRecordList.stream().map(orderRecord -> {
            if (mode == 0) {
                return orderRecord.getEventSessionId();
            } else if (mode == 1) {
                return orderRecord;
            } else {
                JSONObject jsonObject = JSONObject.from(orderRecord);
                jsonObject.put("eventSession", eventSessionMapper.selectById(orderRecord.getEventSessionId()));
                if (mode == 3) {
                    jsonObject.put("event", eventMapper.selectById(orderRecord.getEventId()));
                }
                return jsonObject;
            }
        }).collect(Collectors.toList());
    }

    @Override
    public Object getEventOrderRecord(Integer eventId) {
        QueryWrapper<OrderRecord> queryWrapper = new QueryWrapper<OrderRecord>().eq("event_id", eventId).eq("status", PAID);
        List<OrderRecord> orderRecordList = baseMapper.selectList(queryWrapper);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventMapper.selectById(eventId));
        jsonObject.put("orderRecordList", orderRecordList.stream().map(orderRecord -> {
                    JSONObject jsonObject0 = JSONObject.from(orderRecord);
                    jsonObject0.put("eventSession", eventSessionMapper.selectById(orderRecord.getEventSessionId()));
                    jsonObject0.put("user", userMapper.selectById(orderRecord.getUserId()));
                    return jsonObject0;
                }
        ).collect(Collectors.toList()));
        return jsonObject;
    }


    @Override
    public Object getUnpaidOrderRecord(String userId, Integer eventId, Integer mode) {
        if ((mode == null) || ((mode != 0) && (mode != 1) && (mode != 2) && (mode != 3))) {
            throw new ServiceException("400", "Invalid data");
        }
        QueryWrapper<OrderRecord> queryWrapper = new QueryWrapper<OrderRecord>().eq("user_id", userId).eq("status", UNPAID);

        if (eventId != null) {
            queryWrapper.eq("event_id", eventId);
        }
        List<OrderRecord> orderRecordList = baseMapper.selectList(queryWrapper);
//        System.out.println(orderRecordList.stream().toList());
        Map<Boolean, List<OrderRecord>> expiredAndNonExpiredOrders = orderRecordList.stream()
                .collect(Collectors.partitioningBy(orderRecord -> {
                    LocalDateTime submitDateTime = orderRecord.getSubmitTime();
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    Duration duration = Duration.between(submitDateTime, currentDateTime);
                    return duration.toMinutes() >= 10; // 返回是否过期的布尔值
                }));

        // 获取过期和未过期的订单列表
        List<OrderRecord> expiredOrders = expiredAndNonExpiredOrders.get(true);
        List<OrderRecord> nonExpiredOrders = expiredAndNonExpiredOrders.get(false);

        // 处理过期订单
        expiredOrders.forEach(orderRecord -> {
            orderRecord.setStatus(-1); // 标记为过期状态
            orderRecordMapper.updateById(orderRecord); // 更新订单状态
            System.out.println("Order ID " + orderRecord.getId() + " has expired and updated successfully.");
        });

        return nonExpiredOrders.stream().map(orderRecord -> {
            if (mode == 0) {
                return orderRecord.getEventSessionId();
            } else if (mode == 1) {
                return orderRecord;
            } else {
                JSONObject jsonObject = JSONObject.from(orderRecord);
                jsonObject.put("eventSession", eventSessionMapper.selectById(orderRecord.getEventSessionId()));
                if (mode == 3) {
                    jsonObject.put("event", eventMapper.selectById(orderRecord.getEventId()));
                }
                return jsonObject;
            }
        }).collect(Collectors.toList());
    }

    @Override
    public int getPaymentById(Integer orderId) {
        if (orderId == null) {
            throw new ServiceException("400", "Invalid data");
        }
        QueryWrapper<OrderRecord> queryWrapper = new QueryWrapper<OrderRecord>().eq("id", orderId);
        OrderRecord orderRecord = baseMapper.selectOne(queryWrapper);
        if(orderRecord.getPaymentTime()!=null){
            return 1;
        }
        return 0;
    }
}
