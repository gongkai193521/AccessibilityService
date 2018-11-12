package com.accessibilityservice.manager;


import com.accessibilityservice.model.Client_User;
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
        return getLogin()!=null;
    }

    private Client_User mClient_User;

    public void saveLogin(Client_User mClient_User) {
        this.mClient_User=mClient_User;
        SharedPreferencesUtils.putValue(LOGINNAME, mClient_User);
    }

    public Client_User getLogin() {
        if (mClient_User==null) {
            mClient_User=SharedPreferencesUtils.getValue(LOGINNAME, null, Client_User.class);
        }
        return mClient_User;
    }

    public boolean reset() {
        mClient_User=null;
        return SharedPreferencesUtils.remove(LOGINNAME);
    }

}
