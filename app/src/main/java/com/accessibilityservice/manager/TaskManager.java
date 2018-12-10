package com.accessibilityservice.manager;

import android.os.Build;
import android.util.Log;

import com.accessibilityservice.MainApplication;
import com.accessibilityservice.activity.ScriptListActivity;
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
    private String homeCls;

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
        ScriptListActivity activity = ScriptListActivity.getActivity();
        if (activity != null) {
            if (activity.getMaxTime() != null && System.currentTimeMillis() >= activity.getMaxTime()) {
                TaskManager.getInstance().stop();
            }
        }
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
            }
        }
        this.appModel = appModel;
        doTask(appModel);
    }

    //开始执行任务
    public void doTask(AppModel appModel) {
        if (isStop()) return;
        if (!appModel.getAppPackage().equals(AppUtils.getTopPkg())) {
            Log.i("----", "打开== " + appModel.getAppName());
            AppUtils.startAppByPkg(MainApplication.getContext(), appModel.getAppPackage());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (appModel.getAppPackage().equals(AppUtils.getTopPkg())) {
            for (AppModel.AppPageModel model : appModel.getPages()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (model.getClassName().equals(AppUtils.getTopCls())) {
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
                        Log.i("----", "阅读完毕回到主页== " + AppUtils.getTopCls());
                    }
                } else if (!clsList.contains(AppUtils.getTopCls())) {
                    if (AppUtils.getTopCls().contains("Video")) {
                        AccessibilityManager.sendMsg("跳过广告视屏页面");
                    }
                    Log.i("----", "没有该页面--回到主页");
                    backHome();
                    break;
                }
            }
        }
        Log.i("----", "接着执行");
        doTask(appModel);
    }

    //上拉
    private void scrollDown(boolean isDetails, AppModel.AppPageModel model) {
        int y;
        int sleepTime;
        int scroolCount;
        int count = 0;
        if (isDetails) {//详情页 4-13次随机滑动次数，3-10秒滑动一次
            scroolCount = getRandom(4, 13);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {//主页 1到3次随机滑动次数， 2到5秒滑动一次
            scroolCount = getRandom(1, 3);
        }
        Log.i("----", "scroolCount == " + scroolCount);
        for (; ; ) {
            if (isStop()) break;
            y = getRandom(100, 300);
            if (isDetails) {//详情页 4-13次随机滑动次数，3-10秒滑动一次
                sleepTime = getRandom(3, 10);
            } else {//主页 1到3次随机滑动次数， 2到5秒滑动一次
                sleepTime = getRandom(2, 5);
            }
            Log.i("----", "y == " + y);
            Log.i("----", "sleepTime == " + sleepTime);
            if (count < scroolCount) {
                count++;
                ActivityInfo topActivity = AppUtils.getTopActivity();
                String topCls = topActivity.getClsName();
                if (!clsList.contains(topCls) || !model.getClassName().equals(topCls)) {
                    Log.i("----", "不是该页面--回到主页");
                    backHome();
                    break;
                } else if (isDetails) {
                    for (String viewId : model.getViews()) {//点击阅读全文
                        AccessibilityManager.clickByViewId(viewId);
                    }
                    if (!"com.yanhui.qktx".equals(topActivity.getPkgName())) {
                        AccessibilityManager.clickByText("查看全文,阅读全文,展开全文");
                    }
                }
                if (appModel.getAppPackage().equals(topActivity.getPkgName())) {
                    if (Build.VERSION.SDK_INT < 21 && isDetails) {
                        if (topActivity.getPkgName().equals("cn.weli.story") || topActivity.getPkgName().equals("com.martian.hbnews")) {
                            Shell.execute("input swipe " + y + " 800 " + y + " " + y);
                            Shell.exec("input keyevent 20");
                        } else {
                            Shell.exec("input keyevent 20");
                            Shell.exec("input keyevent 20");
                            Shell.exec("input keyevent 20");
                            Shell.exec("input keyevent 20");
                        }
                    } else {
                        Shell.execute("input swipe " + y + " 800 " + y + " " + y);
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

    private int getRandom(int min, int max) {
        Random random = new Random();
        int num = random.nextInt(max) % (max - min + 1) + min;
        return num;
    }

    private void backHome() {
        //阅新闻和搜狐资讯
        if ("cn.viaweb.toutiao".equals(AppUtils.getTopPkg())
                || "com.sohu.infonews".equals(AppUtils.getTopPkg())) {
            MyAccessibilityService.back();
        } else {
            Shell.exec("am start -n " + appModel.getAppPackage() + "/" + homeCls);
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

    public boolean getStop() {
        return isStop;
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
