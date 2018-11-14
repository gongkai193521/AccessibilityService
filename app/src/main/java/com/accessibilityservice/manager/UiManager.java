package com.accessibilityservice.manager;

import android.content.Context;
import android.content.Intent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.accessibilityservice.service.MyAccessibilityService;
import com.accessibilityservice.util.Selector;

import org.json.JSONArray;

import java.util.List;

public class UiManager {
    public Context context;

    public UiManager(Context context) {
        this.context = context;
    }

    public boolean back() {
        return MyAccessibilityService.back();
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

    public boolean click(AccessibilityNodeInfo accessibilityNodeInfo) {
        return MyAccessibilityService.click(accessibilityNodeInfo);
    }

    public Selector clickable() {
        return new Selector(MyAccessibilityService.getList(), this).clickable();
    }

    public Selector containsByClass(String str) {
        return new Selector(MyAccessibilityService.getList(), this).containsByClass(str);
    }

    public Selector containsByDesc(String str) {
        return new Selector(MyAccessibilityService.getList(), this).containsByDesc(str);
    }

    public Selector containsById(String str) {
        return new Selector(MyAccessibilityService.getList(), this).containsById(str);
    }

    public Selector containsByText(String str) {
        return new Selector(MyAccessibilityService.getList(), this).containsByText(str);
    }

    public Selector editable() {
        return new Selector(MyAccessibilityService.getList(), this).editable();
    }

    public Selector enabled() {
        return new Selector(MyAccessibilityService.getList(), this).enabled();
    }

    public Selector endsWithByClass(String str) {
        return new Selector(MyAccessibilityService.getList(), this).endsWithByClass(str);
    }

    public Selector endsWithByDesc(String str) {
        return new Selector(MyAccessibilityService.getList(), this).endsWithByDesc(str);
    }

    public Selector endsWithById(String str) {
        return new Selector(MyAccessibilityService.getList(), this).endsWithById(str);
    }

    public Selector endsWithByText(String str) {
        return new Selector(MyAccessibilityService.getList(), this).endsWithByText(str);
    }

    public Selector findByClass(String str) {
        return new Selector(MyAccessibilityService.getList(), this).findByClass(str);
    }

    public Selector findByDesc(String str) {
        return new Selector(MyAccessibilityService.getList(), this).findByDesc(str);
    }

    public Selector findById(String str) {
        return new Selector(MyAccessibilityService.getList(), this).findById(str);
    }

    public Selector findByText(String str) {
        return new Selector(MyAccessibilityService.getList(), this).findByText(str);
    }

    public Selector focused() {
        return new Selector(MyAccessibilityService.getList(), this).focused();
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

    public boolean home() {
        return MyAccessibilityService.home();
    }

    public boolean isRun() {
        return true;
    }

    public Selector listClasses() {
        return new Selector(MyAccessibilityService.getList(), this).listClasses();
    }

    public Selector listDescs() {
        return new Selector(MyAccessibilityService.getList(), this).listDescs();
    }

    public Selector listIds() {
        return new Selector(MyAccessibilityService.getList(), this).listIds();
    }

    public Selector listTexts() {
        return new Selector(MyAccessibilityService.getList(), this).listTexts();
    }

    public boolean longClick(AccessibilityNodeInfo accessibilityNodeInfo) {
        return MyAccessibilityService.longClick(accessibilityNodeInfo);
    }

    public Selector longClickable() {
        return new Selector(MyAccessibilityService.getList(), this).longClickable();
    }

    public boolean scrollDown(AccessibilityNodeInfo accessibilityNodeInfo) {
        return MyAccessibilityService.scrollDown(accessibilityNodeInfo);
    }

    public boolean scrollUp(AccessibilityNodeInfo accessibilityNodeInfo) {
        return MyAccessibilityService.scrollUp(accessibilityNodeInfo);
    }

    public Selector scrollable() {
        return new Selector(MyAccessibilityService.getList(), this).scrollable();
    }

    public Selector selected() {
        return new Selector(MyAccessibilityService.getList(), this).selected();
    }

    public Selector startsWithByClass(String str) {
        return new Selector(MyAccessibilityService.getList(), this).startsWithByClass(str);
    }

    public Selector startsWithByDesc(String str) {
        return new Selector(MyAccessibilityService.getList(), this).startsWithByDesc(str);
    }

    public Selector startsWithByText(String str) {
        return new Selector(MyAccessibilityService.getList(), this).startsWithByText(str);
    }
}
