package org.nus.cloud.eventDetailedDataService.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nus.cloud.eventDetailedDataService.entity.Seat;
import org.nus.cloud.eventDetailedDataService.entity.SeatMap;
import org.nus.cloud.eventDetailedDataService.exception.ServiceException;
import org.nus.cloud.eventDetailedDataService.mapper.SeatMapMapper;
import org.nus.cloud.eventDetailedDataService.mapper.SeatMapper;
import org.nus.cloud.eventDetailedDataService.service.ISeatMapService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SeatMapServiceImpl extends ServiceImpl<SeatMapMapper, SeatMap> implements ISeatMapService {

    @Resource
    private SeatMapper seatMapper;

    @Override
    public SeatMap getById(JSONObject requestData) {
        Integer id = requestData.getInteger("id");
        if (id == null) {
            throw new ServiceException("400", "Invalid seat map id");
        }
        SeatMap seatMap = baseMapper.selectById(id);
        seatMap.setDataJson(JSONObject.parseObject(seatMap.getData()));
        seatMap.setData(null);
        return seatMap;
    }

    @Override
    public SeatMap getWithSeatsById(JSONObject requestData) {
        Integer id = requestData.getInteger("id");
        if (id == null) {
            throw new ServiceException("400", "Invalid seat map id");
        }
        SeatMap seatMap = baseMapper.selectById(id);
        if (seatMap == null) {
            throw new ServiceException("400", "Seat map not exist");
        }
        JSONObject seatMapData = JSONObject.parseObject(seatMap.getData());
        JSONArray seatDataJSONArray = seatMapData.getJSONArray("seats");
        List<String> seatIdList = seatDataJSONArray.stream().map(seat -> ((JSONObject) seat).getString("id")).collect(Collectors.toList());
        Map<String, Seat> seatDetailedDataMap = seatMapper.selectList(new QueryWrapper<Seat>().in("seat_id", seatIdList).eq("seat_map_id", id)).stream().collect(Collectors.toMap(Seat::getSeatId, seat -> seat));
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
}