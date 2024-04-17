package com.ivmiku.w5r1.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ivmiku.w5r1.entity.Result;
import com.ivmiku.w5r1.entity.User;
import com.ivmiku.w5r1.entity.UserInfo;
import com.ivmiku.w5r1.mapper.UserMapper;
import com.ivmiku.w5r1.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User selectUserByname(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    public String login(String username, String password) {
        User user = selectUserByname(username);
        String encrypted = PasswordUtil.encrypt(password, user.getSalt());
        if (encrypted.equals(user.getPassword())) {
            StpUtil.login(user.getId());
            return StpUtil.getTokenValue();
        } else {
            return null;
        }
    }

    public Result register(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setSalt(PasswordUtil.getSalt(10));
        user.setPassword(PasswordUtil.encrypt(password, user.getSalt()));
        try {
            userMapper.insert(user);
        } catch (Exception e) {
            return Result.error();
        }
        return Result.ok();
    }

    public Result getUserInfo(String userId) {
        Result result;
        User user;
        if (Objects.equals(userId, "")) {
            user = userMapper.selectById((String) StpUtil.getTokenInfo().getLoginId());
        } else {
            user = userMapper.selectById(userId);
        }
        if (user == null) {
            result = Result.error();
            result.setMessage("用户不存在");
            return result;
        }
        result = Result.ok();
        UserInfo userInfo = new UserInfo();
        userInfo.setInfo(user);
        result.setData(userInfo);
        return result;
    }
}
