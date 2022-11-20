package com.hspedu.QQ.common;

/**
 * @author Zexi He.
 * @date 2022/11/17 9:29
 * @description:
 */
public interface MessageType {

    String MESSAGE_LOGIN_SUCCEED = "1"; //表示登录成功
    String MESSAGE_LOGIN_FAIL = "2"; //表示登录成功
    String MESSAGE_EXIT_SYSTEM = "3"; //表示客户端请求断开与服务端的连接
    String MESSAGE_CONFIRM_EXIT = "4";   //表示服务端接收到客户端离线请求并确认

    String MESSAGE_CURRENT_USERS_LIST_REQUEST = "5";    //该方法表示客户端请求显示在线用户列表
    String MESSAGE_CURRENT_USERS_LIST_RESPONSE = "6";   //该方法表示服务端返回显示在线用户列表的结果

    String MESSAGE_TO_SINGLE_USER = "7";   //表示私聊消息
    String MESSAGE_TO_SINGLE_USER_ACCEPTED = "8";   //表示私聊消息发送成功
    String MESSAGE_TO_SINGLE_USER_FAILED = "9"; //表示私聊消息发送失败

    String MESSAGE_TO_ALL_USERS_REQUEST = "10"; //表示群发消息的请求
    String MESSAGE_TO_ALL_USERS_RESPONSE = "11";    //表示群发消息的回复结果
    String MESSAGE_TO_ALL_USERS_ACCEPTED = "12";    //表示群发成功
    String MESSAGE_TO_ALL_USERS_REJECTED = "13";    //表示群发失败


    String MESSAGE_FILE_TRANSPORT_REQUEST = "14";   //表示传输文件的请求
    String MESSAGE_FILE_TRANSPORT_RESPONSE = "15";  //表示传输文件的回复
    String MESSAGE_FILE_TRANSPORT_ACCEPTED = "16";  //表示文件传输成功
    String MESSAGE_FILE_TRANSPORT_REJECTED = "17";  //表示文件传输失败
}
