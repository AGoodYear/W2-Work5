package com.ivmiku.w5r1.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.alibaba.fastjson2.JSON;
import com.ivmiku.w5r1.entity.Result;
import com.ivmiku.w5r1.entity.UserInput;
import com.ivmiku.w5r1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Object login (@RequestBody UserInput input) {
        Result result;
        String token = userService.login(input.getUsername(), input.getPassword());
        if (token != null) {
            result = Result.ok();
            Map<String, Object> temp = new HashMap<>(1);
            temp.put("token", token);
            result.setData(temp);
        } else {
            result = Result.error();
            result.setMessage("用户名或密码错误");
        }
        return JSON.toJSONString(result);
    }

    @PostMapping("/register")
    public Object register (@RequestBody UserInput input) {
        if (input.getUsername() == null || input.getPassword() == null || input.getUsername().isEmpty() || input.getPassword().isEmpty()) {
            return JSON.toJSONString(Result.error("请输入合法的用户名和密码！"));
        }
        Result result = userService.register(input.getUsername(), input.getPassword());
        if (result.getCode() == -1) {
            result.setMessage("用户名重复");
        }
        return JSON.toJSONString(result);
    }

    @GetMapping("/info")
    @SaCheckLogin
    public Object getUserInfo(@RequestParam String user_id) {
        return JSON.toJSONString(userService.getUserInfo(user_id));
    }
}
