package com.hspedu.QQ.server.UserDataBase;

import com.hspedu.QQ.common.Message;
import com.hspedu.QQ.common.MessageType;
import com.hspedu.QQ.common.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zexi He.
 * @date 2022/11/19 13:01
 * @description: 该类存放所有的认证用户
 */
public class UsersDataBase {

    //该集合用于存储合法的用户
    private static Map<String, User> validUsersMap = new HashMap<>();
    //该集合用于存储在线用户
    private static Set<String> activeUsersSet = new HashSet<>();
    //该集合用于存放离线消息
    private static Map<String, ArrayList<Message>> offlineMessageMap = new ConcurrentHashMap<>();

    static {
        //构建合法的用户列表
        validUsersMap.put("100", new User("100", "123456"));
        validUsersMap.put("200", new User("200", "123456"));
        validUsersMap.put("300", new User("300", "123456"));
        validUsersMap.put("400", new User("400", "123456"));
        validUsersMap.put("500", new User("500", "123456"));

        //构建离线用户集合
        offlineMessageMap.put("100", new ArrayList<>());
        offlineMessageMap.put("200", new ArrayList<>());
        offlineMessageMap.put("300", new ArrayList<>());
        offlineMessageMap.put("400", new ArrayList<>());
        offlineMessageMap.put("500", new ArrayList<>());
    }

    public static Map<String, ArrayList<Message>> getOfflineMessageMap() {
        return offlineMessageMap;
    }

    //该方法用于添加离线消息到离线集合中
    public static void addOfflineMessageToMap(String username, Message message) {
        offlineMessageMap.get(username).add(message);
    }

    //该方法用于将离线用户加入到离线集合中
    public static void addOfflineUserToMap(String username) {
        offlineMessageMap.put(username, new ArrayList<>());
    }

    //该方法将从发送完离线消息的用户从离线消息集合中剔除
    public static void removeUserFromOfflineMessageSet(String username) {
        offlineMessageMap.remove(username);
    }

    public static Set<String> getActiveUsersSet() {
        return activeUsersSet;
    }

    //该方法用于检查请求登录的用户是否为合法用户
    //如果为合法用户，登录后将其添加到在线用户的列表
    //并且从离线用户列表中去除
    public static boolean isValidUser(String username, String pwd) {
        if (validUsersMap.containsKey(username) && validUsersMap.get(username).getPassword().equals(pwd)) {
            activeUsersSet.add(username);
            return true;
        }
        return false;
    }

    //该方法用于检查用户是否存在
    public static boolean isUserExist(String username) {
        return validUsersMap.containsKey(username);
    }

    //该方法用于检查用户是否在线
    public static boolean isUserActive(String username) {
        return activeUsersSet.contains(username);
    }

    //该方法用于返回当前在线的用户列表
    public static String getCurrentActiveUsersToString() {
        StringBuilder activeUsersStringBuilder = new StringBuilder();
        for (String s : activeUsersSet) {
            activeUsersStringBuilder.append(s).append(",");
        }
        return activeUsersStringBuilder.toString();
    }

    //该方法用于删除已经离线的用户
    public static void removeActiveUser(String username) {
        activeUsersSet.remove(username);
    }
}
