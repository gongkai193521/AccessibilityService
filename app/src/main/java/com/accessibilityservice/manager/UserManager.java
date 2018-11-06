package com.accessibilityservice.manager;

import android.text.TextUtils;

import com.accessibilityservice.util.SharedPreferencesUtils;

/**
 * Created by gackor on 15/10/15.
 */
public class UserManager {
    public static final String LOGINNAME = "LoginName";//帐号
    public static final String PASSWORD = "password";//密码
    private static UserManager instance = null;

    public static UserManager getInstance() {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null) {
                    instance = new UserManager();
                }
            }
        }
        return instance;
    }

    /**
     * 是否登陆
     *
     * @return
     */
    public boolean isLogin() {
        return !TextUtils.isEmpty(getLoginname());
    }

    public void saveLoginName(String loginName) {
        SharedPreferencesUtils.putValue(LOGINNAME, loginName);
    }

    public String getLoginname() {
        return SharedPreferencesUtils.getValue(LOGINNAME, null, String.class);
    }

    public boolean reset() {
        return SharedPreferencesUtils.remove(LOGINNAME);
    }

}
