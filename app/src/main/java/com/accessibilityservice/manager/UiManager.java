package com.accessibilityservice.manager;

import android.content.Context;
import android.content.Intent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.accessibilityservice.service.MyAccessibilityService;

import org.json.JSONArray;

import java.util.List;

public class UiManager {
    public Context context;

    public UiManager(Context context) {
        this.context = context;
    }

    public boolean check() {
        if (!MyAccessibilityService.check()) {
            Intent intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.context.startActivity(intent);
            return false;
        }
        return true;
    }

    public JSONArray getClassNames() {
        List list = getList();
        JSONArray jSONArray = new JSONArray();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= list.size()) {
                return jSONArray;
            }
            AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) list.get(i2);
            if (accessibilityNodeInfo != null) {
                CharSequence className = accessibilityNodeInfo.getClassName();
                if (className != null) {
                    jSONArray.put(className.toString());
                }
            }
            i = i2 + 1;
        }
    }

    public JSONArray getDescs() {
        List list = getList();
        JSONArray jSONArray = new JSONArray();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= list.size()) {
                return jSONArray;
            }
            AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) list.get(i2);
            if (accessibilityNodeInfo != null) {
                CharSequence contentDescription = accessibilityNodeInfo.getContentDescription();
                if (contentDescription != null) {
                    jSONArray.put(contentDescription.toString());
                }
            }
            i = i2 + 1;
        }
    }

    public JSONArray getIds() {
        List list = getList();
        JSONArray jSONArray = new JSONArray();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= list.size()) {
                return jSONArray;
            }
            AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) list.get(i2);
            if (accessibilityNodeInfo != null) {
                String viewIdResourceName = accessibilityNodeInfo.getViewIdResourceName();
                if (viewIdResourceName != null) {
                    jSONArray.put(viewIdResourceName);
                }
            }
            i = i2 + 1;
        }
    }

    public List getList() {
        return MyAccessibilityService.getList();
    }

    public JSONArray getTexts() {
        List list = getList();
        JSONArray jSONArray = new JSONArray();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= list.size()) {
                return jSONArray;
            }
            AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) list.get(i2);
            if (accessibilityNodeInfo != null) {
                CharSequence text = accessibilityNodeInfo.getText();
                if (text != null) {
                    jSONArray.put(text.toString());
                }
            }
            i = i2 + 1;
        }
    }
}
