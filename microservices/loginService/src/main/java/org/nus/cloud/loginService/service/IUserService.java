package org.nus.cloud.loginService.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.nus.cloud.loginService.entity.User;

public interface IUserService extends IService<User> {

    User login(User user);
}