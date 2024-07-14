package org.nus.cloud.eventPublishService.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nus.cloud.eventPublishService.entity.*;
import org.nus.cloud.eventPublishService.exception.ServiceException;
import org.nus.cloud.eventPublishService.mapper.*;
import org.nus.cloud.eventPublishService.service.IEventService;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class EventServiceImpl extends ServiceImpl<EventMapper, Event> implements IEventService {

    @Value("${event-global-data-service.host:}")
    private String eventGlobalDataServiceHost;

    @Value("${event-global-data-service.port:}")
    private String eventGlobalDataServicePort;

    @Value("${pod-index:}")
    private String podIndex;

    @Resource
    private EventSessionMapper eventSessionMapper;

    @Resource
    private SeatMapTemplateMapper seatMapTemplateMapper;

    @Resource
    private SeatTemplateMapper seatTemplateMapper;

    @Resource
    private SeatMapMapper seatMapMapper;

    @Resource
    private SeatMapper seatMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private FanoutExchange fanoutExchange;

    @Override
    public JSONObject publishEvent(String fullUserId, JSONObject requestData) {
        String name = requestData.getString("name");
        if (name == null || name.isBlank()) {
            throw new ServiceException("400", "Invalid event name");
        }
        String content = requestData.getString("content");
        Event event = new Event(null, fullUserId, LocalDateTime.now(), name, content);
        baseMapper.insert(event);
        List<JSONObject> eventSessionJSONObjectList = requestData.getList("sessions", JSONObject.class);
        if (eventSessionJSONObjectList == null || eventSessionJSONObjectList.isEmpty()) {
            throw new ServiceException("400", "An event must have at least one session");
        }
        List<EventSession> eventSessionResultList = new ArrayList<>();
        List<SeatMap> seatMapResultList = new ArrayList<>();
        eventSessionJSONObjectList.forEach(session -> {
            LocalDateTime registrationStartTime = session.getObject("registrationStartTime", LocalDateTime.class);
            LocalDateTime registrationEndTime = session.getObject("registrationEndTime", LocalDateTime.class);
            LocalDateTime startTime = session.getObject("startTime", LocalDateTime.class);
            LocalDateTime endTime = session.getObject("endTime", LocalDateTime.class);
            Integer seatMapTemplateId = session.getInteger("seatMapTemplateId");
            String venue = session.getString("venue");
            if (registrationStartTime.isBefore(LocalDateTime.now()) || registrationEndTime.isBefore(LocalDateTime.now())) {
                ServiceException serviceException = new ServiceException("400", "Registration start time and registration end time should not earlier than the current time");
                serviceException.setCauseObject(session);
                throw serviceException;
            }
            if (!registrationStartTime.isBefore(registrationEndTime)) {
                ServiceException serviceException = new ServiceException("400", "Registration start time should earlier than registration end time");
                serviceException.setCauseObject(session);
                throw serviceException;
            }
            if (startTime.isBefore(LocalDateTime.now()) || endTime.isBefore(LocalDateTime.now())) {
                ServiceException serviceException = new ServiceException("400", "Start time and end time should not earlier than the current time");
                serviceException.setCauseObject(session);
                throw serviceException;
            }
            if (!startTime.isBefore(registrationEndTime)) {
                ServiceException serviceException = new ServiceException("400", "Start time should earlier than end time");
                serviceException.setCauseObject(session);
                throw serviceException;
            }
            if (seatMapTemplateId == null) {
                ServiceException serviceException = new ServiceException("400", "Invalid seat map template id");
                serviceException.setCauseObject(session);
                throw serviceException;
            }
            if ((venue == null) || (venue.isBlank())) {
                ServiceException serviceException = new ServiceException("400", "Venue cannot be empty");
                serviceException.setCauseObject(session);
                throw serviceException;
            }
            SeatMapTemplate seatMapTemplate = seatMapTemplateMapper.selectById(seatMapTemplateId);
            List<SeatTemplate> seatTemplateList = null;
            if (seatMapTemplate == null) {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders httpHeaders = new HttpHeaders();
                JSONObject requestBody = new JSONObject();
                requestBody.put("id", seatMapTemplateId);
                try {
                    HttpEntity<JSONObject> httpEntity = new HttpEntity<>(requestBody, httpHeaders);
                    ResponseEntity<JSONObject> responseFromService = restTemplate.exchange("http://" + eventGlobalDataServiceHost + ":" + eventGlobalDataServicePort + "/seatMapTemplate/getSeatMapTemplateById", HttpMethod.POST, httpEntity, JSONObject.class);
                    if (responseFromService.getStatusCode().value() != 200) {
                        throw new ServiceException("500", "An error occurred when requesting seat map template from event global data service");
                    }
                    JSONObject seatMapTemplateWithSeats = Objects.requireNonNull(responseFromService.getBody()).getJSONObject("data");
                    seatMapTemplate = seatMapTemplateWithSeats.getObject("seatMapTemplate", SeatMapTemplate.class);
                    seatTemplateList = seatMapTemplateWithSeats.getList("seatTemplateArray", SeatTemplate.class);
                    seatMapTemplateMapper.insert(seatMapTemplate);
                    seatTemplateMapper.insert(seatTemplateList);
                } catch (RestClientException e) {
                    throw new ServiceException("500", "An error occurred when requesting seat map template from event global data service");
                }
            }
            if (seatTemplateList == null) {
                seatTemplateList = seatTemplateMapper.selectList(new QueryWrapper<SeatTemplate>().eq("seat_map_id", seatMapTemplateId));
            }
            SeatMap seatMap = new SeatMap(null, seatMapTemplate.getName(), seatMapTemplate.getData());
            seatMapMapper.insert(seatMap);
            seatMapResultList.add(seatMap);
            seatTemplateList.forEach(seatTemplate -> {
                Seat seat = new Seat(seatMap.getId(), seatTemplate.getSeatId(), seatTemplate.getType(), true, seatTemplate.getPrice());
                seatMapper.insert(seat);
            });
            EventSession eventSession = new EventSession(null, event.getId(), registrationStartTime, registrationEndTime, startTime, endTime, seatMap.getId(), venue);
            eventSessionMapper.insert(eventSession);
            eventSessionResultList.add(eventSession);
        });
        JSONObject eventBriefData = new JSONObject();
        eventBriefData.put("id", event.getId());
        eventBriefData.put("publisherFullId", event.getPublisherFullId());
        eventBriefData.put("publishDatetime", event.getPublishDatetime());
        eventBriefData.put("name", name);
        eventBriefData.put("detailedDataLocation", podIndex);
        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "", eventBriefData.toString());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", event);
        jsonObject.put("eventSessionList", eventSessionResultList);
        jsonObject.put("seatMapList", seatMapResultList);
        return jsonObject;
    }
}