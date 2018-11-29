package com.accessibilityservice.manager;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.accessibilityservice.service.MyAccessibilityService;
import com.accessibilityservice.util.Shell;

import java.util.ArrayList;
import java.util.List;

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
        if (nodeInfos.size() > 0) {
            AccessibilityNodeInfo nodeInfo = nodeInfos.get(1);
            Rect rect2 = new Rect();
            nodeInfo.getBoundsInScreen(rect2);
            if (nodeInfo.getText() != null) {
                final String text = nodeInfo.getText().toString();
                if (!textList.toArray().toString().contains(text)) {
                    Shell.execute("input tap " + rect2.left + " " + rect2.top);
                    Log.i("----", "text == " + text);
                    textList.add(text);
                    sendMsg("阅读" + text);
                }
            } else {
                Shell.execute("input tap " + rect2.left + " " + rect2.top);
            }
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean clickByViewId(String viewId) {
        for (AccessibilityNodeInfo nodeInfo : MyAccessibilityService.getList()) {
            if (viewId.equals(nodeInfo.getViewIdResourceName())) {
                Rect rect2 = new Rect();
                nodeInfo.getBoundsInScreen(rect2);
                Shell.execute("input tap " + rect2.left + " " + rect2.top);
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
    public static boolean clickByRect() {
        Shell.execute("input tap 340 440");
        return false;
    }

    public static void clickBack() {
        Shell.execute("input keyevent 4");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean clickByText(String text) {
        for (AccessibilityNodeInfo nodeInfo : MyAccessibilityService.getList()) {
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
                    Shell.execute("input tap " + rect2.left + " " + rect2.top);
                    Log.i("----", "点击text== " + s);
                    break;
                }
            }
        }
        return false;
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
