package com.accessibilityservice.util;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

import static com.accessibilityservice.MainApplication.mHandler;

/**
 * Created by gongkai on 2018/11/7.
 */

public class AccessibilityManager {
    private static List<String> mList = new ArrayList<>();

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean clickByViewIdForList(String viewId) {
        for (AccessibilityNodeInfo nodeInfo : MyAccessibilityService.getList()) {
            if (viewId.equals(nodeInfo.getViewIdResourceName())
                    && nodeInfo.getText() != null) {
                final String text = nodeInfo.getText().toString();
                if (!mList.toArray().toString().contains(text)) {
                    Log.i("----", "text == " + text);
                    Rect rect2 = new Rect();
                    nodeInfo.getBoundsInScreen(rect2);
                    Shell.execute("input tap " + rect2.left + " " + rect2.top);
                    mList.add(text);
                    Message message = mHandler.obtainMessage();
                    message.obj = "阅读" + text;
                    mHandler.sendMessage(message);
                    break;
                }
            }
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean clickByViewId(String viewId) {
        for (AccessibilityNodeInfo nodeInfo : MyAccessibilityService.getList()) {
            if (viewId.equals(nodeInfo.getViewIdResourceName())
                    && nodeInfo.getText() != null) {
                Rect rect2 = new Rect();
                nodeInfo.getBoundsInScreen(rect2);
                Shell.execute("input tap " + rect2.left + " " + rect2.top);
                final String text = nodeInfo.getText().toString();
                Log.i("----", "text == " + text);
                Message message = mHandler.obtainMessage();
                message.obj = "点击" + text;
                mHandler.sendMessage(message);
                break;
            }
        }
        return false;
    }

    public static void clear() {
        mList.clear();
    }
}
