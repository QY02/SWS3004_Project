package org.nus.cloud.eventGlobalDataService.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nus.cloud.eventGlobalDataService.entity.SeatMapTemplate;
import org.nus.cloud.eventGlobalDataService.entity.SeatMapTemplateInfo;
import org.nus.cloud.eventGlobalDataService.entity.SeatTemplate;
import org.nus.cloud.eventGlobalDataService.exception.ServiceException;
import org.nus.cloud.eventGlobalDataService.mapper.SeatMapTemplateMapper;
import org.nus.cloud.eventGlobalDataService.mapper.SeatTemplateMapper;
import org.nus.cloud.eventGlobalDataService.service.ISeatMapTemplateService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SeatMapTemplateServiceImpl extends ServiceImpl<SeatMapTemplateMapper, SeatMapTemplate> implements ISeatMapTemplateService {

    @Resource
    private SeatTemplateMapper seatTemplateMapper;

    @Override
    public JSONArray getAllSeatMapTemplateName() {
        List<SeatMapTemplateInfo> seatMapTemplateInfoList = new ArrayList<>();
        baseMapper.selectList(new QueryWrapper<SeatMapTemplate>().select("id", "name")).forEach(seatMapTemplate -> {
            String[] nameArray = seatMapTemplate.getName().split("/");
            List<SeatMapTemplateInfo> currentSeatMapTemplateInfoList = seatMapTemplateInfoList;
            String currentValue = null;
            for (int i = 0; i < nameArray.length; i++) {
                SeatMapTemplateInfo currentSeatMapTemplateInfo = new SeatMapTemplateInfo();
                currentSeatMapTemplateInfo.setLabel(nameArray[i]);
                if (currentSeatMapTemplateInfoList.contains(currentSeatMapTemplateInfo)) {
                    SeatMapTemplateInfo currentNode = currentSeatMapTemplateInfoList.get(currentSeatMapTemplateInfoList.indexOf(currentSeatMapTemplateInfo));
                    currentValue = currentNode.getValue();
                    currentSeatMapTemplateInfoList = currentNode.getChildren();
                }
                else {
                    if (i < nameArray.length - 1) {
                        if (currentSeatMapTemplateInfoList.isEmpty()) {
                            if (currentValue == null) {
                                currentValue = "0";
                                currentSeatMapTemplateInfo.setValue(currentValue);
                            }
                            else {
                                currentValue = currentValue + ".0";
                                currentSeatMapTemplateInfo.setValue(currentValue);
                            }
                        }
                        else {
                            if (currentValue == null) {
                                String prevNodeValue = currentSeatMapTemplateInfoList.get(currentSeatMapTemplateInfoList.size() - 1).getValue();
                                int maxValue;
                                try {
                                    maxValue = Integer.parseInt(prevNodeValue);
                                }
                                catch (NumberFormatException e) {
                                    maxValue = Integer.parseInt(prevNodeValue.split("\\.")[0]);
                                }
                                currentValue = String.valueOf((maxValue + 1));
                                currentSeatMapTemplateInfo.setValue(currentValue);
                            }
                            else {
                                String[] maxValueStringArray = currentSeatMapTemplateInfoList.get(currentSeatMapTemplateInfoList.size() - 1).getValue().split("\\.");
                                int maxValue;
                                try {
                                    maxValue = Integer.parseInt(maxValueStringArray[maxValueStringArray.length - 1]);
                                }
                                catch (NumberFormatException e) {
                                    maxValue = Integer.parseInt(maxValueStringArray[maxValueStringArray.length - 2]);
                                }
                                currentValue = currentValue + "." + (maxValue + 1);
                                currentSeatMapTemplateInfo.setValue(currentValue);
                            }
                        }
                    }
                    else {
                        if (currentValue == null) {
                            currentValue = String.valueOf(seatMapTemplate.getId());
                            currentSeatMapTemplateInfo.setValue(currentValue + "." + seatMapTemplate.getName());
                        }
                        else {
                            currentValue = currentValue + "." + seatMapTemplate.getId();
                            currentSeatMapTemplateInfo.setValue(currentValue + "." + seatMapTemplate.getName());
                        }
                    }
                    currentSeatMapTemplateInfoList.add(currentSeatMapTemplateInfo);
                    currentSeatMapTemplateInfoList = currentSeatMapTemplateInfo.getChildren();
                }
            }
        });
        return JSONArray.from(seatMapTemplateInfoList);
    }

    @Override
    public JSONObject getSeatMapTemplateById(JSONObject requestData) {
        Integer id = requestData.getInteger("id");
        if (id == null) {
            throw new ServiceException("400", "Invalid request data");
        }
        SeatMapTemplate seatMapTemplate = baseMapper.selectById(id);
        if (seatMapTemplate == null) {
            throw new ServiceException("400", "The seat map template does not exist");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("seatMapTemplate", seatMapTemplate);
        jsonObject.put("seatTemplateArray", seatTemplateMapper.selectList(new QueryWrapper<SeatTemplate>().eq("seat_map_id", id)));
        return jsonObject;
    }
}