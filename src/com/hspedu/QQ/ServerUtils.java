package com.hspedu.QQ;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Zexi He.
 * @date 2022/11/19 11:17
 * @description:
 */
public class ServerUtils {
    //该方法用于返回当前的时间
    public static String getCurrentTime() {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("HH:mm");
        return pattern.format(time);
    }

    //该方法用于接收限定字符串长度的输入
    public static String readInputStringByLimited(String input, int limitLength) {
        if (input.length() > limitLength) {
            return "";
        } else {
            return input;
        }
    }
}
