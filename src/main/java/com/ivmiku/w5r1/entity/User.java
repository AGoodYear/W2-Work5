package com.ivmiku.w5r1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * 用户实体类
 * @author Aurora
 */
@Data
@TableName("user")
public class User {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String username;
    private String password;
    private String salt;
    private String avatarUrl;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
}
