package com.accessibilityservice.util;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static Map<String, Object> getNodeItem(AccessibilityNodeInfo accessibilityNodeInfo) {
        if (accessibilityNodeInfo == null) {
            return null;
        }
        String charSequence;
        Map<String, Object> map = new HashMap<>();
        CharSequence text = accessibilityNodeInfo.getText();
        CharSequence contentDescription = accessibilityNodeInfo.getContentDescription();
        if (text != null) {
            charSequence = text.toString();
            if (!"".equals(charSequence)) {
                map.put("text", charSequence);
            }
        }
        if (contentDescription != null) {
            charSequence = contentDescription.toString();
            if (!"".equals(charSequence)) {
                map.put("desc", charSequence);
            }
        }
        map.put("className", accessibilityNodeInfo.getClassName());
        map.put("id", accessibilityNodeInfo.getViewIdResourceName());
        map.put("isPassword", accessibilityNodeInfo.isPassword());
        map.put("isChecked", accessibilityNodeInfo.isChecked());
        map.put("isClickable", accessibilityNodeInfo.isClickable());
        map.put("isCheckable", accessibilityNodeInfo.isCheckable());
        map.put("isEditable", accessibilityNodeInfo.isEditable());
        map.put("isFocused", accessibilityNodeInfo.isFocused());
        map.put("isEnabled", accessibilityNodeInfo.isEnabled());
        map.put("isLongClickable", accessibilityNodeInfo.isLongClickable());
        map.put("isSelected", accessibilityNodeInfo.isSelected());
        map.put("isScrollable", accessibilityNodeInfo.isScrollable());
        Rect rect = new Rect();
        accessibilityNodeInfo.getBoundsInParent(rect);
        Rect rect2 = new Rect();
        accessibilityNodeInfo.getBoundsInScreen(rect2);
        map.put("boundsInParent", GsonUtils.toJson(rect));
        map.put("boundsInScreen", GsonUtils.toJson(rect2));
        return map;
    }

    public static List<Map<String, Object>> getNodeItems() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < MyAccessibilityService.getList().size(); i++) {
            list.add(getNodeItem(MyAccessibilityService.getList().get(i)));
            Log.i("----", " == " + getNodeItem(MyAccessibilityService.getList().get(i)).toString());
        }
        return list;
    }

    public static void clear() {
        mList.clear();
    }
}
