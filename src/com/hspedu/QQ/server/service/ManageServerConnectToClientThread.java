package com.hspedu.QQ.server.service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zexi He.
 * @date 2022/11/17 23:44
 * @description:    服务端管理和客户端通信的线程
 */
public class ManageServerConnectToClientThread {

    private static Map<String, ServerConnectClientThread> map = new HashMap<>();

    //添加线程到 map 集合中， key = username
    public static void addThread(String username, ServerConnectClientThread thread) {
        map.put(username, thread);
    }

    //根据用户Id删除线程
    public static void removeThread(String username) {
        map.remove(username);
    }

    //根据用户 ID 获取到相应的线程
    public static ServerConnectClientThread getThreadByUserId(String userId) {
        return map.get(userId);
    }
}
