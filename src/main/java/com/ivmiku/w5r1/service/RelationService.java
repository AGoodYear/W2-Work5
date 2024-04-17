package com.ivmiku.w5r1.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ivmiku.w5r1.entity.ChatId;
import com.ivmiku.w5r1.entity.Friend;
import com.ivmiku.w5r1.entity.IgnoreUser;
import com.ivmiku.w5r1.mapper.BlackListMapper;
import com.ivmiku.w5r1.mapper.FriendMapper;
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

    public void IgnoreUser(String userId, String toIgnore) {
        blackListMapper.insert(new IgnoreUser(userId, toIgnore));
    }

    public List<String> getIgnoreUser(String userId) {
        QueryWrapper<IgnoreUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<IgnoreUser> list = blackListMapper.selectList(queryWrapper);
        List<String> result = new ArrayList<>();
        if (list != null) {
            for (IgnoreUser object : list) {
                result.add(object.getToIgnore());
            }
        }
        return result;
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
}
