package org.nus.cloud.registerService.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.nus.cloud.registerService.entity.User;
import org.nus.cloud.registerService.exception.ServiceException;
import org.nus.cloud.registerService.mapper.UserMapper;
import org.nus.cloud.registerService.service.IUserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public String register(HttpServletRequest request, JSONObject requestData) {
        String email = request.getHeader("email");
        if (email == null) {
            throw new ServiceException("400", "Invalid email");
        }
        String routingHash = request.getHeader("routingHash");
        if (routingHash == null) {
            throw new ServiceException("500", "Invalid routingHash");
        }
        String name = requestData.getString("name");
        if (name == null) {
            throw new ServiceException("400", "Invalid name");
        }
        String password = requestData.getString("password");
        if (password == null) {
            throw new ServiceException("400", "Invalid password");
        }

        User user = new User(null, routingHash, name, password, email);
        try {
            baseMapper.insert(user);
            return routingHash + user.getId();
        } catch (DuplicateKeyException e) {
            throw new ServiceException("400", "This email has already been registered");
        }
    }
}