package org.cs304.backend.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.cs304.backend.entity.Event;
import org.cs304.backend.entity.EventSession;
import org.cs304.backend.entity.OrderRecord;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IEventService extends IService<Event> {

    @Transactional(rollbackFor = Exception.class)
    JSONObject createEventStart(JSONObject data);

    String getAttachment(Integer eventId);

    @Transactional(rollbackFor = Exception.class)
    JSONObject createEventFinish(JSONObject requestData);

    JSONArray getAllEvents();
    List<Event> getEventByPublisher(int userType, Integer publisherId);

    List<Event> getBatchByIds(int userType,List<Integer> idList);
    JSONArray getAuditList(String eventStatus);

    JSONObject updateEventStart(JSONObject data);

    JSONObject updateEventFinish(JSONObject requestData);

    void insertSessions(int id, JSONArray eventSessionData);

    List<EventSession> getEventSessionsByEventId(int userType, Integer eventId);

//    Event getEventByEventId(int userType, Integer eventId);

    @Transactional(rollbackFor = Exception.class)
    void submitBookingData(int userType, String userId, OrderRecord orderRecord);

    @Transactional(rollbackFor = Exception.class)
    void changeAudit(String publisherId,Integer eventId, Integer status, String reason);

    List<Event> getRecommendEvents(String userId);

    JSONArray getHotValue();
}
