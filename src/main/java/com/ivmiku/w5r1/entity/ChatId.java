package com.ivmiku.w5r1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("chatid")
public class ChatId {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String user1Id;
    private String user2Id;
}
