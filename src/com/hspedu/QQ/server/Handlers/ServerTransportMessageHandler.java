package com.hspedu.QQ.server.Handlers;

import com.hspedu.QQ.ServerUtils;
import com.hspedu.QQ.common.Message;
import com.hspedu.QQ.common.MessageType;
import com.hspedu.QQ.common.User;
import com.hspedu.QQ.server.UserDataBase.UsersDataBase;
import com.hspedu.QQ.server.service.ManageServerConnectToClientThread;
import com.hspedu.QQ.server.service.ServerConnectClientThread;

import java.util.Set;

/**
 * @author Zexi He.
 * @date 2022/11/19 10:36
 * @description:
 */
public class ServerTransportMessageHandler {


    //该方法用于判断接收到的消息类型
    public void handleMessage(Message message) {
        String type = message.getType();
        if (MessageType.MESSAGE_EXIT_SYSTEM.equals(type)) { //客户端断开和服务器的连接
            clientLoginOut(message);
        } else if (MessageType.MESSAGE_CURRENT_USERS_LIST_REQUEST.equals(type)) {
            //客户端请求和在线用户列表
            getCurrentLiveUsersList(message);
        } else if (MessageType.MESSAGE_TO_SINGLE_USER.equals(type)) {
            //客户端向单个用户发送消息
            sendMessageToSingleUser(message);
        } else if (MessageType.MESSAGE_TO_ALL_USERS_REQUEST.equals(type)) {
            //处理群发消息的请求
            sendMessageToAllUsers(message);
        } else if (MessageType.MESSAGE_FILE_TRANSPORT_REQUEST.equals(type)) {
            //处理传输文件的请求
            sendFileToUser(message);
        }
    }

    //该方法用于完成文件传输的操作
    private void sendFileToUser(Message clientToServerMessage) {
        byte[] bytes = clientToServerMessage.getBytes();
        String destPath = clientToServerMessage.getContent().split(",")[1];
        String receiver = clientToServerMessage.getReceiver();

        //回复消息给接收方方法
        Message ServerToClientMessage = null;
        //回复消息给sender
        Message serverToSenderMessage;
        String responseToSender;

        //判断当前用户是否为合法用户
        if (UsersDataBase.isUserExist(receiver)) {
            //构建文件发送回复
            ServerToClientMessage = MessageFactory.getMessageItem(
                    clientToServerMessage.getSender(),
                    receiver,
                    MessageType.MESSAGE_FILE_TRANSPORT_RESPONSE,
                    destPath,
                    bytes
            );
            //判断当前用户是否为活跃用户
            if (UsersDataBase.isUserActive(receiver)) {
                //获取对应的 Socket
                //发送文件传输信息
                ServerConnectClientThread thread = ManageServerConnectToClientThread.getThreadByUserId(receiver);
                thread.sendMessageToClient(ServerToClientMessage);
            } else {
                //存放到离线消息的集合中
                System.out.println("[" + receiver + "] 不在线,将其存储为离线留言");
                UsersDataBase.addOfflineMessageToMap(receiver, ServerToClientMessage);
            }

            //给发送者回复发送成功的结果
            responseToSender = "文件发送成功";
            serverToSenderMessage = MessageFactory.getMessageItem(
                    clientToServerMessage.getSender(),
                    MessageType.MESSAGE_FILE_TRANSPORT_ACCEPTED,
                    responseToSender
            );
        } else {
            //返回发送失败的消息
            responseToSender = "文件发送失败，该用户不存在或者不在线";
            serverToSenderMessage = MessageFactory.getMessageItem(
                    clientToServerMessage.getSender(),
                    MessageType.MESSAGE_FILE_TRANSPORT_REJECTED,
                    responseToSender
            );
        }
        //返回结果给 sender 客户端
        ServerConnectClientThread senderThread = ManageServerConnectToClientThread.getThreadByUserId(serverToSenderMessage.getReceiver());
        senderThread.sendMessageToClient(serverToSenderMessage);
        //服务端打印提示
        System.out.println(
                clientToServerMessage.getSendTime() +
                        "  [" +
                        clientToServerMessage.getSender() +
                        "] 给 [" +
                        clientToServerMessage.getReceiver()
                        + "] " + responseToSender);
    }

    //该方法用于完成群发操作
    private void sendMessageToAllUsers(Message message) {
        String sender = message.getSender();
        String content = message.getContent();
        //获取当前所有的在线用户
        Set<String> activeUsersSet = UsersDataBase.getActiveUsersSet();
        String responseToSender = "";
        //提示发送者群发消息结果
        Message serverToSenderMessage;
        ServerConnectClientThread senderThread = ManageServerConnectToClientThread.getThreadByUserId(sender);

        //当前活跃用户为空的时候不能进行群发消息
        if (!activeUsersSet.isEmpty()) {
            Message serverToClientMessage;
            for (String username : activeUsersSet) {
                //获取其与服务端建立通信的线程
                //通过该线程持有的 Socket 去发送消息
                if (!username.equals(sender)) {
                    //不用发给发送者本人
                    ServerConnectClientThread thread = ManageServerConnectToClientThread.getThreadByUserId(username);
                    //构建群发消息
                    serverToClientMessage = MessageFactory.getMessageItem(
                            sender,
                            username,
                            MessageType.MESSAGE_TO_ALL_USERS_RESPONSE,
                            content
                    );
                    thread.sendMessageToClient(serverToClientMessage);
                }
            }

            responseToSender = "群发消息成功";
            serverToSenderMessage = MessageFactory.getMessageItem(
                    sender,
                    MessageType.MESSAGE_TO_ALL_USERS_ACCEPTED,
                    responseToSender
            );
            senderThread.sendMessageToClient(serverToSenderMessage);
            //服务端提示信息
        } else {
            //提示发送者群发消息失败
            responseToSender = "群发消息失败，当前无用户在线";
            serverToSenderMessage = MessageFactory.getMessageItem(
                    sender,
                    MessageType.MESSAGE_TO_ALL_USERS_REJECTED,
                    responseToSender
            );
            senderThread.sendMessageToClient(serverToSenderMessage);
        }
        //服务端提示信息
        System.out.println("\n" + sender + " 进行群发消息操作" + responseToSender);
    }

    //该方法用于完成私聊操作
    private void sendMessageToSingleUser(Message message) {
        String sender = message.getSender();
        String receiver = message.getReceiver();

        //初始化返回给客户端的消息
        Message serverToSenderMessage = null;
        //获取发送方与服务端通信的线程
        ServerConnectClientThread senderConnectedThread = ManageServerConnectToClientThread.getThreadByUserId(sender);
        String content = "";

        //判断接收方是否为合法用户
        if (UsersDataBase.isUserExist(receiver)) {
            //检查是否为在线用户
            if (UsersDataBase.isUserActive(receiver)) {
                //向接收方发送消息
                ServerConnectClientThread receiverConnectedThread = ManageServerConnectToClientThread.getThreadByUserId(receiver);
                receiverConnectedThread.sendMessageToClient(message);
            } else {
                //如果不是在线用户，将其存放到离线消息集合中
                System.out.println("[" + receiver + "] 不在线,将其存储为离线留言");
                UsersDataBase.addOfflineMessageToMap(receiver, message);
            }
            //给发送方回复发送成功！
            content = "发送成功";
            serverToSenderMessage = MessageFactory.getMessageItem(
                    sender,
                    MessageType.MESSAGE_TO_SINGLE_USER_ACCEPTED,
                    content
            );
            senderConnectedThread.sendMessageToClient(serverToSenderMessage);
        } else {
            //返回给发送方发送消息失败的信息
            content = "发送失败,用户[" + receiver + "]不是合法用户";
            serverToSenderMessage = MessageFactory.getMessageItem(
                    sender,
                    MessageType.MESSAGE_TO_SINGLE_USER_FAILED,
                    content
            );
            senderConnectedThread.sendMessageToClient(serverToSenderMessage);
        }
        System.out.println("\n服务端返回 [" + sender + "] 想对 [" + receiver + "] 发起的私聊结果:" + content);
    }

    //该方法处理客户端断开与服务器的连接
    private void clientLoginOut(Message message) {
        //从集合中找出对应的线程及其持有的Socket
        String sender = message.getSender();
        ServerConnectClientThread thread = ManageServerConnectToClientThread.getThreadByUserId(sender);
        //构建返回给客户端的消息对象
        Message serverToClientMessage = MessageFactory.getMessageItem(sender, MessageType.MESSAGE_CONFIRM_EXIT, "");
        try {
            thread.sendMessageToClient(serverToClientMessage);
            //将服务端与此客户端连接的线程关闭
            thread.shutDownThread();
        } finally {
            System.out.println("时间:" + message.getSendTime() + " --> " + message.getContent());
            //将此线程从管理的集合中移除
            ManageServerConnectToClientThread.removeThread(sender);
            //从在线用户集合中移除
            UsersDataBase.removeActiveUser(sender);
            //将其加入到离线用户集合中
            UsersDataBase.addOfflineUserToMap(sender);
        }
    }


    private void getCurrentLiveUsersList(Message message) {
        String sender = message.getSender();
        System.out.println(message.getContent());
        //从集合中获取与该用户通信的 Socket
        ServerConnectClientThread thread = ManageServerConnectToClientThread.getThreadByUserId(sender);

        //获取在线用户列表
        String content = UsersDataBase.getCurrentActiveUsersToString();
        //构建返回给客户端的消息
        Message serverToClientMessage = MessageFactory.getMessageItem(
                sender,
                MessageType.MESSAGE_CURRENT_USERS_LIST_RESPONSE,
                content);
        //通过该线程持有的socket回复给客户端
        thread.sendMessageToClient(serverToClientMessage);
    }
}
