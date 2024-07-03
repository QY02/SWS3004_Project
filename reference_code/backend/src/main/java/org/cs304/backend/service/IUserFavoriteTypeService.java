package org.cs304.backend.service;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import org.cs304.backend.entity.UserFavoriteType;

public interface IUserFavoriteTypeService extends IService<UserFavoriteType> {
    JSONArray getAllType(String userId);

    JSONArray changeType(String userId, String newTypes);
}
