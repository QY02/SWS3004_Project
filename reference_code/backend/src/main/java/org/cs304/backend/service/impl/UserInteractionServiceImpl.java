package org.cs304.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.cs304.backend.entity.UserInteraction;
import org.cs304.backend.mapper.UserInteractionMapper;
import org.cs304.backend.service.IUserInteractionService;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.cs304.backend.constant.constant_InteractionType.*;

@Service
@Slf4j
//更新交互，传入交互的用户、活动、交互类型、交互分数（只有帖子和评星要传）
public class UserInteractionServiceImpl extends ServiceImpl<UserInteractionMapper, UserInteraction> implements IUserInteractionService{
    @Override
    public void changeUserInteraction(String userId, Integer eventId, Integer type, Integer action) {
        List<UserInteraction> list = baseMapper.selectList(new QueryWrapper<UserInteraction>().eq("event_id",eventId).eq("user_id",userId));
        UserInteraction userInteraction = list.isEmpty() ? null : list.get(0);
        if (userInteraction == null) {
            userInteraction = new UserInteraction();
            userInteraction.setUserId(userId);
            userInteraction.setEventId(eventId);
            changeRating(type, action, userInteraction);
            userInteraction.setUpdateType(type);
            baseMapper.insert(userInteraction);
        } else {
            if (type>=userInteraction.getUpdateType()){
                changeRating(type, action, userInteraction);
                baseMapper.update(new UpdateWrapper<UserInteraction>().eq("event_id",eventId).eq("user_id",userId).set("update_type",type).set("rating",userInteraction.getRating()));
            }
        }
    }

    private void changeRating(Integer type, Integer action, UserInteraction userInteraction) {
        switch (type){
            case STAR:
            case BLOG:
                userInteraction.setRating(action);
                break;
            case FAVORITE:
                userInteraction.setRating(5);
                break;
            case ATTEND:
                userInteraction.setRating(4);
                break;
            case HISTORY:
                userInteraction.setRating(3);
                break;
        }
    }
}
