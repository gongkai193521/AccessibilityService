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
    private long runTime;
    private AppModel appModel;
    private List<String> clsList;
    private String homeCls;
    private boolean isRefresh;

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
                stop();
            }
        }
        //一个平台10-20分钟
        if (isStop || System.currentTimeMillis() - runStartTime >= runTime) {
            Shell.exec("am force-stop " + appModel.getAppPackage(), true);
            Shell.exec("am start -n " + MainApplication.getContext().getPackageName() + "/" + ScriptListActivity.class.getName());
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
        Log.i("----", "开始执行-appmodel== " + GsonUtils.toJson(appModel));
        runStartTime = System.currentTimeMillis();
        runTime = getIntRandom(10, 20) * 60 * 1000;
        isRefresh = false;
        this.appModel = appModel;
        for (AppModel.AppPageModel model : appModel.getPages()) {
            clsList.add(model.getClassName());
            if (model.getType().equals("1")) {
                homeCls = model.getClassName();
            }
        }
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
                        if (!isRefresh) {
                            Shell.execute("input swipe 600 600 600 1200");
                            isRefresh = true;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
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
        int sleepTime;
        int scroolCount;
        int count = 0;
        if (isDetails) {//详情页 6-20次随机滑动次数
            scroolCount = getIntRandom(6, 20);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {//主页 1到3次随机滑动次数
            scroolCount = getIntRandom(1, 2);
        }
        Log.i("----", "scroolCount == " + scroolCount);
        for (; ; ) {
            if (isStop()) break;
            int y = getIntRandom(300, 500);
            if (isDetails) {//详情页 3-5秒滑动一次
                sleepTime = getIntRandom(3, 5);
            } else {//主页 2到5秒滑动一次
                sleepTime = getIntRandom(1, 2);
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
                    if (isDetails) {
                        if (Build.VERSION.SDK_INT < 21) {
                            if (topActivity.getPkgName().equals("cn.weli.story")
                                    || topActivity.getPkgName().equals("com.martian.hbnews")) {
                                Shell.execute("input swipe " + y + " 1300 " + y + " " + y);
                                Shell.exec("input keyevent 20");
                            } else {
                                Shell.exec("input keyevent 20");
                                Shell.exec("input keyevent 20");
                                Shell.exec("input keyevent 20");
                                Shell.exec("input keyevent 20");
                            }
                        } else if (getBooleanRandom()) {//下滑
                            Shell.execute("input swipe " + y + " " + y + " " + y + " 1000 ");
                        } else {//上滑
                            Shell.execute("input swipe " + y + " 1300 " + y + " " + y + " ");
                        }
                    } else {//上滑
                        Shell.execute("input swipe " + y + " 1300 " + y + " " + y + " ");
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

    private void sign() {
        switch (AppUtils.getTopPkg()) {
            case "com.cashtoutiao"://惠头条
                AccessibilityManager.clickByViewIdAndText("com.cashtoutiao:id/count_down_tv", "点击领取");
                AccessibilityManager.clickByText("任务中心");
                AccessibilityManager.clickByViewId("com.cashtoutiao:id/sign_btn_container");
                break;
            case "cn.viaweb.toutiao"://阅新闻
                AccessibilityManager.clickByText("领取");
                AccessibilityManager.clickByViewIdAndText("cn.viaweb.toutiao:id/smallLabel", "任务中心");
                AccessibilityManager.clickByText("今日签到");
                AccessibilityManager.clickByViewIdAndText("cn.viaweb.toutiao:id/smallLabel", "头条");
                break;
            case "cn.youth.news"://中青看点
                skip("cn.youth.news", "com.weishang.wxrd.activity.MoreActivity");
                AccessibilityManager.clickByText("签到领红包");
                break;
            case "com.zhangku.qukandian"://趣看点
                AccessibilityManager.clickByText("可领取");
                AccessibilityManager.clickByViewIdAndText("com.zhangku.qukandian:id/activity_main_tab_shoutu", "任务");
                AccessibilityManager.clickByViewIdAndText("com.zhangku.qukandian:id/signTV", "签到");
                AccessibilityManager.clickByViewIdAndText("com.zhangku.qukandian:id/activity_main_tab_information", "首页");
                break;
            case "com.huolea.bull"://牛牛头条
                AccessibilityManager.clickByText("点击领取");
                AccessibilityManager.clickByViewIdAndText("com.huolea.bull:id/id_layout_navigation_task_text", "每日金币");
                AccessibilityManager.clickByText("签到");
                AccessibilityManager.clickByViewIdAndText("com.huolea.bull:id/id_layout_navigation_news_text", "资讯");
                break;
            case "com.ifeng.kuaitoutiao"://快头条
                skip("com.ifeng.kuaitoutiao", "com.ifeng.news2.advertise.AdDetailActivity");
                AccessibilityManager.clickByText("立即签到");
                break;
            case "com.martian.hbnews"://红包头条
                skip("com.martian.hbnews", "com.martian.hbnews.activity.MainActivity");
                AccessibilityManager.clickByViewIdAndText("com.martian.hbnews:id/tab_textview", "任务");
                AccessibilityManager.clickByViewIdAndText("com.martian.hbnews:id/mc_sign", "签到");
                AccessibilityManager.clickByViewIdAndText("com.martian.hbnews:id/tab_textview", "头条");
                break;
            case "com.songheng.eastnews"://东方头条
                AccessibilityManager.clickByViewIdAndText("com.songheng.eastnews:id/jx", "任务");
                AccessibilityManager.clickByViewIdAndText("signIn", "立即签到");
                AccessibilityManager.clickByViewIdAndText("com.songheng.eastnews:id/jq", "新闻");
                break;
            case "cn.weli.story"://微鲤看看
                AccessibilityManager.clickByText("签到");//cn.weli.story:id/iv_check_day
                AccessibilityManager.clickByText("立即签到");
                break;
            case "com.zm.news"://福头条
                AccessibilityManager.clickByText("签到");
                break;
            case "com.ldzs.zhangxin"://蚂蚁头条
                AccessibilityManager.clickByViewId("com.ldzs.zhangxin:id/time_period_sign_in");
                break;
            case "com.expflow.reading"://悦头条
                AccessibilityManager.clickByViewIdAndText("com.expflow.reading:id/tv_counter_new", "阅读领取");
                break;
            default:
                break;
        }
    }

    private void skip(String pkg, String cls) {
        Shell.exec("am start -n " + pkg + "/" + cls);
    }

    private int getIntRandom(int min, int max) {
        Random random = new Random();
        int num = random.nextInt(max) % (max - min + 1) + min;
        return num;
    }

    private boolean getBooleanRandom() {
        Random random = new Random();
        return random.nextInt(10) < 3;
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
