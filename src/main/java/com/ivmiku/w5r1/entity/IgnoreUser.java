package com.ivmiku.w5r1.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Aurora
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("blacklist")
public class IgnoreUser {
    @JSONField(ordinal = 1)
    private String userId;
    @JSONField(ordinal = 2)
    private String toIgnore;
}
