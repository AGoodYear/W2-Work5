package com.ivmiku.w5r1.controller;

import com.alibaba.fastjson2.JSON;
import com.ivmiku.w5r1.entity.HistoryQuery;
import com.ivmiku.w5r1.entity.Message;
import com.ivmiku.w5r1.entity.Result;
import com.ivmiku.w5r1.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @PostMapping("/history")
    public Object getChatHistory(@RequestBody HistoryQuery input) throws ParseException {
        if (input.getPage() <= 0 || input.getSize() <=0) {
            return Result.error("请输入合法分页参数");
        }
        Result result = Result.ok();
        if (input.getStartDate() != null && input.getEndDate() != null) {
            result.setData(messageService.getChatHistoryByDate(input.getUser1Id(), input.getUser2Id(), input.getStartDate(), input.getEndDate()));
        } else {
            result.setData(messageService.getChatHistory(input.getUser1Id(), input.getUser2Id(), input.getPage(), input.getSize()));
        }
        return JSON.toJSON(result);
    }

    @GetMapping("/list")
    public Object getChatList(@RequestParam String user_id, @RequestParam int page, @RequestParam int size) {
        return Result.ok(messageService.getChatList(user_id, page, size));
    }
}
