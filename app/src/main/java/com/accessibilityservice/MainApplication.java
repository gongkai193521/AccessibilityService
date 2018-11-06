package com.accessibilityservice;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

import com.accessibilityservice.manager.UiManager;

/**
 * Created by gongkai on 2018/11/5.
 */

public class MainApplication extends Application {
    private static Context mContext;
    private static UiManager uiManager;
    private static PackageManager pm;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
        uiManager = new UiManager(this);
    }

    public static Context getContext() {
        return mContext;
    }

    public static UiManager getUiManager() {
        return uiManager;
    }

    public static synchronized PackageManager getPkgManager() {
        if (pm == null) {
            pm = mContext.getPackageManager();
        }
        return pm;
    }
}
