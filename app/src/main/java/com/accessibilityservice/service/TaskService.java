package com.accessibilityservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityNodeInfo;

import com.accessibilityservice.MainApplication;
import com.accessibilityservice.manager.AccessPageManager;
import com.accessibilityservice.util.AppUtils;
import com.accessibilityservice.util.MyAccessibilityService;
import com.accessibilityservice.util.Shell;
import com.accessibilityservice.util.Threads;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by gongkai on 2018/11/5.
 */

public class TaskService extends Service {
    ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
    Threads mThreads = new Threads();
    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scheduledThreadPool.submit(mRunnable);
        return super.onStartCommand(intent, flags, startId);
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doTask();
        }
    };

    private void doTask() {
        if (!"cn.weli.story".equals(AccessPageManager.getInstance().getCurrentPkgName())) {
            AppUtils.startAppByPkg(MainApplication.getContext(), "cn.weli.story");
        }
        if ("cn.etouch.ecalendar.MainActivity".equals(AccessPageManager.getInstance().getCurentClsName())) {
            Shell.execute("input swipe 100 300 100 100");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Shell.execute("input swipe 100 400 100 200");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Shell.execute("input swipe 100 400 100 200");
            for (AccessibilityNodeInfo nodeInfo : MyAccessibilityService.getList()) {
//                if ("cn.weli.story:id/ll_root".equals(nodeInfo.getViewIdResourceName())) {
//                    MyAccessibilityService.click(nodeInfo);
//                }
                if ("cn.weli.story:id/tv_one_title".equals(nodeInfo.getViewIdResourceName())) {
                    MyAccessibilityService.click(nodeInfo);
                }
//                if ("cn.weli.story:id/layout".equals(nodeInfo.getViewIdResourceName())) {
//                    MyAccessibilityService.click(nodeInfo);
//                }
//            cn.weli.story:id/tv_height_more
            }
        }
        if ("cn.etouch.ecalendar.tools.life.LifeDetailsActivity"
                .equals(AccessPageManager.getInstance().getCurentClsName())) {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Shell.execute("input swipe 100 400 100 200");
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Shell.execute("input swipe 100 400 100 200");
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Shell.execute("input swipe 100 400 100 200");
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Shell.execute("input swipe 100 400 100 200");
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Shell.execute("input swipe 100 400 100 200");
            MyAccessibilityService.back();
        }
        doTask();
    }
}
