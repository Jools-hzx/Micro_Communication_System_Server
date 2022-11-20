package com.hspedu.QQ.server.service;

import com.hspedu.QQ.common.Message;
import com.hspedu.QQ.server.Handlers.ServerTransportMessageHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author Zexi He.
 * @date 2022/11/17 23:38
 * @description: 该类是和某个客户端保持一个通信
 */
public class ServerConnectClientThread implements Runnable {

    private Socket socket;
    //客户端发送过来的用户 Id
    private String userId;
    //持有消息处理类的一个对象
    private ServerTransportMessageHandler messageHandler;

    public Socket getSocket() {
        return socket;
    }

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
        this.messageHandler = new ServerTransportMessageHandler();
    }

    //该方法用于该线程同 Socket 向 Client 端发送消息
    public void sendMessageToClient(Message serverToClientMessage) {
        //返回确认退出的消息给客户端
        ObjectOutputStream OOS = null;
        try {
            OOS = new ObjectOutputStream(socket.getOutputStream());
            OOS.writeObject(serverToClientMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (!socket.isClosed()) {
            //通过 Socket 持续监听客户端发送过来的消息
            System.out.println("服务端和客户端" + userId + "保持通信，读取数据....");
            try {
                ObjectInputStream OIS = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) OIS.readObject();
                //根据传入的消息对象调用消息处理类进行后续操作
                messageHandler.handleMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //该方法用于关闭该线程
    public void shutDownThread() {
        try {
            //关闭对应的 Socket
            getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
