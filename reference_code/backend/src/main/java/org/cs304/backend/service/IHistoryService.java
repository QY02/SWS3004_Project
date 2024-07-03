package org.cs304.backend.service;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import org.cs304.backend.entity.History;

public interface IHistoryService extends IService<History> {
    void addEventHistory(String userId, int eventId);
    JSONArray getAllHistory(String userId);

}
