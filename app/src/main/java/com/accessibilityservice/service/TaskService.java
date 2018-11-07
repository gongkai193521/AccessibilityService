package com.accessibilityservice.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Rect;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityNodeInfo;

import com.accessibilityservice.MainApplication;
import com.accessibilityservice.util.AppUtils;
import com.accessibilityservice.util.MyAccessibilityService;
import com.accessibilityservice.util.Shell;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.dmoral.toasty.Toasty;

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
        MainApplication.getExecutorService().submit(mRunnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doTask();
        }
    };
    private List<String> mList = new ArrayList<>();

    private void doTask() {
        if ("cn.etouch.ecalendar.MainActivity".equals(AppUtils.getTopActivity().getClsName())
                || "com.ss.android.article.lite.activity.MainActivity".equals(AppUtils.getTopActivity().getClsName())) {
            scrollDown(false);
            for (AccessibilityNodeInfo nodeInfo : MyAccessibilityService.getList()) {
                if ("cn.weli.story:id/tv_title".equals(nodeInfo.getViewIdResourceName())
                        || "com.ss.android.article.lite:id/fs".equals(nodeInfo.getViewIdResourceName())) {
                    Rect rect2 = new Rect();
                    nodeInfo.getBoundsInScreen(rect2);
                    if (nodeInfo.getText() != null) {
                        String text = nodeInfo.getText().toString();
                        if (!mList.toArray().toString().contains(text)) {
                            Toasty.info(this, "点击--" + text);
                            Shell.execute("input tap " + rect2.left + " " + rect2.top);
                            mList.add(text);
                        }
                    }
                }
            }
        }
        if ("cn.etouch.ecalendar.tools.life.LifeDetailsActivity".equals(AppUtils.getTopActivity().getClsName())
                || "com.ss.android.article.base.feature.detail2.view.NewDetailActivity".equals(AppUtils.getTopActivity().getClsName())) {
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
            count = random.nextInt(2) + 6;
            sleepTime = random.nextInt(2) + 6;
        } else {
            count = random.nextInt(1) + 2;
            sleepTime = random.nextInt(3) + 3;
        }
        for (int i = 0; i < count; i++) {
            Shell.execute("input swipe " + y + " 500 " + y + " " + y);
            try {
                Thread.sleep(sleepTime * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void scrollUp() {
        Random random = new Random();
        int y = random.nextInt(50) + 100;
        int count = random.nextInt(2) + 2;
        int sleepTime = random.nextInt(3) + 1;
        for (int i = 0; i < count; i++) {
            Shell.execute("input swipe " + y + " 400 " + y + " " + y);
            try {
                Thread.sleep(sleepTime * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
