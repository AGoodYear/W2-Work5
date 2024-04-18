package com.ivmiku.w5r1.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ivmiku.w5r1.entity.ChatId;
import com.ivmiku.w5r1.entity.Message;
import com.ivmiku.w5r1.mapper.ChatIdMapper;
import com.ivmiku.w5r1.mapper.MessageMapper;
import com.ivmiku.w5r1.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ChatIdMapper chatIdMapper;

    @Autowired
    private RedisUtil redisUtil;

    public String getChatId(String user1Id, String user2Id) {
        if (Objects.equals(user1Id, "public") || Objects.equals(user2Id, "public")) {
            return "0";
        }
        String id1, id2;
        if (user1Id.compareTo(user2Id) < 0) {
            id1 = user1Id;
            id2 = user2Id;
        } else {
            id1 = user2Id;
            id2 = user1Id;
        }
        QueryWrapper<ChatId> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user1_id", id1);
        queryWrapper.eq("user2_id", id2);
        ChatId chatId = chatIdMapper.selectOne(queryWrapper);
        if (chatId == null) {
            chatId = new ChatId();
            chatId.setUser1Id(id1);
            chatId.setUser2Id(id2);
            chatIdMapper.insert(chatId);
        }
        return chatIdMapper.selectOne(queryWrapper).getId();
    }

    @Async
    public void insertToRedis(Message msg) throws ParseException {
        if (!(msg.getMessage().length() >1000)) {
            if (redisUtil.getZsetSize("history:" + msg.getChatId()) >= 50) {
                insertToMysql(redisUtil.zsetRightPop("history:" + msg.getChatId()));
            }
            redisUtil.zsetAdd("history:" + msg.getChatId(), msg);
        }
    }

    @Async
    public void insertToMysql(Message msg) {
        if (!(msg.getMessage().length() >1000)) {
            messageMapper.insert(msg);
        }
    }

    @Async
    public void insertUnreadMsg(String userId, Message msg) {
        redisUtil.listAdd("unread:" + userId, msg);
    }

    public List<Message> getUnreadMsg(String userId) {
        List<Message> result = redisUtil.listGet("unread:" + userId, 0, -1);
        redisUtil.listClear(userId);
        return result;
    }

    public List<Message> getChatHistoryFromDB(String chatId, int current, int size) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        Page<Message> page = new Page<>(current, size);
        queryWrapper.eq("chat_id", chatId);
        queryWrapper.orderByDesc("date");
        return messageMapper.selectPage(page, queryWrapper).getRecords();
    }

    public List<Message> getChatHistoryFromRedis(String chatId, int s, int e) {
        return redisUtil.zsetGet("history:" + chatId, s, e);
    }

    public List<Message> getChatHistory(String user1Id, String user2Id, int page, int size) {
        int start = page * size - size;
        int end = page * size - 1;
        String chatId = getChatId(user1Id, user2Id);
        Long redisSize = redisUtil.getzsetSize("history:" + chatId);
        List<Message> result = new ArrayList<>();
        if (end > redisSize - 1) {
            if (start >redisSize - 1 ) {
                result.addAll(getChatHistoryFromDB(chatId, (int) (((end - redisSize) / size) + 1), size));
            } else {
                result.addAll(getChatHistoryFromRedis(chatId, start, (int) (redisSize-1)));
                result.addAll(getChatHistoryFromDB(chatId, (int) (((end - redisSize) / size) + 1),size));
            }
        } else {
            result.addAll(getChatHistoryFromRedis(chatId, start, end));
        }
        return result;
    }

    public List<Message> getChatHistoryByDate(String user1Id, String user2Id, String startDate, String endDate) throws ParseException {
        List<Message> result = new ArrayList<>();
        String chatId = getChatId(user1Id, user2Id);
        result.addAll(redisUtil.zsetGetByDate("history:" + chatId, startDate, endDate));
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("date", startDate, endDate);
        result.addAll(messageMapper.selectList(queryWrapper));
        return result;
    }

    public List<ChatId> getChatList(String userId, int current, int size) {
        QueryWrapper<ChatId> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user1_id", userId).or().eq("user2_id", userId);
        queryWrapper.orderByDesc("id");
        Page<ChatId> page = new Page<>(current, size);
        return chatIdMapper.selectPage(page, queryWrapper).getRecords();
    }
}
