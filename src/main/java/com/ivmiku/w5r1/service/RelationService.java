package com.ivmiku.w5r1.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ivmiku.w5r1.entity.ChatId;
import com.ivmiku.w5r1.entity.Friend;
import com.ivmiku.w5r1.entity.IgnoreUser;
import com.ivmiku.w5r1.mapper.BlackListMapper;
import com.ivmiku.w5r1.mapper.FriendMapper;
import com.ivmiku.w5r1.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RelationService {
    @Autowired
    private BlackListMapper blackListMapper;

    @Autowired
    private FriendMapper friendMapper;

    @Autowired
    private RedisUtil redisUtil;

    public void IgnoreUser(String userId, String toIgnore) {
        blackListMapper.insert(new IgnoreUser(userId, toIgnore));
        if  (redisUtil.ifExist("blacklist:" + userId)) {
            redisUtil.listAdd("blacklist:" + userId, toIgnore);
        }
    }

    public boolean ifIgnored(String userId, String ignoreId) {
        loadCache(userId);
        List<String> blackList = redisUtil.getStringList("blacklist:" + userId, 0, -1);
        redisUtil.refreshExpire("blacklist:" + userId);
        return blackList.contains(ignoreId);
    }

    public void makeFriend(String user1Id, String user2Id) {
        String id1, id2;
        if (user1Id.compareTo(user2Id) < 0) {
            id1 = user1Id;
            id2 = user2Id;
        } else {
            id1 = user2Id;
            id2 = user1Id;
        }
        friendMapper.insert(new Friend(id1, id2));
    }

    public List<Friend> getFriendList(String userId, int current, int size) {
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user1_id", userId).or().eq("user2_id", userId);
        Page<Friend> page = new Page<>(current, size);
        return friendMapper.selectPage(page, queryWrapper).getRecords();
    }

    public void loadCache(String userId) {
        if (!redisUtil.ifExist("blacklist:" + userId)) {
            QueryWrapper<IgnoreUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            List<IgnoreUser> list = blackListMapper.selectList(queryWrapper);
            List<String> result = new ArrayList<>();
            if (list != null) {
                for (IgnoreUser object : list) {
                    result.add(object.getToIgnore());
                }
            }
            for (String toIgnore : result) {
                redisUtil.listAdd("blacklist:" + userId, toIgnore);
            }
            redisUtil.setExpireTime("blacklist:" + userId);
        }
    }
}
