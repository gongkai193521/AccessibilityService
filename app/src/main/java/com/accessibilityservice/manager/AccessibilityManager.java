package com.accessibilityservice.manager;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.accessibilityservice.service.MyAccessibilityService;
import com.accessibilityservice.util.ShellUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.accessibilityservice.MainApplication.mHandler;

/**
 * Created by gongkai on 2018/11/7.
 */

public class AccessibilityManager {
    private volatile static AccessibilityManager instance = null;
    private static List<String> textList;

    AccessibilityManager() {
        if (textList == null) {
            textList = new ArrayList<>();
        }
    }

    public static AccessibilityManager getInstance() {
        if (instance == null) {
            synchronized (AccessibilityManager.class) {
                if (instance == null) {
                    instance = new AccessibilityManager();
                }
            }
        }
        return instance;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean clickByViewIdForList(String viewId) {
        ArrayList<AccessibilityNodeInfo> nodeInfos = new ArrayList<>();
        for (AccessibilityNodeInfo nodeInfo : MyAccessibilityService.getList()) {
            if (viewId.equals(nodeInfo.getViewIdResourceName())) {
                nodeInfos.add(nodeInfo);
            }
        }
        AccessibilityNodeInfo nodeInfo = null;
        if (nodeInfos.size() > 1) {
            nodeInfo = nodeInfos.get(1);
        } else if (nodeInfos.size() > 0) {
            nodeInfo = nodeInfos.get(0);
        }
        if (nodeInfo != null) {
            Rect rect2 = new Rect();
            nodeInfo.getBoundsInScreen(rect2);
            if (nodeInfo.getText() != null) {
                final String text = nodeInfo.getText().toString();
                if (!textList.toArray().toString().contains(text)) {
                    tap(rect2.left, rect2.top);
                    textList.add(text);
                    sendMsg("阅读" + text);
                }
            } else {
                tap(rect2.left, rect2.top);
            }
        }
        return false;
    }

    private String click = "input tap %s %s ";

    //点击
    private int tap(int tapX, int tapY) {
        int left = tapX; //+ getIntRandom(100, 500);
        int top = tapY;//+ getIntRandom(10, 100);
        String format = String.format(click, left, top);
        Log.i("----", "点击：tapX=" + left + " tapY=" + top);
        return ShellUtils.execute(format);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean clickByViewId(String viewId) {
        for (AccessibilityNodeInfo nodeInfo : MyAccessibilityService.getList()) {
            if (nodeInfo.isVisibleToUser() && viewId.equals(nodeInfo.getViewIdResourceName())) {
                Rect rect2 = new Rect();
                nodeInfo.getBoundsInScreen(rect2);
                ShellUtils.execute("input tap " + rect2.left + " " + rect2.top);
                Log.i("----", "点击viewId== " + viewId);
                if (nodeInfo.getText() != null) {
                    String text = nodeInfo.getText().toString();
                    Log.i("----", "点击 == " + text);
                }
                break;
            }
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean clickByViewIdAndText(String viewId, String text) {
        for (AccessibilityNodeInfo nodeInfo : MyAccessibilityService.getList()) {
            if (viewId.equals(nodeInfo.getViewIdResourceName())) {
                CharSequence charSequence = nodeInfo.getText();
                if (charSequence != null && text.equals(charSequence.toString())) {
                    Rect rect2 = new Rect();
                    nodeInfo.getBoundsInScreen(rect2);
                    ShellUtils.execute("input tap " + rect2.left + " " + rect2.top);
                    Log.i("----", "点击viewId== " + viewId + " text==" + text);
                    break;
                }
            }
        }
        return false;
    }

    public static void clickBack() {
        ShellUtils.execute("input keyevent 4");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean clickByText(String text) {
        for (AccessibilityNodeInfo nodeInfo : MyAccessibilityService.getList()) {
            if (!nodeInfo.isVisibleToUser()) {
                return false;
            }
            CharSequence text1 = nodeInfo.getText();
            CharSequence description = nodeInfo.getContentDescription();
            String[] strings;
            if (text.contains(",")) {
                strings = text.split(",");
            } else {
                strings = new String[]{text};
            }
            for (String s : strings) {
                if ((text1 != null && text1.toString().contains(s))
                        || (description != null && description.toString().contains(s))) {
                    Rect rect2 = new Rect();
                    nodeInfo.getBoundsInScreen(rect2);
                    ShellUtils.execute("input tap " + rect2.left + " " + rect2.top);
                    Log.i("----", "点击text== " + s);
                    break;
                }
            }
        }
        return false;
    }

    private int getIntRandom(int min, int max) {
        Random random = new Random();
        int num = random.nextInt(max) % (max - min + 1) + min;
        return num;
    }

    public static void sendMsg(String msg) {
        Message message = mHandler.obtainMessage();
        message.obj = msg;
        mHandler.sendMessage(message);
    }

    public void clear() {
        textList.clear();
        instance = null;
    }
}
