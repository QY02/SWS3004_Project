package org.nus.cloud.registerService.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.nus.cloud.registerService.entity.User;

public interface IUserService extends IService<User> {

    String register(JSONObject data);
}