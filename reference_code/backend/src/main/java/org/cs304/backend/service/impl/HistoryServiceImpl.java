package org.cs304.backend.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.cs304.backend.entity.Event;
import org.cs304.backend.entity.History;
import org.cs304.backend.mapper.EventMapper;
import org.cs304.backend.mapper.HistoryMapper;
import org.cs304.backend.service.IHistoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class HistoryServiceImpl extends ServiceImpl<HistoryMapper, History> implements IHistoryService {
    @Resource
    private HistoryMapper historyMapper;
    @Resource
    private EventMapper eventMapper;
    @Override
    public void addEventHistory(String userId, int eventId) {
        History history=new History();
        history.setEventId(eventId);
        history.setUserId(userId);
        history.setVisitTime(LocalDateTime.now());
//        System.out.println(history.toString());

        // 打印 Event 对象的属性值
//        System.out.println("Event Object: " + event);
        historyMapper.insert(history);
    }

    @Override
    public JSONArray getAllHistory(String userId) {
//        History history = baseMapper.(userId);
        List<History> list = baseMapper.selectList(new QueryWrapper<History>().eq("user_id", userId).orderByDesc("visit_time"));
//        List<Integer> idlist = baseMapper.selectList(new QueryWrapper<History>().eq("user_id", userId).orderByDesc("visit_time"));

//        QueryWrapper<History> queryWrapper = new QueryWrapper<History>();
//        queryWrapper.orderByDesc("visit_time");
//        List<History> list = list(queryWrapper);
        if (list != null) {
            JSONArray jsonArray = new JSONArray();
            Set<Integer> seenEventIds = new HashSet<>();
            // 遍历历史记录
            for (History history : list) {
                if (!seenEventIds.contains(history.getEventId())) {
                    // 获取每个事件的详细信息
                    Event event = eventMapper.selectById(history.getEventId());
                    if (event != null) {
                        // 创建一个新的 JSON 对象来存储事件信息和访问时间
                        JSONObject eventDetails = new JSONObject();
//                        JSONArray jsonArray = new JSONArray();
//                        jsonArray.addAll(list.stream().map(JSON::toJSON).toList());
                        eventDetails.put("id", event.getId());
                        eventDetails.put("name", event.getName());
                        eventDetails.put("content", event.getContent());
                        eventDetails.put("eventPolicy", event.getEventPolicy());
                        eventDetails.put("publishDate", event.getPublishDate());
                        eventDetails.put("type", event.getType());
                        eventDetails.put("visitTime", history.getVisitTime());
                        // 添加到 JSON 数组
                        jsonArray.add(eventDetails);
                        seenEventIds.add(history.getEventId());
                    }
                }
            }
            return jsonArray;
        }
        return new JSONArray();
    }
}
