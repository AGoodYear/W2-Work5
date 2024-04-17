package com.ivmiku.w5r1.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("friend")
public class Friend {
    private String user1Id;
    private String user2Id;
}
