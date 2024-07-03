package org.cs304.backend.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.cs304.backend.entity.UserBlogInteraction;
import org.springframework.transaction.annotation.Transactional;

public interface IUserBlogInteractionService extends IService<UserBlogInteraction> {
    JSONObject getBlog(Integer commentId, String userId);
    @Transactional(rollbackFor = Exception.class)
    void changeVote(Integer commentId, String userId, Integer voteType);
}
