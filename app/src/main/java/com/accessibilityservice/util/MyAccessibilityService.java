package com.accessibilityservice.util;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;


public class MyAccessibilityService extends AccessibilityService {
    private static MyAccessibilityService instance;

    public static boolean back() {
        if (instance == null) {
            return false;
        }
        instance.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        return true;
    }

    public static boolean check() {
        return instance != null;
    }

    public static boolean click(AccessibilityNodeInfo accessibilityNodeInfo) {
        if (instance == null || accessibilityNodeInfo == null) {
            return false;
        }
        accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        return true;
    }

    public static void fullNodes(AccessibilityNodeInfo accessibilityNodeInfo, List list) {
        if (accessibilityNodeInfo != null) {
            for (int i = 0; i < accessibilityNodeInfo.getChildCount(); i++) {
                AccessibilityNodeInfo child = accessibilityNodeInfo.getChild(i);
                if (child != null) {
                    list.add(child);
                    fullNodes(child, list);
                }
            }
        }
    }

    public static List<AccessibilityNodeInfo> getList() {
        List arrayList = new ArrayList();
        if (instance != null) {
            AccessibilityNodeInfo rootInActiveWindow = instance.getRootInActiveWindow();
            if (rootInActiveWindow != null) {
                if (rootInActiveWindow.getParent() != null) {
                    arrayList.add(rootInActiveWindow.getParent());
                    fullNodes(rootInActiveWindow.getParent(), arrayList);
                } else {
                    arrayList.add(rootInActiveWindow);
                    fullNodes(rootInActiveWindow, arrayList);
                }
            }
        }
        return arrayList;
    }

    public static boolean home() {
        if (instance == null) {
            return false;
        }
        instance.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
        return true;
    }

    public static boolean longClick(AccessibilityNodeInfo accessibilityNodeInfo) {
        if (instance == null || accessibilityNodeInfo == null) {
            return false;
        }
        accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
        return true;
    }

    public static boolean scrollDown(AccessibilityNodeInfo accessibilityNodeInfo) {
        if (instance == null || accessibilityNodeInfo == null) {
            return false;
        }
        accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        return true;
    }

    public static boolean scrollUp(AccessibilityNodeInfo accessibilityNodeInfo) {
        if (instance == null || accessibilityNodeInfo == null) {
            return false;
        }
        accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        return true;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        AccessibilityManager.getNodeItems();
    }

    public void onCreate() {
        super.onCreate();
    }

    public void onDestroy() {
        stopForeground(true);
    }

    public void onInterrupt() {
        instance = null;
    }

    protected void onServiceConnected() {
        instance = this;
        super.onServiceConnected();
    }

    public boolean onUnbind(Intent intent) {
        instance = null;
        return super.onUnbind(intent);
    }
}
