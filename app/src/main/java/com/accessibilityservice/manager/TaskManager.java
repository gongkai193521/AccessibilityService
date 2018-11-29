package com.accessibilityservice.manager;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.accessibilityservice.MainApplication;
import com.accessibilityservice.model.ActivityInfo;
import com.accessibilityservice.model.AppModel;
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
    private List<String> viewIdList;
    private String homeCls;

    private TaskManager() {
        if (clsList == null) {
            clsList = new ArrayList<>();
            viewIdList = new ArrayList<>();
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
            Shell.exec("am start -n " + MainApplication.getContext().getPackageName() + "/com.accessibilityservice.activity.ScriptListActivity");
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
            if (model.getType().equals("1")) {
                homeCls = model.getClassName();
            } else if (model.getType().equals("0")) {
                viewIdList.addAll(model.getViews());
            }
        }
        this.appModel = appModel;
        doTask(appModel);
    }

    //开始执行任务
    public void doTask(AppModel appModel) {
        if (isStop()) return;
        if (!appModel.getAppPackage().equals(AppUtils.getTopActivity().getPkgName())) {
            Log.i("----", "打开== " + appModel.getAppName());
//            AppUtils.startAppByPkg(MainApplication.getContext(), appModel.getAppPackage());
            backHome();
        }
        String topCls = AppUtils.getTopCls();
        if (!TextUtils.isEmpty(topCls)) {
            boolean isHaved=false;
            for (AppModel.AppPageModel model : appModel.getPages()){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (model.getClassName().equals(topCls)){
                    isHaved=true;
                    if (isStop()) break;
                    Log.i("----", " getType== " + model.getType());
                    if ("0".equals(model.getType())) {
                        AccessibilityManager.clickByText("跳过");
                        for (String viewId : model.getViews()) {
                            AccessibilityManager.clickByViewId(viewId);
                        }
                    } else if ("1".equals(model.getType())) {
                        scrollDown(false, model);
                        for (String viewId : model.getViews()) {
                            AccessibilityManager.getInstance().clickByViewIdForList(viewId);
                        }
                    } else if ("2".equals(model.getType())) {
                        scrollDown(true, model);
                        backHome();
                    }
                }
            }
            if (!isHaved){
                AccessibilityManager.clickBack();
            }
//
//            for (AppModel.AppPageModel model : appModel.getPages()) {
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if (model.getClassName().equals(topCls)) {
//                    if (isStop()) break;
//                    Log.i("----", " getType== " + model.getType());
//                    if ("0".equals(model.getType())) {
//                        AccessibilityManager.clickByText("跳过");
//                        for (String viewId : model.getViews()) {
//                            AccessibilityManager.clickByViewId(viewId);
//                        }
//                    } else if ("1".equals(model.getType())) {
//                        scrollDown(false, model);
//                        for (String viewId : model.getViews()) {
//                            AccessibilityManager.getInstance().clickByViewIdForList(viewId);
//                        }
//                    } else if ("2".equals(model.getType())) {
//                        scrollDown(true, model);
//                        backHome();
//                    }
//                } else if (!clsList.contains(topCls)) {
//                    if (topCls.contains("Video")) {
//                        AccessibilityManager.sendMsg("跳过广告视屏页面");
//                    }
//                    Log.i("----", "没有该页面--回到主页");
//                    backHome();
//                }
//            }
        }
        doTask(appModel);
    }

    //上拉
    private void scrollDown(boolean isDetails, AppModel.AppPageModel model) {
        Random random = new Random();
        final int y = random.nextInt(50) + 100;
        long planTime;
        int sleepTime;
        if (isDetails) {//详情页
            sleepTime = random.nextInt(2) + 2;
            planTime = model.getPlanTime();
        } else {//主页
            sleepTime = random.nextInt(2) + 2;
            planTime = (random.nextInt(5) + 3) * 1000;
        }
        long lasTime = System.currentTimeMillis();
        for (; ; ) {
            if (isStop()) break;
            if (System.currentTimeMillis() - lasTime < planTime) {
                for (String viewId : model.getViews()) {
                    AccessibilityManager.clickByViewId(viewId);
                }
                if (isDetails) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                ActivityInfo topActivity = AppUtils.getTopActivity();
                if (appModel.getAppPackage().equals(topActivity.getPkgName())) {
                    if (Build.VERSION.SDK_INT < 21 && isDetails) {
                        if (topActivity.getPkgName().equals("cn.weli.story")||topActivity.getPkgName().equals("com.martian.hbnews")){
                            Shell.execute("input swipe " + y + " 600 " + y + " " + y);
                            Shell.exec("input keyevent 20");
                        }else {
                            Shell.exec("input keyevent 20");
                            Shell.exec("input keyevent 20");
                            Shell.exec("input keyevent 20");
                            Shell.exec("input keyevent 20");
                        }
                    } else {
                        Shell.execute("input swipe " + y + " 600 " + y + " " + y);
                    }
                    String topCls = topActivity.getClsName();
//                    !clsList.contains(topCls) ||
                    if (!model.getClassName().equals(topCls)) {
                        Log.i("----", "不是该页面--回到主页");
//                        backHome();
                        AccessibilityManager.clickBack();
                        break;
                    } else if (isDetails) {
                        for (String viewId : model.getViews()) {//点击阅读全文
                            AccessibilityManager.clickByViewId(viewId);
                        }
//                        AccessibilityManager.clickByText("查看全文,阅读全文,展开全文");
                    }
                } else {
                    break;
                }
                try {
                    Thread.sleep(sleepTime * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
    }

    private void backHome() {
        Shell.exec("am start -n " + appModel.getAppPackage() + "/" + homeCls);
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
        viewIdList.clear();
        AccessibilityManager.getInstance().clear();
        instance = null;
        Log.i("----", " 停止执行== " + (appModel == null ? "" : appModel.getAppName()) + "脚本");
        AccessibilityManager.sendMsg("已停止执行" + (appModel == null ? "" : appModel.getAppName()) + "脚本");
    }
}
