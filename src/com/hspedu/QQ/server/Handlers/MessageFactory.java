package com.hspedu.QQ.server.Handlers;

import com.hspedu.QQ.ServerUtils;
import com.hspedu.QQ.common.Message;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Zexi He.
 * @date 2022/11/19 13:30
 * @description: 该方法用于创建各种返回给客户端的消息
 */
public class MessageFactory {

    public static Message getMessageItem(String sender, String receiver, String messageType, String content) {
        return new Message(
                sender,
                receiver,
                content,
                ServerUtils.getCurrentTime(),
                messageType);
    }

    public static Message getMessageItem(String sender, String receiver, String messageType, String content, byte[] bytes) {
        return new Message(
                sender,
                receiver,
                content,
                ServerUtils.getCurrentTime(),
                messageType,
                bytes);
    }


    public static Message getMessageItem(String receiver, String messageType, String content) {
        try {
            return new Message(
                    InetAddress.getByName("127.0.0.1").toString(),
                    receiver,
                    content,
                    ServerUtils.getCurrentTime(),
                    messageType);
        } catch (UnknownHostException e) {
            return new Message(
                    "127.0.0.1",
                    receiver,
                    content,
                    ServerUtils.getCurrentTime(),
                    messageType);
        }
    }
}
