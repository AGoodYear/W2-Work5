package com.ivmiku.w5r1.entity;

import com.alibaba.fastjson2.annotation.JSONType;
import lombok.Data;

/**
 * @author Aurora
 */
@Data
@JSONType(alphabetic = false)
public class UserInfo {
    private String id;
    private String username;
    private String avatarUrl;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    public void setInfo(User user) {
        id = user.getId();
        username = user.getUsername();
        avatarUrl = user.getAvatarUrl();
        createdAt = user.getCreatedAt();
        updatedAt = user.getUpdatedAt();
        deletedAt = user.getDeletedAt();
    }
}
