package com.ivmiku.w5r1.entity;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.alibaba.fastjson2.annotation.JSONField;
import com.alibaba.fastjson2.annotation.JSONType;
import lombok.Data;

/**
 * 结果返回类
 * @author Aurora
 */
@Data
public class Result {
    @JSONField(ordinal = 1)
    private int code;
    @JSONField(ordinal = 2)
    private String message;
    @JSONField(ordinal = 3)
    private Object data;

    public static Result ok() {
        Result result = new Result();
        result.code = 10000;
        result.message = "success";
        return result;
    }

    public static Result ok(Object data) {
        Result result = new Result();
        result.code = 10000;
        result.message = "success";
        result.setData(data);
        return result;
    }

    public static Result error() {
        Result result = new Result();
        result.code = -1;
        result.message = "error";
        return result;
    }

    public static Result error(String message) {
        Result result = new Result();
        result.code = -1;
        result.message = message;
        return result;
    }
}
