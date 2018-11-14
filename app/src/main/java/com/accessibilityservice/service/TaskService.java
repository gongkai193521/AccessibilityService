package com.accessibilityservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.accessibilityservice.MainApplication;
import com.accessibilityservice.util.AccessibilityManager;
import com.accessibilityservice.util.AppUtils;
import com.accessibilityservice.util.Shell;

import java.util.Random;

/**
 * Created by gongkai on 2018/11/5.
 */

public class TaskService extends Service {
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
        MainApplication.getExecutorService().submit(mRunnable);
        return super.onStartCommand(intent, flags, startId);
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doTask();
        }
    };

    private void doTask() {
        if (!"cn.weli.story".equals(AppUtils.getTopActivity().getPkgName())) {
            doTask();
            return;
        }
        if ("cn.etouch.ecalendar.MainActivity".equals(AppUtils.getTopCls())) {
            scrollUp();
            scrollDown(false);
            //"cn.weli.story:id/text_ok"
            //cn.weli.story:id/iv_take
            //cn.weli.story:id/iv_close
            AccessibilityManager.clickByViewIdForList("cn.weli.story:id/tv_title");
        }
        if ("cn.etouch.ecalendar.tools.life.LifeDetailsActivity".equals(AppUtils.getTopCls())) {
            scrollDown(true);
            MyAccessibilityService.back();
        }
        doTask();
    }

    private void doTask1() {
        if ("com.ss.android.article.lite.activity.MainActivity".equals(AppUtils.getTopCls())) {
            scrollDown(false);
            AccessibilityManager.clickByViewIdForList("com.ss.android.article.lite:id/fs");
        }
        if ("com.ss.android.article.base.feature.detail2.view.NewDetailActivity".equals(AppUtils.getTopCls())) {
            scrollDown(true);
            MyAccessibilityService.back();
        }
        doTask();
    }

    public static void scrollDown(boolean isDetails) {
        Random random = new Random();
        int y = random.nextInt(50) + 100;
        int count;
        int sleepTime;
        if (isDetails) {
            count = random.nextInt(2) + 5;
            sleepTime = random.nextInt(2) + 5;
        } else {
            count = random.nextInt(2) + 1;
            sleepTime = random.nextInt(3) + 2;
        }
        for (int i = 0; i < count; i++) {
            try {
                Thread.sleep(sleepTime * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Shell.execute("input swipe " + y + " 500 " + y + " " + y);
            if (i == 1) {
                AccessibilityManager.clickByViewId("cn.weli.story:id/tv_height_more");
            }
        }
    }

    public static void scrollUp() {
        Random random = new Random();
        int y = random.nextInt(50) + 100;
        Shell.execute("input swipe " + y + " " + y + " " + y + 400);
    }
}
