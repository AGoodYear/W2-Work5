package com.ivmiku.w5r1.controller;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import com.ivmiku.w5r1.entity.Message;
import com.ivmiku.w5r1.service.MessageService;
import com.ivmiku.w5r1.utils.DateUtil;
import com.ivmiku.w5r1.utils.MessageUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static jakarta.websocket.CloseReason.CloseCodes.CLOSED_ABNORMALLY;

@Controller
@ServerEndpoint(value = "/chat/{satoken}")
public class WebSocketServer implements ApplicationContextAware {
    public static Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    public static Map<String, Integer> controlMap = new HashMap<>();

    private static ApplicationContext applicationContext;

    @Autowired
    private MessageService messageService;

    private RabbitTemplate rabbitTemplate;

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        Message msg = JSON.parseObject(message, Message.class);
        msg.setDate(DateUtil.getCurrentTime());
        if (MessageUtil.checkMessage(msg.getMessage())) {
            session.getBasicRemote().sendText("发送的信息含有敏感词，请进行调整");
            if (!controlMap.containsKey(msg.getFromId())){
                controlMap.put(msg.getFromId(), 0);
            }
            if (controlMap.get(msg.getFromId()) == 4){
                session.getBasicRemote().sendText("由于多次违反社区规则，您已被封禁1小时");
                session.close(new CloseReason(CLOSED_ABNORMALLY, "账号被封禁"));
                StpUtil.kickout(msg.getFromId());
                StpUtil.disable(msg.getFromId(), 3600);
                controlMap.put(msg.getFromId(), 0);
            }
            controlMap.put(msg.getFromId(), controlMap.get(msg.getFromId())+1);
        } else
            if (MessageUtil.ifIgnored(msg.getFromId(), msg.getToId())) {
            session.getBasicRemote().sendText("您已被对方屏蔽");
        } else {
            rabbitTemplate.convertAndSend("message_queue", JSON.toJSONString(msg));
        }
    }


    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig, @PathParam("satoken") String satoken) throws IOException {
        String userId = (String) StpUtil.getLoginIdByToken(satoken);
        if (userId == null) {
            session.getBasicRemote().sendText("Invalid Token");
            session.close();
        }
        this.messageService = WebSocketServer.applicationContext.getBean(MessageService.class);
        this.rabbitTemplate = WebSocketServer.applicationContext.getBean(RabbitTemplate.class);
        sessionMap.put(userId, session);
        List<Message> unreadList = messageService.getUnreadMsg(userId);
        for(Message msg : unreadList) {
            session.getBasicRemote().sendText(JSON.toJSONString(msg));
        }
    }

    @OnClose
    public void onClose(CloseReason closeReason, Session session){
        sessionMap.remove(session.getId());
    }

    @OnError
    public void onError(Throwable throwable) throws IOException {
        throwable.printStackTrace();
    }

    public void sendToUser(Message msg) throws IOException, ParseException {
        if (sessionMap.containsKey(msg.getToId())){
            sessionMap.get(msg.getToId()).getBasicRemote().sendText(JSON.toJSONString(msg));
        }
        else {
            messageService.insertUnreadMsg(msg.getToId(), msg);
        }
        msg.setChatId(messageService.getChatId(msg.getFromId(), msg.getToId()));
        messageService.insertToMysql(msg);
        messageService.insertToRedis(msg);
    }

    public void sendToPublic(Message msg) throws IOException, ParseException {
        for (Session session : sessionMap.values()) {
            session.getBasicRemote().sendText(JSON.toJSONString(msg));
        }
        msg.setChatId(messageService.getChatId(msg.getFromId(), msg.getToId()));
        messageService.insertToMysql(msg);
        messageService.insertToRedis(msg);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        WebSocketServer.applicationContext = applicationContext;
    }

    @RabbitHandler
    @RabbitListener(queuesToDeclare = @Queue("message_queue"))
    public void sendMsg(String message) throws IOException, ParseException {
        Message msg = JSON.parseObject(message, Message.class);
        if (!Objects.equals(msg.getToId(), "public")) {
            sendToUser(msg);
        } else {
            sendToPublic(msg);
        }
    }
}
