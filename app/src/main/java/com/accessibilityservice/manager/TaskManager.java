package com.accessibilityservice.manager;

import android.util.Log;

import com.accessibilityservice.MainApplication;
import com.accessibilityservice.model.AppModel;
import com.accessibilityservice.service.MyAccessibilityService;
import com.accessibilityservice.util.AccessibilityManager;
import com.accessibilityservice.util.AppUtils;
import com.accessibilityservice.util.GsonUtils;
import com.accessibilityservice.util.Shell;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by gongkai on 2018/11/8.
 */

public class TaskManager {
    private volatile static TaskManager instance = null;
    public static boolean isRun = true;
    private static long runStartTime;
    private static AppModel appModel;

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

    private List<String> mList = new ArrayList<>();

    //停止执行
    private boolean isStop() {
        if (!isRun || System.currentTimeMillis() - runStartTime >= appModel.getPlanTime()) {
            Log.i("----", " 停止执行== " + appModel.getAppName() + "脚本");
            AccessibilityManager.sendMsg("已停止执行脚本");
            Shell.exec("am force-stop " + appModel.getAppPackage(), true);
            Shell.exec("am start -n " + MainApplication.getContext().getPackageName() + "/com.accessibilityservice.activity.LoginActivity");
            return true;
        }
        return false;
    }

    public void task(AppModel appModel) {
        if (!AppUtils.isApplicationAvilible(appModel.getAppPackage())) {
            AccessibilityManager.sendMsg("请先安装" + appModel.getAppName());
            return;
        }
        Log.i("----", " 开始执行== " + appModel.getAppName() + "脚本");
        Log.i("----", " appmodel== " + GsonUtils.toJson(appModel));
        runStartTime = System.currentTimeMillis();
        TaskManager.isRun = true;
        doTask(appModel);
    }

    public void doTask(AppModel appModel) {
        this.appModel = appModel;
        if (isStop()) return;
        if (!appModel.getAppPackage().equals(AppUtils.getTopActivity().getPkgName())) {
            AppUtils.startAppByPkg(MainApplication.getContext(), appModel.getAppPackage());
            Log.i("----", "打开== " + appModel.getAppName());
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mList.clear();
        for (AppModel.AppPageModel model : appModel.getPages()) {
            mList.add(model.getClassName());
        }
        for (AppModel.AppPageModel model : appModel.getPages()) {
            if (model.getClassName().equals(AppUtils.getTopCls())) {
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
            } else if (!mList.contains(AppUtils.getTopCls())) {
                Log.i("----", " == 略过此页面");
                MyAccessibilityService.back();
            }
        }
        doTask(appModel);
    }

    //上拉
    private void scrollDown(boolean isDetails, AppModel.AppPageModel model) {
        Random random = new Random();
        int y = random.nextInt(50) + 100;
        long planTime;
        int sleepTime;
        if (isDetails) {//详情页面
            sleepTime = random.nextInt(2) + 3;
            planTime = model.getPlanTime();
        } else {//列表页面
            sleepTime = random.nextInt(2) + 2;
            planTime = (random.nextInt(6) + 3) * 1000;
        }
        long lasTime = System.currentTimeMillis();
        for (; ; ) {
            if (isStop()) return;
            if (System.currentTimeMillis() - lasTime < planTime) {
                try {
                    Thread.sleep(sleepTime * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Shell.execute("input swipe " + y + " 500 " + y + " " + y);
                if (isDetails) {
                    for (String viewId : model.getViews()) {//点击阅读全文
                        AccessibilityManager.clickByViewId(viewId);
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!model.getClassName().equals(AppUtils.getTopCls())) {
                    Log.i("----", "无此页面--返回");
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
