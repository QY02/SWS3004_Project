package org.cs304.backend.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.cs304.backend.entity.Comment;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ICommentService extends IService<Comment> {

    List<JSONObject> getAllMoment(Integer momentId, Integer viewType,String userId);

    List<JSONObject> getEventMoment(Integer eventId);

    JSONObject getMomentById(Integer commentId);

    @Transactional(rollbackFor = {Exception.class})
    void deleteMoment(Integer momentId);

    @Transactional(rollbackFor = {Exception.class})
    JSONObject createMomentStart(JSONObject comment, String userId);

    @Transactional(rollbackFor = {Exception.class})
    JSONObject createMomentFinish(JSONObject requestData);
}
