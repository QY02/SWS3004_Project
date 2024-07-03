package org.cs304.backend.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.cs304.backend.constant.constant_AttachmentType;
import org.cs304.backend.constant.constant_EventStatus;
import org.cs304.backend.constant.constant_OrderRecordStatus;
import org.cs304.backend.constant.constant_User;
import org.cs304.backend.entity.*;
import org.cs304.backend.exception.ServiceException;
import org.cs304.backend.mapper.*;
import org.cs304.backend.service.IAttachmentService;
import org.cs304.backend.service.IEventService;
import org.cs304.backend.service.IEventSessionService;
import org.cs304.backend.service.INotificationService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static org.cs304.backend.constant.constant_AttachmentType.IMAGE;
import static org.cs304.backend.constant.constant_EntityType.EVENT;
import static org.cs304.backend.constant.constant_OrderRecordStatus.*;
import static org.cs304.backend.constant.constant_User.ADMIN;

@Service
public class EventServiceImpl extends ServiceImpl<EventMapper, Event> implements IEventService {

    private final Lock submitBookingDataLock = new ReentrantLock();

    @Resource
    private IEventSessionService eventSessionService;
    @Resource
    private IAttachmentService attachmentService;
    @Resource
    private EventMapper eventMapper;
    @Resource
    private EventSessionMapper eventSessionMapper;
    @Resource
    private EntityAttachmentRelationMapper entityAttachmentRelationMapper;
    @Resource
    private SeatMapper seatMapper;
    @Resource
    private SeatMapMapper seatMapMapper;
    @Resource
    private OrderRecordMapper orderRecordMapper;
    @Resource
    private UserInteractionMapper userInteractionMapper;
    @Resource
    private UserFavoriteTypeMapper userFavoriteTypeMapper;
    @Resource
    private HistoryMapper historyMapper;
    @Resource
    private UserMapper userMapper;

    @Resource
    private INotificationService notificationService;

    @Override
    public JSONArray getAuditList(String eventStatus) {
        String[] eventStatusArray = eventStatus.split(",");
        List<Integer> eventStatusList = Arrays.stream(eventStatusArray).map(Integer::parseInt).toList();
        QueryWrapper<Event> queryWrapper = new QueryWrapper<Event>().in("status", eventStatusList);
        List<Event> list = list(queryWrapper);
        if (list != null) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(list.stream().map(JSON::toJSON).toList());
            for (int i = 0; i < jsonArray.size(); i++) {
                QueryWrapper<EventSession> queryWrapper1 = new QueryWrapper<EventSession>().eq("event_id", jsonArray.getJSONObject(i).getInteger("id"));
                List<EventSession> eventSessionList = eventSessionService.list(queryWrapper1);
                if (eventSessionList == null || eventSessionList.isEmpty()) {
                    jsonArray.getJSONObject(i).put("startTime", null);
                    jsonArray.getJSONObject(i).put("location", null);
                    continue;
                }
                LocalDateTime start_time = eventSessionList.get(0).getStartTime();
                for (int j = 1; j < eventSessionList.size(); j++) {
                    if (start_time.isAfter(eventSessionList.get(j).getStartTime())) {
                        start_time = eventSessionList.get(j).getStartTime();
                    }
                }
                jsonArray.getJSONObject(i).put("startTime", start_time);
                jsonArray.getJSONObject(i).put("location", eventSessionList.get(0).getVenue());
                jsonArray.getJSONObject(i).put("avatar", userMapper.selectById(jsonArray.getJSONObject(i).getString("publisherId")).getIconId());
            }
            return jsonArray;
        }
        return new JSONArray();
    }


    @Override
    public String getAttachment(Integer eventId) {
        EntityAttachmentRelation attachmentRelation = entityAttachmentRelationMapper.selectOne(new QueryWrapper<EntityAttachmentRelation>().eq("entity_id", eventId).eq("attachment_type", IMAGE).eq("entity_type", EVENT));
        int attachmentId = attachmentRelation.getAttachmentId();
        Attachment attachment = attachmentService.getById(ADMIN, attachmentId);
        String attachmentPath = attachment.getFilePath();
        return attachmentPath;
    }

    @Override
    public JSONObject createEventStart(JSONObject data) {
        JSONObject eventData = data.getJSONObject("event");
        JSONArray eventSessionData = data.getJSONArray("sessions");

//        System.out.println("eventData:" + eventData);

        Event event = new Event();
        event.setName(eventData.getString("name"));
        event.setContent(eventData.getString("content"));
        event.setType(eventData.getInteger("type"));
        event.setPublisherId(eventData.getString("publisher_id"));
        event.setPublishDate(LocalDateTime.now());
        event.setStatus(constant_EventStatus.AUDITING);
        event.setEventPolicy(eventData.getString("event_policy"));
        event.setVisible(eventData.getBoolean("visible"));
        JSONObject requestData = JSONObject.from(event);
        requestData.put("serviceClassName", this.getClass().getName());
        requestData.put("serviceMethodName", "createEventFinish");
        requestData.put("eventSessionData", eventSessionData);
        List<String> fileList = data.getJSONArray("poster").toJavaList(String.class);

        List<String> fileDirList = new ArrayList<>();
        for (String ignored : fileList) {
            fileDirList.add("event/" + "uuid");
        }

//        System.out.println("fileList");
//        System.out.println(fileList);
//        System.out.println("fileDirList");
//        System.out.println(fileDirList);

        // 打印 Event 对象的属性值
//        System.out.println("Event Object: " + event);
//        insertSessions(1, eventSessionData);

        return attachmentService.uploadBatchStart(ADMIN, fileDirList, fileList, requestData);
//        return null;
    }

    @Override
    public JSONObject createEventFinish(JSONObject requestData) {
        List<Attachment> attachmentList = requestData.getList("fileInfoList", Attachment.class);

        Event event = requestData.toJavaObject(Event.class);
        eventMapper.insert(event);
        Integer eventId = event.getId();

        JSONArray eventSessionData = requestData.getJSONArray("eventSessionData");
        insertSessions(eventId, eventSessionData);

        attachmentList.forEach(attachment -> {
            EntityAttachmentRelation entityAttachmentRelation = new EntityAttachmentRelation();
            entityAttachmentRelation.setEntityType(EVENT);
            entityAttachmentRelation.setEntityId(eventId);
            entityAttachmentRelation.setAttachmentType(constant_AttachmentType.IMAGE);
            entityAttachmentRelation.setAttachmentId(attachment.getId());
            entityAttachmentRelationMapper.insert(entityAttachmentRelation);
        });
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("eventId", eventId);
        return jsonObject;
    }
    @Override
    public JSONObject updateEventStart(JSONObject data) {
        JSONObject eventData = data.getJSONObject("event");
        JSONArray eventSessionData = data.getJSONArray("sessions");

        Event event=eventMapper.selectById(eventData.getInteger("eventId"));
//        event.setId(eventData.getInteger("eventId"));
        event.setName(eventData.getString("name"));
        event.setContent(eventData.getString("content"));
        event.setType(eventData.getInteger("type"));
        event.setPublisherId(eventData.getString("publisher_id"));
        event.setPublishDate(LocalDateTime.now());
        event.setStatus(constant_EventStatus.AUDITING);
        event.setEventPolicy(eventData.getString("event_policy"));
        event.setVisible(eventData.getBoolean("visible"));
        JSONObject requestData = JSONObject.from(event);
        requestData.put("serviceClassName", this.getClass().getName());
        requestData.put("serviceMethodName", "updateEventFinish");
        requestData.put("eventSessionData", eventSessionData);
        List<String> fileList = data.getJSONArray("poster").toJavaList(String.class);

        List<String> fileDirList = new ArrayList<>();
        for (String ignored : fileList) {
            fileDirList.add("event/" + "uuid");
        }

        return attachmentService.uploadBatchStart(ADMIN, fileDirList, fileList, requestData);
//        return null;
    }

    @Override
    public JSONObject updateEventFinish(JSONObject requestData) {
        List<Attachment> attachmentList = requestData.getList("fileInfoList", Attachment.class);

        Event event = requestData.toJavaObject(Event.class);
        eventMapper.updateById(event);
        Integer eventId = event.getId();

        QueryWrapper<EventSession> queryWrapper = new QueryWrapper<EventSession>().eq("event_id", eventId);
//        queryWrapper.orderByDesc("publish_date");
        List<EventSession> list = eventSessionMapper.selectList(queryWrapper);
        eventSessionMapper.deleteBatchIds(list);

        JSONArray eventSessionData = requestData.getJSONArray("eventSessionData");
        insertSessions(eventId, eventSessionData);

        attachmentList.forEach(attachment -> {
            entityAttachmentRelationMapper.update(new UpdateWrapper<EntityAttachmentRelation>().eq("entity_type", EVENT).eq("entity_id", eventId).eq("attachment_type", constant_AttachmentType.IMAGE).set("attachment_id", attachment.getId()));
        });
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("eventId", eventId);
        return jsonObject;
    }


    public void insertSessions(int id, JSONArray eventSessionData) {
        if (eventSessionData != null && !eventSessionData.isEmpty()) {
            int highestPrice = -1;
            int lowestPrice = -1;
            int dataSize = eventSessionData.size();
            for (int i = 0; i < dataSize; i++) {
                JSONObject sessionData = eventSessionData.getJSONObject(i);
                eventSessionService.insertEventSession(id, sessionData);

                int seatMapId = sessionData.getInteger("seat_map_id");
                if (seatMapId == -1) {
                    int price = sessionData.getInteger("price");
                    if ((highestPrice == -1 || price > highestPrice)) {
                        highestPrice = price;
                    }
                    if (lowestPrice == -1 || price < lowestPrice) {
                        lowestPrice = price;
                    }
                } else {
                    SeatMap seatMap = seatMapMapper.selectById(seatMapId);
                    JSONObject seatMapData = JSONObject.parseObject(seatMap.getData());
                    JSONArray seatDataJSONArray = seatMapData.getJSONArray("seats");
                    for (Object seat : seatDataJSONArray) {
                        JSONObject seatDataJSONObject = (JSONObject) seat;
                        String seatId = seatDataJSONObject.getString("id");
                        Integer price = seatMapper.selectOne(new QueryWrapper<Seat>().eq("seat_map_id", seatMapId).eq("seat_id", seatId)).getPrice();
                        if ((highestPrice == -1 || price > highestPrice)) {
                            highestPrice = price;
                        }
                        if (lowestPrice == -1 || price < lowestPrice) {
                            lowestPrice = price;
                        }
                    }
                }
            }
            eventMapper.update(new UpdateWrapper<Event>().eq("id", id).set("highest_price", highestPrice).set("lowest_price", lowestPrice));
        } else {
            throw new ServiceException("400", "eventSessionData is null or empty.");
        }
    }


    @Override
    public List<EventSession> getEventSessionsByEventId(int userType, Integer eventId) {
        if (eventId == null) {
            throw new ServiceException("400", "Invalid event id");
        }
        Event event = baseMapper.selectById(eventId);
        if (event == null) {
            throw new ServiceException("400", "Event not exist");
        }
//        if ((userType != constant_User.ADMIN) && ((event.getStatus() != constant_EventStatus.PASSED) || (!event.getVisible()))) {
//            throw new ServiceException("400", "Event not exist");
//        }
        if ((userType != constant_User.ADMIN) && (!event.getVisible())) {
            throw new ServiceException("400", "Event not exist");
        }
        return eventSessionMapper.selectList(new QueryWrapper<EventSession>().eq("event_id", eventId)).stream().filter(eventSession -> (userType == constant_User.ADMIN) || (eventSession.getVisible())).peek(eventSession -> {
            long currentSize = orderRecordMapper.selectCount(new QueryWrapper<OrderRecord>().eq("event_session_id", eventSession.getEventSessionId()).ne("status", EXPIRED));
            eventSession.setCurrentSize((int) currentSize);
        }).collect(Collectors.toList());
    }


    @Override
    public void submitBookingData(int userType, String userId, OrderRecord orderRecord) {
        submitBookingDataLock.lock();
        try {
            Event event = baseMapper.selectById(orderRecord.getEventId());
            if ((event == null) || (event.getStatus() != constant_EventStatus.PASSED) || (!event.getVisible())) {
                throw new ServiceException("400", "Event not exist");
            }
            EventSession eventSession = eventSessionMapper.selectById(orderRecord.getEventSessionId());
            if ((eventSession == null) || (!eventSession.getVisible()) || (!Objects.equals(eventSession.getEventId(), orderRecord.getEventId()))) {
                throw new ServiceException("400", "Event session not exist");
            }
            if (!eventSession.getRegistrationRequired()) {
                throw new ServiceException("401", "This event session does not require registration");
            }
            if (LocalDateTime.now().isBefore(eventSession.getRegistrationStartTime()) || (!LocalDateTime.now().isBefore(eventSession.getRegistrationEndTime()))) {
                throw new ServiceException("401", "Not within the registration time period");
            }
            if (eventSession.getCurrentSize() >= eventSession.getMaxSize()) {
                throw new ServiceException("401", "This session is full");
            }
            if(eventSession.getSeatMapId()!=-1){
                Seat seat = seatMapper.selectOne(new QueryWrapper<Seat>().eq("seat_map_id", eventSession.getSeatMapId()).eq("seat_id", orderRecord.getSeatId()));
                if (seat == null) {
                    throw new ServiceException("400", "Seat not exist");
                }
                if (!seat.getAvailability()) {
                    throw new ServiceException("409", "The seat is not available");
                }
                seat.setAvailability(false);
                seatMapper.update(seat, new UpdateWrapper<Seat>().eq("seat_map_id", eventSession.getSeatMapId()).eq("seat_id", orderRecord.getSeatId()));
                orderRecord.setPrice(seat.getPrice());
            }
            else{
                orderRecord.setPrice(orderRecord.getPrice());
            }
            List<Integer> statuses = Arrays.asList(PAID, UNPAID, SUBMITTED);
            OrderRecord order = orderRecordMapper.selectOne(new QueryWrapper<OrderRecord>().eq("user_id", userId).eq("event_id", orderRecord.getEventId()).eq("event_session_id", orderRecord.getEventSessionId())
                    .in("status", statuses));
            if (order != null) {
                LocalDateTime submitDateTime = order.getSubmitTime();
                LocalDateTime currentDateTime = LocalDateTime.now();
                Duration duration = Duration.between(submitDateTime, currentDateTime);
                if (duration.toMinutes() >= 10) {
                    order.setStatus(-1);//expired
                    orderRecordMapper.updateById(order);
                    System.out.println("update successful");
                }
                int status = order.getStatus();
                if (status == SUBMITTED) {
                    throw new ServiceException("400", "您已成功报名。");
                }
                if (status == PAID) {
                    throw new ServiceException("400", "您已成功报名并支付。");
                }
                if (status == UNPAID) {
                    throw new ServiceException("400", "已报名，等待用户支付中，请限定时间内及时支付。");
                }
                // status == EXPIRED 就可以重新加入了
            }
//        if (orderRecordMapper.exists(new QueryWrapper<OrderRecord>().eq("user_id", userId).eq("event_id", orderRecord.getEventId()).eq("event_session_id", orderRecord.getEventSessionId()))) {
//            throw new ServiceException("400", "The order record already exist");
//        }
            orderRecord.setUserId(userId);
            // orderRecord.setPrice(seat.getPrice());
            orderRecord.setStatus(SUBMITTED);
            orderRecord.setSubmitTime(LocalDateTime.now());
            orderRecord.setPaymentTime(null);
            orderRecordMapper.insert(orderRecord);
        } finally {
            submitBookingDataLock.unlock();
        }
    }


    @Override
    public JSONArray getAllEvents() {//开始时间从大到小排序返回
        QueryWrapper<Event> queryWrapper = new QueryWrapper<Event>();
        queryWrapper.orderByDesc("publish_date");
        List<Event> list = list(queryWrapper);
        if (list != null) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(list.stream().map(JSON::toJSON).toList());
            return jsonArray;
        }
        return new JSONArray();
    }

    public List<Event> getEventByPublisher(int userType, Integer publisherId) {
        if (publisherId == null) {
            throw new ServiceException("400", "Invalid event id");
        }
        QueryWrapper<Event> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("publisher_id", publisherId);
//        System.out.println(eventMapper.selectList(queryWrapper));
        return eventMapper.selectList(queryWrapper);

    }

    @Override
    public List<Event> getBatchByIds(int userType, List<Integer> idList) {
        List<Event> result = new ArrayList<>();
        for (Integer id : idList) {
            try {
                result.add(getById(id));
            } catch (ServiceException e) {
                e.setCauseObject(id);
                throw e;
            }
        }
        return result;
    }

    @Override
    public void changeAudit(String publisherId,Integer eventId, Integer status, String reason) {
        Event event = baseMapper.selectById(eventId);
        if (event == null) {
            throw new ServiceException("400", "Event not exist");
        }
        if (status == constant_EventStatus.PASSED) {
            event.setStatus(constant_EventStatus.PASSED);
            notificationService.insertEventPassNotification(publisherId,eventId);
        } else if (status == constant_EventStatus.REJECTED) {
            event.setStatus(constant_EventStatus.REJECTED);
            event.setAuditRecord(reason);
            notificationService.insertEventNotPassNotification(publisherId,eventId,reason);
        } else {
            throw new ServiceException("400", "Invalid status");
        }
        baseMapper.updateById(event);
    }

    @Override
    public List<Event> getRecommendEvents(String userId) {
        // TODO 控制返回的个数
        List<UserFavoriteType> userFavoriteTypeList = userFavoriteTypeMapper.selectList(new QueryWrapper<UserFavoriteType>().eq("user_id", userId));
        if (userFavoriteTypeList == null || userFavoriteTypeList.isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> typeList = userFavoriteTypeList.stream().map(UserFavoriteType::getEventType).collect(Collectors.toList());
        QueryWrapper<Event> queryWrapper = new QueryWrapper<Event>().in("type", typeList).eq("status", constant_EventStatus.PASSED).eq("visible", true).last("limit 5");
        List<Event> list = list(queryWrapper);
        List<UserInteraction> userInteractionList = userInteractionMapper.selectList(new QueryWrapper<UserInteraction>().eq("user_id", userId).eq("update_type", 0));
        if (userInteractionList != null && !userInteractionList.isEmpty()) {
            List<Integer> eventIdList = userInteractionList.stream().map(UserInteraction::getEventId).collect(Collectors.toList());
            eventIdList = eventIdList.stream().filter(eventId -> !list.stream().map(Event::getId).toList().contains(eventId)).collect(Collectors.toList());
            List<Event> eventList = listByIds(eventIdList);
            list.addAll(eventList);
        }
        if (list != null) {
            return list;
        }
        return new ArrayList<>();
    }

    @Override
    public JSONArray getHotValue() {
        QueryWrapper<Event> queryWrapper = new QueryWrapper<Event>();
        List<Event> list = list(queryWrapper);//所有活动
        List<History> histories = historyMapper.selectList(new QueryWrapper<>());
        Map<Integer, Long> countEvents = histories.stream()
                .collect(Collectors.groupingBy(History::getEventId, Collectors.counting()));

        if (list != null) {
            LocalDateTime now = LocalDateTime.now();
            List<JSONObject> jsonArray = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Event curEvent = list.get(i);
                JSONObject eventDetails = JSONObject.from(curEvent);
                //已经发布了多久
                long daysBetween = ChronoUnit.DAYS.between(now, curEvent.getPublishDate());
                double heat;
                int eventId = curEvent.getId();
                long countsOfEvent = 0;
                if (countEvents.containsKey(eventId)) {
                    countsOfEvent = countEvents.get(eventId);
                }
                if (daysBetween <= 3) {
                    heat = Math.max(Math.exp(-0.099 * daysBetween), 0.01) * countsOfEvent;
                } else if (daysBetween <= 7) {
                    heat = Math.max(Math.exp(-0.099 * daysBetween), 0.01) * Math.log(countsOfEvent);
                } else if (daysBetween <= 30) {
                    heat = (double) (31 - daysBetween) / 30000 + 0.00001 * countsOfEvent;
                } else if (daysBetween <= 365) {
                    heat = (double) (366 - daysBetween) / 365000;
                } else {
                    heat = (double) 1 / 365000;
                }
                eventDetails.put("heat", heat);
                jsonArray.add(eventDetails);
            }

            jsonArray.sort(new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    double heat1 = o1.getDouble("heat");
                    double heat2 = o2.getDouble("heat");
                    return Double.compare(heat2, heat1); // Order by descending
                }
            });

            // Create a new JSONArray from the sorted list
            JSONArray sortedJsonArray = new JSONArray();
            sortedJsonArray.addAll(jsonArray);

            return sortedJsonArray;
        }
        return new JSONArray();
    }
}
