package org.cs304.backend.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.cs304.backend.constant.constant_EventStatus;
import org.cs304.backend.constant.constant_OrderRecordStatus;
import org.cs304.backend.constant.constant_SeatMapType;
import org.cs304.backend.constant.constant_User;
import org.cs304.backend.entity.*;
import org.cs304.backend.exception.ServiceException;
import org.cs304.backend.mapper.*;
import org.cs304.backend.service.ISeatMapService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SeatMapServiceImpl extends ServiceImpl<SeatMapMapper, SeatMap> implements ISeatMapService {
    @Resource
    private EventMapper eventMapper;

    @Resource
    private EventSessionMapper eventSessionMapper;

    @Resource
    private SeatMapper seatMapper;

    @Resource
    private OrderRecordMapper orderRecordMapper;

    @Override
    public SeatMap getSeatMapWithSeatsById(int userType, Integer seatMapId) {
        List<OrderRecord> orderRecordList = orderRecordMapper.selectList(new QueryWrapper<OrderRecord>().eq("status", constant_OrderRecordStatus.UNPAID).lt("submit_time", LocalDateTime.now().minusMinutes(10)));
        orderRecordList.forEach(orderRecord -> {
            EventSession eventSession = eventSessionMapper.selectById(orderRecord.getEventSessionId());
            seatMapper.update(new UpdateWrapper<Seat>().eq("seat_map_id", eventSession.getSeatMapId()).eq("seat_id", orderRecord.getSeatId()).set("availability", true));
            orderRecordMapper.update(new UpdateWrapper<OrderRecord>().eq("id", orderRecord.getId()).set("status", constant_OrderRecordStatus.EXPIRED));
        });

        SeatMap seatMap = baseMapper.selectById(seatMapId);
        if (seatMap == null) {
            throw new ServiceException("400", "Seat map not exist");
        }
        if ((userType == constant_User.USER) && (seatMap.getType() == 1)) {
            List<Integer> eventIdList = eventSessionMapper.selectList(new QueryWrapper<EventSession>().eq("seat_map_id", seatMapId).eq("visible", true)).stream().map(EventSession::getEventId).collect(Collectors.toList());
            if (eventIdList.isEmpty()) {
                throw new ServiceException("400", "Seat map not exist");
            } else if (!eventMapper.exists(new QueryWrapper<Event>().in("id", eventIdList).eq("status", constant_EventStatus.PASSED).eq("visible", true))) {
                throw new ServiceException("400", "Seat map not exist");
            }
        }
        JSONObject seatMapData = JSONObject.parseObject(seatMap.getData());
        JSONArray seatDataJSONArray = seatMapData.getJSONArray("seats");
        List<String> seatIdList = seatDataJSONArray.stream().map(seat -> ((JSONObject) seat).getString("id")).collect(Collectors.toList());
        Map<String, Seat> seatDetailedDataMap = seatMapper.selectList(new QueryWrapper<Seat>().in("seat_id", seatIdList).eq("seat_map_id", seatMapId)).stream().collect(Collectors.toMap(Seat::getSeatId, seat -> seat));
        seatDataJSONArray.forEach(seat -> {
            JSONObject seatDataJSONObject = (JSONObject) seat;
            Seat seatDetailedData = seatDetailedDataMap.get(seatDataJSONObject.getString("id"));
            seatDataJSONObject.put("type", seatDetailedData.getType());
            seatDataJSONObject.put("availability", seatDetailedData.getAvailability());
            seatDataJSONObject.put("price", seatDetailedData.getPrice());
        });
        seatMap.setData(null);
        seatMap.setDetailedData(seatMapData);
        return seatMap;
    }

    @Override
    public JSONArray getAllSeatMapTemplate() {
        List<SeatMapInfo> seatMapInfoList = new ArrayList<>();
        baseMapper.selectList(new QueryWrapper<SeatMap>().eq("type", constant_SeatMapType.TEMPLATE).select("id", "name")).forEach(seatMap -> {
            String[] nameArray = seatMap.getName().split("/");
            List<SeatMapInfo> currentSeatMapInfoList = seatMapInfoList;
            String currentValue = null;
            for (int i = 0; i < nameArray.length; i++) {
                SeatMapInfo currentSeatMapInfo = new SeatMapInfo();
                currentSeatMapInfo.setLabel(nameArray[i]);
                if (currentSeatMapInfoList.contains(currentSeatMapInfo)) {
                    SeatMapInfo currentNode = currentSeatMapInfoList.get(currentSeatMapInfoList.indexOf(currentSeatMapInfo));
                    currentValue = currentNode.getValue();
                    currentSeatMapInfoList = currentNode.getChildren();
                }
                else {
                    if (i < nameArray.length - 1) {
                        if (currentSeatMapInfoList.isEmpty()) {
                            if (currentValue == null) {
                                currentValue = "0";
                                currentSeatMapInfo.setValue(currentValue);
                            }
                            else {
                                currentValue = currentValue + ".0";
                                currentSeatMapInfo.setValue(currentValue);
                            }
                        }
                        else {
                            if (currentValue == null) {
                                String prevNodeValue = currentSeatMapInfoList.get(currentSeatMapInfoList.size() - 1).getValue();
                                int maxValue;
                                try {
                                    maxValue = Integer.parseInt(prevNodeValue);
                                }
                                catch (NumberFormatException e) {
                                    maxValue = Integer.parseInt(prevNodeValue.split("\\.")[0]);
                                }
                                currentValue = String.valueOf((maxValue + 1));
                                currentSeatMapInfo.setValue(currentValue);
                            }
                            else {
                                String[] maxValueStringArray = currentSeatMapInfoList.get(currentSeatMapInfoList.size() - 1).getValue().split("\\.");
                                int maxValue;
                                try {
                                    maxValue = Integer.parseInt(maxValueStringArray[maxValueStringArray.length - 1]);
                                }
                                catch (NumberFormatException e) {
                                    maxValue = Integer.parseInt(maxValueStringArray[maxValueStringArray.length - 2]);
                                }
                                currentValue = currentValue + "." + (maxValue + 1);
                                currentSeatMapInfo.setValue(currentValue);
                            }
                        }
                    }
                    else {
                        if (currentValue == null) {
                            currentValue = String.valueOf(seatMap.getId());
                            currentSeatMapInfo.setValue(currentValue + "." + seatMap.getName());
                        }
                        else {
                            currentValue = currentValue + "." + seatMap.getId();
                            currentSeatMapInfo.setValue(currentValue + "." + seatMap.getName());
                        }
                    }
                    currentSeatMapInfoList.add(currentSeatMapInfo);
                    currentSeatMapInfoList = currentSeatMapInfo.getChildren();
                }
            }
        });
        return JSONArray.from(seatMapInfoList);
    }
}
