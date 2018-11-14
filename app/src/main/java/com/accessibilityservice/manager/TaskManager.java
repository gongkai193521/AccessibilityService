package com.accessibilityservice.manager;

import android.util.Log;

import com.accessibilityservice.MainApplication;
import com.accessibilityservice.model.AppModel;
import com.accessibilityservice.service.MyAccessibilityService;
import com.accessibilityservice.util.AccessibilityManager;
import com.accessibilityservice.util.AppUtils;
import com.accessibilityservice.util.GsonUtils;
import com.accessibilityservice.util.Shell;

import java.util.Random;

/**
 * Created by gongkai on 2018/11/8.
 */

public class TaskManager {
    private volatile static TaskManager instance = null;

    private TaskManager() {
    }

    public static TaskManager getInstance() {
        if (instance == null) {
            synchronized (TaskManager.class) {
                if (instance == null) {
                    instance = new TaskManager();
                }
            }
        }
        return instance;
    }


    public void doTask(AppModel appModel) {
        if (!appModel.getAppPackage().equals(AppUtils.getTopActivity().getPkgName())) {
            AppUtils.startAppByPkg(MainApplication.getContext(), appModel.getAppPackage());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            doTask(appModel);
            return;
        }
        Log.i("----", " appmodel== " + GsonUtils.toJson(appModel));
        boolean isContains = false;
        for (AppModel.AppPageModel model : appModel.getPages()) {
            if (model.getClassName().contains(AppUtils.getTopCls())) {
                isContains = true;
                if ("0".equals(model.getType())) {
                    for (String viewId : model.getViews()) {
                        AccessibilityManager.clickByViewId(viewId);
                    }
                } else if ("1".equals(model.getType())) {
                    scrollDown(false, model);
                    for (String viewId : model.getViews()) {
                        AccessibilityManager.clickByViewIdForList(viewId);
                    }
                } else if ("2".equals(model.getType())) {
                    scrollDown(true, model);
                    MyAccessibilityService.back();
                }
            }
        }
        if (!isContains) {
            Log.i("----", " == 没有该页面");
            MyAccessibilityService.back();
        }
        doTask(appModel);
    }

    //上拉
    public static void scrollDown(boolean isDetails, AppModel.AppPageModel model) {
        Random random = new Random();
        int y = random.nextInt(50) + 100;
        long planTime;
        int sleepTime;
        if (isDetails) {//详情页面
            sleepTime = random.nextInt(2) + 4;
            planTime = model.getPlanTime();
        } else {//列表页面
            sleepTime = random.nextInt(2) + 2;
            planTime = (random.nextInt(6) + 3) * 1000;
        }
        long lasTime = System.currentTimeMillis();
        for (; ; ) {
            if (System.currentTimeMillis() - lasTime < planTime) {
                try {
                    Thread.sleep(sleepTime * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Shell.execute("input swipe " + y + " 500 " + y + " " + y);
                if (isDetails) {
                    for (String viewId : model.getViews()) {//点击阅读全文
                        Log.i("----", "viewId == " + viewId);
                        AccessibilityManager.clickByViewId(viewId);
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!model.getClassName().equals(AppUtils.getTopCls())) {
                    Log.i("----", " == 返回");
                    MyAccessibilityService.back();
                }
            } else {
                break;
            }
        }
    }

    //下拉刷新
    public static void scrollUp() {
        Random random = new Random();
        int y = random.nextInt(50) + 100;
        Shell.execute("input swipe " + y + " " + y + " " + y + 400);
    }
}
