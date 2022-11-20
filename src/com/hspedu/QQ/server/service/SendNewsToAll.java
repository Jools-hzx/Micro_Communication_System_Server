package com.hspedu.QQ.server.service;

import com.hspedu.QQ.ServerUtils;
import com.hspedu.QQ.common.Message;
import com.hspedu.QQ.common.MessageType;
import com.hspedu.QQ.server.Handlers.MessageFactory;
import com.hspedu.QQ.server.UserDataBase.UsersDataBase;

import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Zexi He.
 * @date 2022/11/20 10:27
 * @description:    为了可以推送多次，我们可以用 while 循环
 */
public class SendNewsToAll implements Runnable {

    private Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        while (true) {
            System.out.println("请输入服务器需要推送的新闻[输入exit表示退出新闻推送服务]:");
            String str = scanner.next();
            if ("exit".equalsIgnoreCase(str)) {
                System.out.println("新闻推送服务结束");
                break;
            }
            String content = ServerUtils.readInputStringByLimited(str, 1000);
            Message serverToClientMessage = MessageFactory.getMessageItem(
                    "",
                    MessageType.MESSAGE_TO_ALL_USERS_RESPONSE,
                    content
            );
            System.out.println("服务器对所有人说:" + content);
            //遍历当前所有的通信线程，并发送一个 Message
            Set<String> usersSet = UsersDataBase.getActiveUsersSet();
            Iterator<String> iterator = usersSet.iterator();

            while (iterator.hasNext()) {
                String onlineUser = iterator.next();
                ServerConnectClientThread thread = ManageServerConnectToClientThread.getThreadByUserId(onlineUser);
                thread.sendMessageToClient(serverToClientMessage);
            }
        }
    }
}
