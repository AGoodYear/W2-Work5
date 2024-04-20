package com.ivmiku.w5r1.utils;

import com.ivmiku.w5r1.entity.ChatId;
import com.ivmiku.w5r1.mapper.ChatIdMapper;
import com.ivmiku.w5r1.service.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 敏感词检测
 * @author Aurora
 */
@Component
public class MessageUtil {
    private static final String[] SENSITIVE = {"你妈", "你妈逼的"};

    private static RelationService relationService = null;

    public MessageUtil(RelationService relationService) {
        MessageUtil.relationService = relationService;
    }

    /**
     * 查看发送的信息是否含有敏感词
     * @param message 要发送的信息
     * @return 检查结果
     */
    public static boolean checkMessage(String message) {
        if (message != null) {
            for(String keyword : SENSITIVE) {
                if (message.contains(keyword)){
                    return true;
                }
            }
        }
        return false;
    }

}
