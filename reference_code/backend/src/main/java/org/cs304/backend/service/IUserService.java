package org.cs304.backend.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.cs304.backend.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IUserService extends IService<User> {

    User login(User user);

    void registerSearch(User user);

    User loginWithEmail(JSONObject jsonObject);

    void sendEmail(String email);

    void verifyAndSendEmail(String email);

    @Transactional(rollbackFor = Exception.class)
    void deleteUser(String id);
    @Transactional(rollbackFor = Exception.class)
    void deleteUsers(List<String> ids);
}