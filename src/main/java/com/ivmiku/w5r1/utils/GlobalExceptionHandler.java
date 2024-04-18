package com.ivmiku.w5r1.utils;

import cn.dev33.satoken.exception.SaTokenException;
import com.alibaba.fastjson2.JSON;
import com.ivmiku.w5r1.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;
import java.text.ParseException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = SaTokenException.class)
    @ResponseBody
    public Object SaTokenExceptionHandler(SaTokenException e) {
        log.error("SaTokenException:" + e.getLocalizedMessage());
        return JSON.toJSON(Result.error("身份认证错误：" + e.getMessage()));
    }

    @ExceptionHandler(value = ParseException.class)
    @ResponseBody
    public Object parseExceptionHandler(ParseException e) {

        log.error("ParseException:" + e.getLocalizedMessage());
        return JSON.toJSON(Result.error("请检查日期是否合法"));
    }

    @ExceptionHandler(value = SQLException.class)
    @ResponseBody
    public Object SQLExceptionHandler(SQLException e) {
        log.error("SQLException:" + e.getLocalizedMessage());
        return JSON.toJSON(Result.error("数据库出错！（内部错误）\n" + e.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object exceptionHandler(Exception e) {
        log.error("Exception:" + e.getLocalizedMessage());
        return JSON.toJSON(Result.error("服务器内部错误:" + e.getMessage()));
    }
}
