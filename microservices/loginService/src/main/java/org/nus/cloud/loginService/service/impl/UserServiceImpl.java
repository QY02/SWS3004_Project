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
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (user.getId() != null) {
            queryWrapper.eq("id", user.getId());
        } else {
            queryWrapper.eq("email", user.getEmail());
        }
        queryWrapper.eq("password", user.getPassword());
        User dbuser = baseMapper.selectOne(queryWrapper);
        if (dbuser == null) {
            throw new ServiceException("400", "Invalid login information");
        }
        return redisUtil.generateToken(dbuser);
    }
}