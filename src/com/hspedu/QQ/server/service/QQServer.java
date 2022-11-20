package com.hspedu.QQ.server.service;

import com.hspedu.QQ.common.Message;
import com.hspedu.QQ.common.MessageType;
import com.hspedu.QQ.common.User;
import com.hspedu.QQ.server.UserDataBase.UsersDataBase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * @author Zexi He.
 * @date 2022/11/17 23:24
 * @description: 这是服务端，监听 9999 端口，等待客户端的连接
 */
public class QQServer {

    private ServerSocket serverSocket = null;

    //该方法用于发送所有的离线消息
    private void sendOfflineMessages() {
        Map<String, ArrayList<Message>> offlineMessageMap = UsersDataBase.getOfflineMessageMap();
        Set<String> receiverNames = offlineMessageMap.keySet();
        //如果接收方从离线状态变成了在线状态
        //并且存放离线消息的集合中不为空
        for (String receiverName : receiverNames) {
            if (UsersDataBase.isUserExist(receiverName) &&
                    UsersDataBase.isUserActive(receiverName) &&
                    !(UsersDataBase.getOfflineMessageMap().get(receiverName)).isEmpty()) {
                //发送离线消息
                ArrayList<Message> offlineMessages = offlineMessageMap.get(receiverName);
                ServerConnectClientThread thread = ManageServerConnectToClientThread.getThreadByUserId(receiverName);
                for (Message offlineMessage : offlineMessages) {
                    System.out.println(offlineMessage.getSender() + "给"
                            + offlineMessage.getReceiver() + "发送的离线消息:" +
                            offlineMessage.getType());
                    thread.sendMessageToClient(offlineMessage);
                }
                //将接受完离线消息的接收方从离线消息集合中去除
                UsersDataBase.removeUserFromOfflineMessageSet(receiverName);
            }
        }
    }

    public QQServer() {
        try {
            //端口可以写到一个配置文件之中
            System.out.println("服务端在 9999 端口连接");
            this.serverSocket = new ServerSocket(9999);

            //启动推送服务的线程
            new Thread(new SendNewsToAll()).start();
            //当和某个客户端建立连接后，会继续监听；
            while (true) {
                //如果没有客户端连接，则会阻塞在这里
                Socket socket = serverSocket.accept();

                //通过 Socket 从客户端读取数据
                //得到 Socket 关联的输入流
                ObjectInputStream OIS = new ObjectInputStream(socket.getInputStream());
                //得到 Socket 关联的输出流
                ObjectOutputStream OOS = new ObjectOutputStream(socket.getOutputStream());

                //创建一个 Message 回复客户端
                Message message = new Message();
                User user = (User) OIS.readObject();
                //验证用户
                boolean valid = UsersDataBase.isValidUser(user.getUserId(), user.getPassword());
                if (valid) {
                    //如果验证成功;证明为合法用户
                    message.setType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    OOS.writeObject(message);
                    //创建一个线程，和这个客户端保持通信
                    ServerConnectClientThread thread = new ServerConnectClientThread(socket, user.getUserId());
                    new Thread(thread).start();
                    //将线程添加至管理集合
                    ManageServerConnectToClientThread.addThread(user.getUserId(), thread);
                    //发送存储在离线消息集合中的消息
                    sendOfflineMessages();
                } else {
                    //验证失败
                    System.out.println("\n用户 id = " + user.getUserId() + "  pwd=" + user.getPassword() + " 验证失败");
                    message.setType(MessageType.MESSAGE_LOGIN_FAIL);
                    OOS.writeObject(message);
                    socket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            //如果服务端退出了 while 循环，说明这个服务端不再监听了；需要关闭资源
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
