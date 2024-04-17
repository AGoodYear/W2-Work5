package com.ivmiku.w5r1.utils;

import com.ivmiku.w5r1.entity.Message;
import com.ivmiku.w5r1.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.Set;

@Component
public class PersistUtil {
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MessageService messageService;

    @Scheduled(cron = "0 0 4 * * ?")
    public void redisToDB() {
        Set<String> keyList = redisUtil.getKey();
        if (keyList != null) {
            for (String key : keyList) {
                while (redisUtil.getListSize(key) != 0) {
                    Message msg = (Message) redisUtil.rightPop(key);
                    messageService.insertToMysql(msg);
                }
            }
        }
    }
}
