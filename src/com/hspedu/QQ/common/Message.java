package com.hspedu.QQ.common;

import java.io.Serializable;

/**
 * @author Zexi He.
 * @date 2022/11/17 9:23
 * @description:
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sender;      //发送者
    private String receiver;    //接收者
    private String content;     //内容
    private String sendTime;    //发送时间
    private String type;   //在接口中定义
    private byte[] bytes;   //存储字节数组，用于文件传输

    public Message() {
    }

    public Message(String sender, String receiver, String content, String sendTime, String type) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.sendTime = sendTime;
        this.type = type;
    }

    public Message(String sender, String receiver, String content, String sendTime, String type, byte[] bytes) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.sendTime = sendTime;
        this.type = type;
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }
}
