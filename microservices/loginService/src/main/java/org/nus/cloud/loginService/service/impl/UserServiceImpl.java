package org.nus.cloud.loginService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nus.cloud.loginService.entity.User;
import org.nus.cloud.loginService.exception.ServiceException;
import org.nus.cloud.loginService.mapper.UserMapper;
import org.nus.cloud.loginService.service.IUserService;
import org.nus.cloud.loginService.utils.RedisUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private RedisUtil redisUtil;

    @Override
    public User login(User user) {
        User dbuser = baseMapper.selectOne(new QueryWrapper<User>().eq("id", user.getId()).eq("password", user.getPassword()));
        if (dbuser == null) {
            throw new ServiceException("400", "Invalid login information");
        }
        return redisUtil.generateToken(dbuser);
    }
}