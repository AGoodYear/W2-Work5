package com.ivmiku.w5r1.entity;

import lombok.Data;

/**
 * @author Aurora
 */
@Data
public class HistoryQuery {
    private String user1Id;
    private String user2Id;
    private int page;
    private int size;
    private String startDate;
    private String endDate;
}
