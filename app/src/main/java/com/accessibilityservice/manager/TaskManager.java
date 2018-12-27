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
import com.accessibilityservice.util.MemoryUtils;
import com.accessibilityservice.util.ShellUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.accessibilityservice.manager.AccessibilityManager.sendMsg;

/**
 * Created by gongkai on 2018/11/8.
 * 一个平台随机阅读10-20分钟
 * 主页 首次进去下拉刷新，1到2次随机滑动次数，1到2秒随机滑动一次
 * 详情页 6-15次随机滑动次数 3-5秒随机滑动一次
 * 每阅读4篇文章里，随机一篇上拉一次
 */

public class TaskManager {
    private volatile static TaskManager instance = null;

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
            ShellUtils.exec("am force-stop " + appModel.getAppPackage());
            ShellUtils.exec("am force-stop " + appModel.getAppPackage());
            ShellUtils.exec("am start -n " + MainApplication.getContext().getPackageName() + "/" + ScriptListActivity.class.getName());
            return true;
        }
        return false;
    }

    private static boolean isStop = false;//是否停止脚本
    private long runStartTime;//平台阅读开始时间
    private long runTime;//平台随机运行时间
    private static AppModel appModel;
    private List<String> clsList;
    private String homeCls;
    private boolean isRefresh;//主页是否下拉刷新
    private int index = 0;//阅读到第几篇了
    private int indexToRefresh = 0;//第几篇随机刷新

    //初始化执行任务
    public void task(AppModel appModel) {
        if (isStop) {
            return;
        }
        if (!AppUtils.isApplicationAvilible(appModel.getAppPackage())) {
            sendMsg("请先安装" + appModel.getAppName());
            return;
        }
        MemoryUtils.clearMemory();
        sendMsg("执行" + appModel.getAppName() + "脚本");
        Log.i("----", "开始执行-appmodel== " + GsonUtils.toJson(appModel));
        runStartTime = System.currentTimeMillis();
        runTime = getIntRandom(10, 20) * 60 * 1000;
        isRefresh = false;
        index = 0;
        indexToRefresh = 0;
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
                            for (int i = 0; i < getIntRandom(1, 5); i++) {
                                ShellUtils.execute("input swipe 600 600 600 1200");
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            isRefresh = true;
                        }
                        scrollDown(false, model);
                        for (String viewId : model.getViews()) {
                            AccessibilityManager.getInstance().clickByViewIdForList(viewId);
                        }
                    } else if ("2".equals(model.getType())) {
                        if (index == 0) {
                            indexToRefresh = getIntRandom(1, 4);
                        }
                        scrollDown(true, model);
                        backHome();
                        if (index == 4) {
                            index = 0;
                        }
                        Log.i("----", "阅读完毕回到主页== " + AppUtils.getTopCls());
                    }
                } else if (!clsList.contains(AppUtils.getTopCls())) {
                    if (AppUtils.getTopCls().contains("Video")) {
                        sendMsg("跳过广告视屏页面");
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

    //滑动阅读
    private void scrollDown(boolean isDetails, AppModel.AppPageModel model) {
        int sleepTime;
        int scroolCount;
        int count = 0;
        int indexDrop = 0;//详情页随机第几次下拉一次
        if (isDetails) {//详情页 6-15次随机滑动次数
            int minCount = model.getMinScrollCount();
            int maxCount = model.getMaxScrollCount();
            if (minCount == 0 && maxCount == 0) {
                minCount = 6;
                maxCount = 15;
            }
            scroolCount = getIntRandom(minCount, maxCount);
            indexDrop = getIntRandom(3, scroolCount);
            index++;
        } else {//主页 1到2次随机滑动次数
            scroolCount = getIntRandom(1, 2);
        }
        sendMsg("随机滑动" + scroolCount + "次");
        for (; ; ) {
            if (isStop()) break;
            int startX = getIntRandom(400, 800);
            int startY = getIntRandom(1200, 1500);
            int endX = getIntRandom(400, 800);
            int endY = getIntRandom(400, 800);
            int scroolTime = getIntRandom(200, 600);
            if (isDetails) {//详情页 3-5秒滑动一次
                sleepTime = getIntRandom(3, 5);
            } else {//主页 1到2秒滑动一次
                sleepTime = getIntRandom(1, 2);
            }
            Log.i("----", "startX=" + startX + " startY=" + startY
                    + " endX=" + endX + " endY=" + endY
                    + " scroolTime=" + scroolTime + " sleepTime == " + sleepTime
                    + " scroolCount == " + scroolCount);
            if (count < scroolCount) {
                count++;
                try {
                    sendMsg("随机等待" + sleepTime + "秒");
                    Thread.sleep(sleepTime * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ActivityInfo topActivity = AppUtils.getTopActivity();
                String topCls = topActivity.getClsName();
                String topPkg = topActivity.getPkgName();
                if (!clsList.contains(topCls) || !model.getClassName().equals(topCls)) {
                    Log.i("----", "不是该页面--回到主页");
                    backHome();
                    break;
                }
                if (appModel.getAppPackage().equals(topPkg)) {
                    if (isDetails) {
                        if (Build.VERSION.SDK_INT < 21) {
                            if (topPkg.equals("cn.weli.story") || topPkg.equals("com.martian.hbnews")) {
                                slide(startX, startY, endX, endY, scroolTime);
                                ShellUtils.exec("input keyevent 20");
                            } else {
                                ShellUtils.exec("input keyevent 20");
                                ShellUtils.exec("input keyevent 20");
                                ShellUtils.exec("input keyevent 20");
                                ShellUtils.exec("input keyevent 20");
                            }
                        } else if (indexToRefresh == index && count == indexDrop) {//下滑
                            slide(endX, endY, startX, startY, scroolTime);
                            sendMsg("随机下滑");
                            Log.i("----", "阅读到第" + index + "篇，滑动到第" + count + "次随机下滑");
                        } else {//上滑
                            slide(startX, startY, endX, endY, scroolTime);
                        }
                        for (String viewId : model.getViews()) {//点击阅读全文
                            AccessibilityManager.clickByViewId(viewId);
                        }
                        if (!"com.yanhui.qktx".equals(topPkg)) {
                            AccessibilityManager.clickByText("查看全文,阅读全文,展开全文");
                        }
                    } else {//上滑
                        slide(startX, startY, endX, endY, scroolTime);
                    }
                } else {
                    break;
                }
            } else {
                break;
            }
        }
    }

    private String scroll = "input swipe %s %s %s %s %s";

    //滑动

    private int slide(int startX, int startY, int endX, int endY, int scrollTime) {
        String format = String.format(scroll, startX, startY, endX, endY, scrollTime);
        return ShellUtils.execute(format);
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
        ShellUtils.exec("am start -n " + pkg + "/" + cls);
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
            ShellUtils.exec("am start -n " + appModel.getAppPackage() + "/" + homeCls);
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
        sendMsg("已停止执行" + (appModel == null ? "" : appModel.getAppName()) + "脚本");
    }
}
