package com.accessibilityservice.manager;

import android.text.TextUtils;
import android.util.Log;

import com.accessibilityservice.MainApplication;
import com.accessibilityservice.model.ActivityInfo;
import com.accessibilityservice.model.AppModel;
import com.accessibilityservice.service.MyAccessibilityService;
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
    private static boolean isStop = false;//停止脚本
    private long runStartTime;
    private AppModel appModel;
    private List<String> clsList;

    private TaskManager() {
        if (clsList == null) {
            clsList = new ArrayList<>();
        }
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

    //是否停止执行
    private boolean isStop() {
        if (isStop || System.currentTimeMillis() - runStartTime >= appModel.getPlanTime()) {
            Shell.exec("am force-stop " + appModel.getAppPackage(), true);
            Shell.exec("am start -n " + MainApplication.getContext().getPackageName() + "/com.accessibilityservice.activity.LoginActivity");
            return true;
        }
        return false;
    }

    //初始化执行任务
    public void task(AppModel appModel) {
        if (isStop) {
            return;
        }
        if (!AppUtils.isApplicationAvilible(appModel.getAppPackage())) {
            AccessibilityManager.sendMsg("请先安装" + appModel.getAppName());
            return;
        }
        AccessibilityManager.sendMsg("执行" + appModel.getAppName() + "脚本");
        Log.i("----", "开始执行== " + appModel.getAppName() + "脚本\n"
                + " appmodel== " + GsonUtils.toJson(appModel));
        runStartTime = System.currentTimeMillis();
        for (AppModel.AppPageModel model : appModel.getPages()) {
            clsList.add(model.getClassName());
        }
        this.appModel = appModel;
        doTask(appModel);
    }

    //开始执行任务
    public void doTask(AppModel appModel) {
        if (isStop()) return;
        if (!appModel.getAppPackage().equals(AppUtils.getTopActivity().getPkgName())) {
            AppUtils.startAppByPkg(MainApplication.getContext(), appModel.getAppPackage());
            Log.i("----", "打开== " + appModel.getAppName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (AppModel.AppPageModel model : appModel.getPages()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String topCls = AppUtils.getTopCls();
            if (TextUtils.isEmpty(topCls)) {
                doTask(appModel);
                return;
            }
            if (model.getClassName().equals(topCls)) {
                if (isStop()) break;
                Log.i("----", " getType== " + model.getType());
                if ("0".equals(model.getType())) {
                    for (String viewId : model.getViews()) {
                        AccessibilityManager.clickByViewId(viewId);
                    }
                    AccessibilityManager.clickByText("跳过");
                } else if ("1".equals(model.getType())) {
                    scrollDown(false, model);
                    for (String viewId : model.getViews()) {
                        AccessibilityManager.getInstance().clickByViewIdForList(viewId);
                    }
                } else if ("2".equals(model.getType())) {
                    scrollDown(true, model);
                    MyAccessibilityService.back();
                }
            } else if (!clsList.contains(topCls)) {
                if (topCls.contains("Video")) {
                    AccessibilityManager.sendMsg("跳过视屏");
                }
                Log.i("----", "没有该页面--返回");
                MyAccessibilityService.back();
            }
        }
        doTask(appModel);
    }

    //上拉
    private void scrollDown(boolean isDetails, AppModel.AppPageModel model) {
        Random random = new Random();
        int y;
        long planTime;
        int sleepTime;
        if (isDetails) {//详情页面
            sleepTime = random.nextInt(2) + 3;
            planTime = model.getPlanTime();
            y = random.nextInt(50) + 100;
        } else {//列表页面
            sleepTime = random.nextInt(2) + 2;
            y = random.nextInt(50) + 150;
            planTime = (random.nextInt(6) + 3) * 1000;
        }
        long lasTime = System.currentTimeMillis();
        for (; ; ) {
            if (isStop()) break;
            if (System.currentTimeMillis() - lasTime < planTime) {
                try {
                    Thread.sleep(sleepTime * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isStop()) break;
                ActivityInfo topActivity = AppUtils.getTopActivity();
                if (appModel.getAppPackage().equals(topActivity.getPkgName())) {
                    Shell.execute("input swipe " + y + " 600 " + y + " " + y);
                    String topCls = topActivity.getClsName();
                    if (!clsList.contains(topCls)) {
                        Log.i("----", "无此页面--返回");
                        MyAccessibilityService.back();
                        break;
                    } else if (!model.getClassName().equals(topCls)) {
                        Log.i("----", "不是该页面--退出");
                        break;
                    } else if (isDetails) {
                        for (String viewId : model.getViews()) {//点击阅读全文
                            AccessibilityManager.clickByViewId(viewId);
                        }
                        AccessibilityManager.clickByText("查看全文,阅读全文,展开全文");
                    }
                } else {
                    break;
                }
            } else {
                break;
            }
        }
    }

    //下拉刷新
    public void scrollUp() {
        Random random = new Random();
        int y = random.nextInt(50) + 100;
        Shell.execute("input swipe " + y + " " + y + " " + y + 400);
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    public void stop() {
        setStop(true);
        clsList.clear();
        AccessibilityManager.getInstance().clear();
        instance = null;
        Log.i("----", " 停止执行== " + (appModel == null ? "" : appModel.getAppName()) + "脚本");
        AccessibilityManager.sendMsg("已停止执行" + (appModel == null ? "" : appModel.getAppName()) + "脚本");
    }
}
