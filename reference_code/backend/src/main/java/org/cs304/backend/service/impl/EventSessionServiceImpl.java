package org.cs304.backend.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.cs304.backend.constant.constant_SeatMapType;
import org.cs304.backend.entity.EventSession;
import org.cs304.backend.entity.Seat;
import org.cs304.backend.entity.SeatMap;
import org.cs304.backend.exception.ServiceException;
import org.cs304.backend.mapper.EventSessionMapper;
import org.cs304.backend.mapper.SeatMapMapper;
import org.cs304.backend.mapper.SeatMapper;
import org.cs304.backend.service.IEventSessionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EventSessionServiceImpl extends ServiceImpl<EventSessionMapper, EventSession> implements IEventSessionService {
    @Resource
    private EventSessionMapper eventSessionMapper;

    @Resource
    private SeatMapMapper seatMapMapper;

    @Resource
    private SeatMapper seatMapper;

    @Override
    public void insertEventSession(int id,JSONObject sessionData){
        EventSession session = new EventSession();

        // 设置 event_id
        session.setEventId(id);

        // 设置 registration_required
        Boolean registration_required=sessionData.getBoolean("registration_required");
        session.setRegistrationRequired(registration_required);
        if (registration_required){
            // 设置 registration_start_time 和 registration_end_time
            JSONArray registrationTimeRange = sessionData.getJSONArray("registration_time_range");
            if (registrationTimeRange.size()==2){
                LocalDateTime registrationStartTime = parseDateTime(registrationTimeRange.getString(0));
                LocalDateTime registrationEndTime = parseDateTime(registrationTimeRange.getString(1));
                session.setRegistrationStartTime(registrationStartTime);
                session.setRegistrationEndTime(registrationEndTime);
            }else {
                throw new ServiceException("400", "Invalid registration_time_range");
            }
        }
        
        // 设置 start_time 和 end_time
        JSONArray eventTimeRange = sessionData.getJSONArray("event_time_range");
        if (eventTimeRange.size()==2){
            LocalDateTime startTime = parseDateTime(eventTimeRange.getString(0));
            LocalDateTime endTime = parseDateTime(eventTimeRange.getString(1));
            session.setStartTime(startTime);
            session.setEndTime(endTime);
        }else {
            throw new ServiceException("400", "Invalid event_time_range");
        }

        // 设置 min_size 和 max_size
        int minSize = sessionData.getInteger("min_cnt");
        int maxSize = sessionData.getInteger("max_cnt");
        session.setMinSize(minSize);
        session.setMaxSize(maxSize);

        session.setCurrentSize(0);
        Integer seatMapTemplateId = sessionData.getInteger("seat_map_id");
        if (seatMapTemplateId == -1) {
            session.setSeatMapId(seatMapTemplateId);
            session.setPrice(sessionData.getInteger("price"));
        } else {
            SeatMap seatMap = seatMapMapper.selectById(seatMapTemplateId);
            seatMap.setId(null);
            seatMap.setType(constant_SeatMapType.INSTANCE);
            seatMapMapper.insert(seatMap);
            List<Seat> seatList = seatMapper.selectList(new QueryWrapper<Seat>().eq("seat_map_id", seatMapTemplateId));
            seatList.forEach(seat -> {
                seat.setSeatMapId(seatMap.getId());
                seatMapper.insert(seat);
            });
            session.setSeatMapId(seatMap.getId());
        }

        session.setVenue(sessionData.getString("venue"));


        // location 数组去除最外面的括号
        // 数组可以直接当str读，保留中括号
        String location_arr=sessionData.getString("location");
        String location=location_arr.replace("[", "").replace("]", "");
        session.setLocation(location);

        String addi_info_str=sessionData.getString("additional_information_required");
        session.setAdditionalInformationRequired(addi_info_str);
        session.setVisible(sessionData.getBoolean("visible"));

        // 打印 Event 对象的属性值
//        System.out.println("Session Object: " + session);

        eventSessionMapper.insert(session);
    }
    private LocalDateTime parseDateTime(String dateTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateTimeStr, formatter);
    }
}


