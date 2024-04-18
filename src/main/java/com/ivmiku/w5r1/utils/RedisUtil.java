package com.ivmiku.w5r1.utils;

import com.alibaba.fastjson2.JSON;
import com.ivmiku.w5r1.entity.Message;
import com.ivmiku.w5r1.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void listAdd(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    public List<Message> listGet(String key, int s, int e) {
        List<Object> list = redisTemplate.opsForList().range(key, s, e);
        List<Message> result = new ArrayList<>();
        if (list != null) {
            for (Object json : list) {
                result.add(JSON.parseObject(JSON.toJSONString(json), Message.class));
            }
        }
        return result;
    }

    public void listClear(String key) {
        redisTemplate.opsForList().trim(key, 1, 0);
    }

    public Long getListSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    public Set<String> getKey() {
        return redisTemplate.keys("history:*");
    }

    public Object rightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    public void zsetAdd(String key, Message value) throws ParseException {
        redisTemplate.opsForZSet().add(key, value, DateUtil.toTimeSig(value.getDate()));
    }

    public List<Message> zsetGet(String key, int s, int e) {
        Set<Object> list = redisTemplate.opsForZSet().range(key, s, e);
        List<Message> result = new ArrayList<>();
        if (list != null) {
            for (Object json : list) {
                result.add(JSON.parseObject(JSON.toJSONString(json), Message.class));
            }
        }
        return result;
    }

    public Long getzsetSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    public List<Message> zsetGetByDate(String key, String startDate, String endDate) throws ParseException {
        Set<Object> list = redisTemplate.opsForZSet().reverseRangeByScore(key, DateUtil.toTimeSig(startDate), DateUtil.toTimeSig(endDate));
        List<Message> result = new ArrayList<>();
        if (list != null) {
            for (Object json : list) {
                result.add(JSON.parseObject(JSON.toJSONString(json), Message.class));
            }
        }
        return result;
    }

    public Message zsetRightPop(String key) {
        return JSON.parseObject(JSON.toJSONString(Objects.requireNonNull(redisTemplate.opsForZSet().popMin(key)).getValue()), Message.class);
    }

    public Long getZsetSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }
}
