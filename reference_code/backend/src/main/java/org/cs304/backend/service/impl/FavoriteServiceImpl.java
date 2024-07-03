package org.cs304.backend.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.cs304.backend.entity.Event;
import org.cs304.backend.entity.Favorite;
import org.cs304.backend.exception.ServiceException;
import org.cs304.backend.mapper.EventMapper;
import org.cs304.backend.mapper.FavoriteMapper;
import org.cs304.backend.service.IFavoriteService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements IFavoriteService {

    @Resource
    private FavoriteMapper favoriteMapper;

    @Resource
    private EventMapper eventMapper;
    @Override
    public void deleteFavorite(@NotNull Favorite favorite) {
        if (favorite.getEventId() == null || favorite.getUserId()==null) {
            throw new ServiceException("400", "Invalid data");
        }
        favoriteMapper.delete(new QueryWrapper<Favorite>().eq("user_id",favorite.getUserId()).eq("event_id",favorite.getEventId()));
//        favoriteMapper.deleteByUserIdAndEventId(favorite.getUserId(), favorite.getEventId());
    }

    @Override
    public JSONArray getAllFavorite(String userId) {
//        History history = baseMapper.(userId);
        List<Favorite> list = baseMapper.selectList(new QueryWrapper<Favorite>().eq("user_id", userId));

        if (list != null) {
            JSONArray jsonArray = new JSONArray();
            Set<Integer> seenEventIds = new HashSet<>();
            // 遍历历史记录
            for (Favorite favorite : list) {
                if (!seenEventIds.contains(favorite.getEventId())) {
                    // 获取每个事件的详细信息
                    Event event = eventMapper.selectById(favorite.getEventId());
                    if (event != null) {

                        // 添加到 JSON 数组
                        jsonArray.add(event);
                        seenEventIds.add(favorite.getEventId());
                    }
                }
            }
            return jsonArray;
        }
        return new JSONArray();
    }
}
