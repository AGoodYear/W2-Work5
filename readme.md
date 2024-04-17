# 技术栈

SpringBoot3，Mysql，Mybatis-Plus，RabbitMQ，SaToken

# 项目简介

基于SpringBoot的聊天室项目，使用WebSocket。

接口文档：https://apifox.com/apidoc/shared-a85bba75-1d17-46e0-88c5-3e1c78a9f704

项目完成度：70%（许多CRUD不太完善）

项目结构：

```Plain
               ├─config      （存放各种配置）
               ├─controller  （接口层）
               ├─entity      （实体层）
               ├─mapper      （Mapper层）
               ├─service     （服务层）
               └─utils       （工具，日期、加解密相关等）
```

# 项目实现

## 相关实体类构成

![img](https://nin7cu6a4v7.feishu.cn/space/api/box/stream/download/asynccode/?code=YmQ5NTg1YWViMjJmNjU3OGY5MDAwN2ZjMzQ1Y2E1ZGJfb0thVEZkOHZKNkZDUmNqTTFqa29XOGdhNGNVRklnV1hfVG9rZW46REJGd2Jub0o0b2piRzh4UkNLRGNzaXRvbmdmXzE3MTMzNjUwMTc6MTcxMzM2ODYxN19WNA)

## 主要逻辑

WebSocket连接->发送信息至消息队列->处理消息，发送给对应的接收方

# 项目亮点

## 使用RabbitMQ消息队列

工程使用消息队列，将聊天的发送和接收分开，缩短用户发送信息时的用时，改善用户体验，并在高并发时保证系统的可用性。

## 使用Redis+Mysql存储聊天记录

聊天记录会被先缓存到Redis，随后在固定的时间写入数据库，用户获取聊天记录时先从redis获取聊天记录，若redis中没有聊天记录，则再从数据库中查询，减轻了数据库的压力。

聊天记录是Message实体类，JSON序列化后存入Redis的ZSet数据结构中，使用会话ID作为键，聊天记录日期时间戳作为分数，以备带有日期条件的聊天记录查询使用。

## 使用Redis实现离线消息

用户发送信息时，若接收方不在线，则将消息存入Redis中，待用户上线时再发送给用户，更贴近现实中的IM逻辑。

## 实现Base64编码的图片发送

为此将发送大小限制提高以适应大体积图片的传输，图片不会作为聊天记录被存储。

# 待改进的点

由于不了解前端的相关知识，因此可能部分功能在设计上不太合理，不利于前后端的整合。

当使用日期区间查找聊天记录时，没有实现分页功能。

Redis在存放聊天记录方面的做法仍不太合理，使用Redis的本意是减少数据库的读写次数和压力，但是每天写到数据库后再次获取聊天记录仍然需要查询数据库。目前的想法是设置一个Redis存储的上限，当Redis内存储的聊天记录达到这个数后每增加一条新的聊天记录就向数据库写一条旧的，使得Redis中总有缓存。