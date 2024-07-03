package org.cs304.backend.service;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import org.cs304.backend.entity.Favorite;

public interface IFavoriteService extends IService<Favorite> {
    void deleteFavorite(Favorite favorite);

    JSONArray getAllFavorite(String userId);
}
