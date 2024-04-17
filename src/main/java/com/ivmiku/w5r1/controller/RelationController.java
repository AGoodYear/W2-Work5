package com.ivmiku.w5r1.controller;

import com.ivmiku.w5r1.entity.FriendQuery;
import com.ivmiku.w5r1.entity.IgnoreUser;
import com.ivmiku.w5r1.entity.Result;
import com.ivmiku.w5r1.service.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/relation")
public class RelationController {
    @Autowired
    private RelationService relationService;

    @PostMapping("/ignore")
    public Object ignoreUser(@RequestBody IgnoreUser input) {
        relationService.IgnoreUser(input.getUserId(), input.getToIgnore());
        return Result.ok();
    }

    @PostMapping("/make")
    public Object makeFriend(@RequestBody FriendQuery input) {
        relationService.makeFriend(input.getUser1Id(), input.getUser2Id());
        return Result.ok();
    }

    @GetMapping("/friend")
    public Object getFriendList(@RequestParam String user_id, @RequestParam int page, @RequestParam int size) {
        return Result.ok(relationService.getFriendList(user_id, page, size));
    }
}
