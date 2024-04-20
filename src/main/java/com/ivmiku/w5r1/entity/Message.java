package com.ivmiku.w5r1.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.alibaba.fastjson2.annotation.JSONType;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Aurora
 */
@Data
@TableName("message")
public class Message implements Serializable {
    @JSONField(ordinal = 1)
    private String chatId;
    @JSONField(ordinal = 2)
    private String fromId;
    @JSONField(ordinal = 3)
    private String toId;
    @JSONField(ordinal = 4)
    private String message;
    @JSONField(ordinal = 5)
    private String date;
}
