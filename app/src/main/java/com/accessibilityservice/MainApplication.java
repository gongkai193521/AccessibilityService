package com.accessibilityservice;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.accessibilityservice.manager.UiManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import es.dmoral.toasty.Toasty;

/**
 * Created by gongkai on 2018/11/5.
 */

public class MainApplication extends Application {
    private static ScheduledExecutorService scheduledThreadPool;
    private static Context mContext;
    private static UiManager uiManager;
    private static PackageManager pm;
    public static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String text = (String) msg.obj;
            Toasty.info(MainApplication.getContext(), text).show();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        scheduledThreadPool = Executors.newScheduledThreadPool(5);
        mContext = this.getApplicationContext();
        uiManager = new UiManager(this);
    }

    public static ScheduledExecutorService getExecutorService() {
        return scheduledThreadPool;
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
