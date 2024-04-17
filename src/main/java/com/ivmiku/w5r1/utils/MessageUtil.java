package com.ivmiku.w5r1.utils;

import com.ivmiku.w5r1.entity.ChatId;
import com.ivmiku.w5r1.mapper.ChatIdMapper;
import com.ivmiku.w5r1.service.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.List;

@Component
public class MessageUtil {
    private static final String[] SENSITIVE = {"你妈", "你妈逼的"};

    private static RelationService relationService = null;

    public MessageUtil(RelationService relationService) {
        MessageUtil.relationService = relationService;
    }

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

    public static boolean ifIgnored(String userId, String toIgnore) {
        List<String> list = relationService.getIgnoreUser(userId);
        return list.contains(toIgnore);
    }
}
