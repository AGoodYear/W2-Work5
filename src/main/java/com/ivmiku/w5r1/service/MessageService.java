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

/**
 * @author Aurora
 */
@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ChatIdMapper chatIdMapper;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取会话id
     * @param user1Id 用户1id
     * @param user2Id 用户2id
     * @return 查询结果
     */
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

    /**
     * 聊天记录写redis
     * @param msg 要写入的信息
     * @throws ParseException
     */
    @Async
    public void insertToRedis(Message msg) throws ParseException {
        if (!(msg.getMessage().length() >1000)) {
            if (redisUtil.getZsetSize("history:" + msg.getChatId()) >= 50) {
                redisUtil.zsetRightPop("history:" + msg.getChatId());
            }
            redisUtil.zsetAdd("history:" + msg.getChatId(), msg);
            redisUtil.setExpireTime("history:" + msg.getChatId());
        }
    }

    /**
     * 聊天记录写入mysql
     * @param msg 要写入的消息
     */
    @Async
    public void insertToMysql(Message msg) {
        if (!(msg.getMessage().length() >1000)) {
            messageMapper.insert(msg);
        }
    }

    /**
     * 未读消息写入redis
     * @param userId 用户id
     * @param msg 未读消息
     */
    @Async
    public void insertUnreadMsg(String userId, Message msg) {
        redisUtil.listAdd("unread:" + userId, msg);
    }

    /**
     * 获取未读消息列表
     * @param userId 用户id
     * @return 查询结果
     */
    public List<Message> getUnreadMsg(String userId) {
        List<Message> result = redisUtil.listGet("unread:" + userId, 0, -1);
        redisUtil.listClear(userId);
        return result;
    }

    /**
     * 从数据库获取聊天记录
     * @param chatId 会话id
     * @param current 分页参数
     * @param size 分页参数
     * @return 返回的查询结果
     */
    public List<Message> getChatHistoryFromDB(String chatId, int current, int size) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        Page<Message> page = new Page<>(current, size);
        queryWrapper.eq("chat_id", chatId);
        queryWrapper.orderByDesc("date");
        return messageMapper.selectPage(page, queryWrapper).getRecords();
    }

    /**
     * 从数据库获取聊天记录，查询一定范围内
     * @param chatId 会话id
     * @param current 分页参数
     * @param size 分页参数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 返回的查询结果
     */
    public List<Message> getChatHistoryFromDBByDate(String chatId, int current, int size, String startDate, String endDate) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("date", startDate, endDate);
        Page<Message> page = new Page<>(current, size);
        queryWrapper.eq("chat_id", chatId);
        queryWrapper.orderByDesc("date");
        return messageMapper.selectPage(page, queryWrapper).getRecords();
    }

    /**
     * redis获取聊天记录
     * @param chatId 会话id
     * @param s 开始
     * @param e 结束
     * @return 查询结果
     */
    public List<Message> getChatHistoryFromRedis(String chatId, int s, int e) {
        return redisUtil.zsetGet("history:" + chatId, s, e);
    }

    /**
     * 查询聊天记录
     * @param user1Id 用户1id
     * @param user2Id 用户2id
     * @param page 分页参数
     * @param size 分页参数
     * @return 查询结果
     * @throws ParseException
     */
    public List<Message> getChatHistory(String user1Id, String user2Id, int page, int size) throws ParseException {
        int start = page * size - size;
        int end = page * size - 1;
        String chatId = getChatId(user1Id, user2Id);
        loadCache(chatId);
        List<Message> result = new ArrayList<>(redisUtil.zsetGet("history:" + chatId, start, end));
        if ((end -start + 1) == result.size()) {
            return result;
        }
        int redisSize = result.size();
        List<Message> dbList = getChatHistoryFromDB(chatId, ((end - result.size()) / size) + 1, size);
        result.addAll(dbList.subList(redisSize, dbList.size()));
        redisUtil.refreshExpire("history:" + chatId);
        return result;
    }

    /**
     * 在一定时间范围内查询聊天记录
     * @param user1Id 用户1id
     * @param user2Id 用户2id
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param page 分页参数
     * @param size 分页参数
     * @return 查询结果
     * @throws ParseException
     */
    public List<Message> getChatHistoryByDate(String user1Id, String user2Id, String startDate, String endDate, int page, int size) throws ParseException {
        int start = page * size - size;
        int end = page * size - 1;
        String chatId = getChatId(user1Id, user2Id);
        loadCache(chatId);
        List<Message> result = new ArrayList<>(redisUtil.zsetGetByDate("history:" + chatId, startDate, endDate, start, size));
        redisUtil.refreshExpire("history:" + chatId);
        if (result.size() == (end - start + 1)) {
            return result;
        }
        int redisSize = result.size();
        List<Message> dbList = getChatHistoryFromDBByDate(chatId, ((end - result.size()) / size) + 1, size, startDate, endDate).subList(result.size(), size);
        result.addAll(dbList.subList(redisSize, dbList.size()));
        return result;
    }

    /**
     * 获取会话列表
     * @param userId 用户id
     * @param current 分页参数
     * @param size 分页参数
     * @return 查询结果
     */
    public List<ChatId> getChatList(String userId, int current, int size) {
        QueryWrapper<ChatId> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user1_id", userId).or().eq("user2_id", userId);
        queryWrapper.orderByDesc("id");
        Page<ChatId> page = new Page<>(current, size);
        return chatIdMapper.selectPage(page, queryWrapper).getRecords();
    }

    /**
     * 加载聊天记录到redis
     * @param chatId 会话id
     * @throws ParseException
     */
    public void loadCache(String chatId) throws ParseException {
        if (!redisUtil.ifExist("history:" + chatId)) {
            List<Message> list = getChatHistoryFromDB(chatId, 1, 20);
            for (Message message : list) {
                insertToRedis(message);
            }
        }
    }
}
