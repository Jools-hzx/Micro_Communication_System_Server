package com.hspedu.QQ.common;

import java.io.Serializable;

/**
 * @author Zexi He.
 * @date 2022/11/17 9:23
 * @description:
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String password;

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
