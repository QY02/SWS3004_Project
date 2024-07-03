package org.cs304.backend.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.cs304.backend.entity.UserFavoriteType;
import org.cs304.backend.mapper.UserFavoriteTypeMapper;
import org.cs304.backend.service.IUserFavoriteTypeService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserFavoriteTypeServiceImpl extends ServiceImpl<UserFavoriteTypeMapper, UserFavoriteType> implements IUserFavoriteTypeService {

    @Resource
    private UserFavoriteTypeMapper userFavoriteTypeMapper;
    @Override
    public JSONArray getAllType(String userId) {
        List<UserFavoriteType> list = baseMapper.selectList(new QueryWrapper<UserFavoriteType>().eq("user_id", userId));
        JSONArray jsonArray = new JSONArray();
        for (UserFavoriteType userFavoriteType : list) {
            jsonArray.add(userFavoriteType.getEventType());
        }

        return jsonArray;
    }
    @Override
    public JSONArray changeType(String userId, String favType) {
        // 删除当前用户的所有旧类型
        baseMapper.delete(new QueryWrapper<UserFavoriteType>().eq("user_id", userId));
        favType = favType.substring(1, favType.length() - 1);
        // 分割字符串
        String[] strArray = favType.split(",");
        // 创建一个整型数组，长度与字符串数组相同
        for (int i = 0; i < strArray.length; i++) {
            UserFavoriteType userFavoriteType = new UserFavoriteType();
            userFavoriteType.setUserId(userId);
            userFavoriteType.setEventType(Integer.parseInt(strArray[i].trim()));
//                System.out.println(userFavoriteType);
            userFavoriteTypeMapper.insert(userFavoriteType);
//                intArray[i] = Integer.parseInt(strArray[i].trim()); // trim()移除可能的空白字符
        }

//        // 为每个新类型创建一个新的 UserFavoriteType 对象并插入数据库
//        for (Integer type : newTypes) {
//            UserFavoriteType userFavoriteType = new UserFavoriteType();
//            userFavoriteType.setUserId(userId);
//            userFavoriteType.setEventType(type);
//            baseMapper.insert(userFavoriteType);
//        }
//
//        // 返回新类型的JSONArray
        JSONArray jsonArray = new JSONArray();
//        jsonArray.addAll(newTypes);

        return jsonArray;
    }
}
