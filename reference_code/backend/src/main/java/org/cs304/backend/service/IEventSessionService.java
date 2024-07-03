package org.cs304.backend.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.cs304.backend.entity.EventSession;
import org.springframework.transaction.annotation.Transactional;

public interface IEventSessionService extends IService<EventSession> {

    @Transactional(rollbackFor = {Exception.class})
    void insertEventSession(int id, JSONObject sessionData);
}
