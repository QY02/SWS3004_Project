package org.nus.cloud.bookingService.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nus.cloud.bookingService.entity.*;
import org.nus.cloud.bookingService.exception.ServiceException;
import org.nus.cloud.bookingService.mapper.EventMapper;
import org.nus.cloud.bookingService.mapper.EventSessionMapper;
import org.nus.cloud.bookingService.mapper.SeatMapMapper;
import org.nus.cloud.bookingService.mapper.SeatMapper;
import org.nus.cloud.bookingService.service.IEventService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
public class EventServiceImpl extends ServiceImpl<EventMapper, Event> implements IEventService {

    @Value("${order-record-service.host:}")
    private String orderRecordServiceHost;

    @Value("${order-record-service.port:}")
    private String orderRecordServicePort;

    @Resource
    private EventSessionMapper eventSessionMapper;

    @Resource
    private SeatMapMapper seatMapMapper;

    @Resource
    private SeatMapper seatMapper;

    @Override
    public JSONObject book(String fullUserId, JSONObject requestData) {
        Integer eventId = requestData.getInteger("eventId");
        Integer eventSessionId = requestData.getInteger("eventSessionId");
        String seatId = requestData.getString("seatId");
        if (eventId == null) {
            throw new ServiceException("400", "Invalid event id");
        }
        if (eventSessionId == null) {
            throw new ServiceException("400", "Invalid event session id");
        }
        if (seatId == null) {
            throw new ServiceException("400", "Invalid seat id");
        }
        Event event = baseMapper.selectById(eventId);
        if (event == null) {
            throw new ServiceException("400", "Event not exist");
        }
        EventSession eventSession = eventSessionMapper.selectById(eventSessionId);
        if (eventSession == null) {
            throw new ServiceException("400", "Event session not exist");
        }
        if (LocalDateTime.now().isBefore(eventSession.getStartTime()) || (!LocalDateTime.now().isBefore(eventSession.getEndTime()))) {
            throw new ServiceException("401", "The current time is not within the registration period");
        }
        SeatMap seatMap = seatMapMapper.selectById(eventSession.getSeatMapId());
        if (seatMap == null) {
            throw new ServiceException("500", "Seat Map not exist");
        }
        Seat seat = seatMapper.selectOne(new QueryWrapper<Seat>().eq("seat_map_id", eventSession.getSeatMapId()).eq("seat_id", seatId));
        if (seat == null) {
            throw new ServiceException("400", "Seat not exist");
        }
        if (!seat.getAvailability()) {
            throw new ServiceException("400", "Seat is not available");
        }
        OrderRecord orderRecord = new OrderRecord();
        orderRecord.setFullUserId(fullUserId);
        orderRecord.setEventId(eventId);
        orderRecord.setEventSessionId(eventSessionId);
        orderRecord.setSeatId(seatId);
        orderRecord.setAdditionalInformation(requestData.getString("additionalInformation"));
        orderRecord.setPrice(seat.getPrice());
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        try {
            HttpEntity<OrderRecord> httpEntity = new HttpEntity<>(orderRecord, httpHeaders);
            ResponseEntity<JSONObject> responseFromService = restTemplate.exchange("http://" + orderRecordServiceHost + ":" + orderRecordServicePort + "/add", HttpMethod.POST, httpEntity, JSONObject.class);
            if (responseFromService.getStatusCode().value() != 200) {
                throw new ServiceException("500", "An error occurred when sending order record to order record service");
            } else {
                seatMapper.update(new UpdateWrapper<Seat>().eq("seat_map_id", seatMap.getId()).eq("seat_id", seatId).set("availability", false));
                return Objects.requireNonNull(responseFromService.getBody()).getJSONObject("data");
            }
        } catch (RestClientException e) {
            throw new ServiceException("500", "An error occurred when sending order record to order record service");
        }
    }
}