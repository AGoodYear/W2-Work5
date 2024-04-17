package com.ivmiku.w5r1.utils;

import cn.dev33.satoken.exception.SaTokenException;
import com.alibaba.fastjson2.JSON;
import com.ivmiku.w5r1.entity.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;
import java.text.ParseException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = SaTokenException.class)
    @ResponseBody
    public Object SaTokenExceptionHandler(SaTokenException e) {
        return JSON.toJSON(Result.error(e.getMessage()));
    }

    @ExceptionHandler(value = ParseException.class)
    @ResponseBody
    public Object parseExceptionHandler(ParseException e) {
        return JSON.toJSON(Result.error("请检查日期是否合法"));
    }

    @ExceptionHandler(value = SQLException.class)
    @ResponseBody
    public Object SQLExceptionHandler(SQLException e) {
        return JSON.toJSON(Result.error("数据库出错！（内部错误）\n" + e.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object exceptionHandler(Exception e) {
        return JSON.toJSON(Result.error("服务器内部错误"));
    }
}
