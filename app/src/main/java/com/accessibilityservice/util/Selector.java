package com.accessibilityservice.util;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;


import com.accessibilityservice.manager.UiManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Selector {
    private List copyList;
    private List list;
    private Threads threads;
    private UiManager ui;
    private Selector mSelector;

    public class Fn {
        public boolean onCall(Object obj) {
            return false;
        }
    }

    public Selector(List list, UiManager uiManager) {
        this.threads = null;
        this.threads = new Threads();
        init(list, list, this.ui);
    }

    public Selector(List list, List list2, UiManager uiManager) {
        this.threads = null;
        init(list, list2, uiManager);
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
        map.put("isPassword", accessibilityNodeInfo.isChecked());
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

    private String getParentNodeId(AccessibilityNodeInfo accessibilityNodeInfo) {
        if (accessibilityNodeInfo == null) {
            return "";
        }
        try {
            String str = AccessibilityNodeInfo.class.getDeclaredMethod("getParentNodeId", new Class[0]).invoke(accessibilityNodeInfo, new Object[0]) + "";
            return str == null ? "" : str;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getSourceNodeId(AccessibilityNodeInfo accessibilityNodeInfo) {
        if (accessibilityNodeInfo == null) {
            return "";
        }
        try {
            String str = AccessibilityNodeInfo.class.getDeclaredMethod("getSourceNodeId", new Class[0]).invoke(accessibilityNodeInfo, new Object[0]) + "";
            return str == null ? "" : str;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void init(List list, List list2, UiManager uiManager) {
        this.list = list;
        if (list2 == null) {
            this.copyList = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                this.copyList.add(list.get(i));
            }
        } else {
            this.copyList = list2;
        }
        this.ui = uiManager;
    }

    public Selector _each(Fn fn) {
        int i = 0;
        while (true) {
            try {
                int i2 = i;
                if (!this.ui.isRun() || i2 >= this.list.size()) {
                    return this;
                }
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) this.list.get(i2);
                if (accessibilityNodeInfo != null) {
                    fn.onCall(accessibilityNodeInfo);
                }
                i = i2 + 1;
            } catch (Exception e) {
                throw e;
            }
        }
    }

    public Selector filter(Fn fn) {
        List arrayList = new ArrayList();
        try {
            int i = 0;
            while (true) {
                int i2 = i;
                if (this.ui.isRun() && i2 < this.list.size()) {
                    AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) this.list.get(i2);
                    if (accessibilityNodeInfo != null && true == fn.onCall(accessibilityNodeInfo)) {
                        arrayList.add(accessibilityNodeInfo);
                    }
                    i = i2 + 1;
                }
            }
        } catch (Exception e) {
        }
        return new Selector(arrayList, this.copyList, this.ui);
    }

    public boolean back() {
        return this.ui.back();
    }

    public boolean click() {
        return click(0);
    }

    public boolean click(int i) {
        AccessibilityNodeInfo item = getItem(i);
        return item != null && item.isClickable() && this.ui.click(item);
    }

    public boolean clickNext() {
        return clickNext(50);
    }

    public boolean clickNext(int i) {
        for (Selector next = getNext(); !next.isEmpty() && this.ui.isRun() && i != 0; next = next.getNext()) {
            if (next.isClickable()) {
                return next.click();
            }
            i--;
        }
        return false;
    }

    public boolean clickParent() {
        return clickParent(50);
    }

    public boolean clickParent(int i) {
        for (Selector parent = getParent(); !parent.isEmpty() && this.ui.isRun() && i != 0; parent = parent.getParent()) {
            if (parent.isClickable()) {
                return parent.click();
            }
            i--;
        }
        return false;
    }

    public boolean clickPrev() {
        return clickPrev(50);
    }

    public boolean clickPrev(int i) {
        for (Selector prev = getPrev(); !prev.isEmpty() && this.ui.isRun() && i != 0; prev = prev.getPrev()) {
            if (prev.isClickable()) {
                return prev.click();
            }
            i--;
        }
        return false;
    }

    public Selector clickable() {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                return ((AccessibilityNodeInfo) obj).isClickable();
            }
        });
    }

    public Selector containsByClass(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return (accessibilityNodeInfo.getClass() == null || -1 == accessibilityNodeInfo.getClassName().toString().indexOf(str)) ? false : true;
            }
        });
    }

    public Selector containsByDesc(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return (accessibilityNodeInfo.getContentDescription() == null || -1 == accessibilityNodeInfo.getContentDescription().toString().indexOf(str)) ? false : true;
            }
        });
    }

    public Selector containsById(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return (accessibilityNodeInfo.getViewIdResourceName() == null || -1 == accessibilityNodeInfo.getViewIdResourceName().toString().indexOf(str)) ? false : true;
            }
        });
    }

    public Selector containsByText(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return (accessibilityNodeInfo.getText() == null || -1 == accessibilityNodeInfo.getText().toString().indexOf(str)) ? false : true;
            }
        });
    }

    public Selector editable() {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                return ((AccessibilityNodeInfo) obj).isEditable();
            }
        });
    }

    public Selector enabled() {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                return ((AccessibilityNodeInfo) obj).isEditable();
            }
        });
    }

    public Selector endsWithByClass(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return accessibilityNodeInfo.getClass() != null && accessibilityNodeInfo.getClassName().toString().endsWith(str);
            }
        });
    }

    public Selector endsWithByDesc(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return accessibilityNodeInfo.getContentDescription() != null && accessibilityNodeInfo.getContentDescription().toString().endsWith(str);
            }
        });
    }

    public Selector endsWithById(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return accessibilityNodeInfo.getViewIdResourceName() != null && accessibilityNodeInfo.getViewIdResourceName().toString().endsWith(str);
            }
        });
    }

    public Selector endsWithByText(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return accessibilityNodeInfo.getText() != null && accessibilityNodeInfo.getText().toString().endsWith(str);
            }
        });
    }

    public Selector eq(int i) {
        List arrayList = new ArrayList();
        arrayList.add(getItem(i));
        return new Selector(arrayList, this.copyList, this.ui);
    }


    public Selector findByClass(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return accessibilityNodeInfo.getClass() != null && accessibilityNodeInfo.getClassName().toString().equals(str);
            }
        });
    }

    public Selector findByClassMatchs(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return accessibilityNodeInfo.getText() != null && Pattern.matches(str, accessibilityNodeInfo.getClassName().toString());
            }
        });
    }

    public Selector findByDesc(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return accessibilityNodeInfo.getContentDescription() != null && accessibilityNodeInfo.getContentDescription().toString().equals(str);
            }
        });
    }

    public Selector findByDescMatchs(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return accessibilityNodeInfo.getText() != null && Pattern.matches(str, accessibilityNodeInfo.getContentDescription().toString());
            }
        });
    }

    public Selector findById(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return accessibilityNodeInfo.getViewIdResourceName() != null && accessibilityNodeInfo.getViewIdResourceName().toString().equals(str);
            }
        });
    }

    public Selector findByIdMatchs(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return accessibilityNodeInfo.getText() != null && Pattern.matches(str, accessibilityNodeInfo.getViewIdResourceName().toString());
            }
        });
    }

    public Selector findByText(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return accessibilityNodeInfo.getText() != null && accessibilityNodeInfo.getText().toString().equals(str);
            }
        });
    }

    public Selector findByTextMatchs(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return accessibilityNodeInfo.getText() != null && Pattern.matches(str, accessibilityNodeInfo.getText().toString());
            }
        });
    }

    public Selector focused() {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                return ((AccessibilityNodeInfo) obj).isFocused();
            }
        });
    }

    public Rect getBoundsInParent() {
        Rect rect = new Rect();
        AccessibilityNodeInfo item = getItem();
        if (item != null) {
            item.getBoundsInParent(rect);
        }
        return rect;
    }

    public Rect getBoundsInScreen() {
        Rect rect = new Rect();
        AccessibilityNodeInfo item = getItem();
        if (item != null) {
            item.getBoundsInScreen(rect);
        }
        return rect;
    }

    public Selector getChildren() {
        AccessibilityNodeInfo item = getItem();
        List list = this.copyList;
        List arrayList = new ArrayList();
        if (item == null) {
            return this;
        }
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= list.size()) {
                return new Selector(arrayList, this.copyList, this.ui);
            }
            AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) list.get(i2);
            if (getParentNodeId(accessibilityNodeInfo).equals(getSourceNodeId(item))) {
                arrayList.add(accessibilityNodeInfo);
            }
            i = i2 + 1;
        }
    }

    public String getClassName() {
        AccessibilityNodeInfo item = getItem();
        return (item == null || item.getClassName() == null) ? "" : item.getClassName().toString();
    }

    public String getDesc() {
        AccessibilityNodeInfo item = getItem();
        return (item == null || item.getContentDescription() == null) ? "" : item.getContentDescription().toString();
    }

    public Selector getFirst() {
        if (this.list.size() <= 0) {
            return this;
        }
        List arrayList = new ArrayList();
        arrayList.add(this.list.get(0));
        return new Selector(arrayList, this.copyList, this.ui);
    }

    public String getId() {
        AccessibilityNodeInfo item = getItem();
        return item != null ? item.getViewIdResourceName() : "";
    }

    public int getIndex() {
        AccessibilityNodeInfo item = getItem();
        List list = this.copyList;
        if (item == null) {
            return -1;
        }
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= list.size()) {
                return -1;
            }
            AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) list.get(i2);
            String sourceNodeId = getSourceNodeId(item);
            String sourceNodeId2 = getSourceNodeId(accessibilityNodeInfo);
            if (sourceNodeId != null && sourceNodeId2 != null && sourceNodeId.equals(sourceNodeId2)) {
                return i2;
            }
            i = i2 + 1;
        }
    }

    public AccessibilityNodeInfo getItem() {
        return getItem(0);
    }

    public AccessibilityNodeInfo getItem(int i) {
        List list = this.list;
        return (list.size() == 0 || i + 1 > list.size()) ? null : (AccessibilityNodeInfo) list.get(i);
    }

    public Selector getLast() {
        if (this.list.size() <= 0) {
            return this;
        }
        List arrayList = new ArrayList();
        arrayList.add(this.list.get(this.list.size() - 1));
        return new Selector(arrayList, this.copyList, this.ui);
    }

    public Selector getNext() {
        List list = this.copyList;
        int index = getIndex();
        if (-1 == index || index + 1 >= list.size()) {
            return this;
        }
        List arrayList = new ArrayList();
        arrayList.add(list.get(index + 1));
        return new Selector(arrayList, this.copyList, this.ui);
    }

    public Selector getNextCarryClass() {
        return getNextCarryClass(50);
    }

    public Selector getNextCarryClass(int i) {
        for (Selector next = getNext(); !next.isEmpty() && this.ui.isRun() && i != 0; next = next.getNext()) {
            if (!"".equals(next.getClassName())) {
                return next;
            }
            i--;
        }
        return this;
    }

    public Selector getNextCarryDesc() {
        return getNextCarryDesc(50);
    }

    public Selector getNextCarryDesc(int i) {
        for (Selector next = getNext(); !next.isEmpty() && this.ui.isRun() && i != 0; next = next.getNext()) {
            if (!"".equals(next.getDesc())) {
                return next;
            }
            i--;
        }
        return this;
    }

    public Selector getNextCarryId() {
        return getNextCarryId(50);
    }

    public Selector getNextCarryId(int i) {
        for (Selector next = getNext(); !next.isEmpty() && this.ui.isRun() && i != 0; next = next.getNext()) {
            if (!"".equals(next.getId())) {
                return next;
            }
            i--;
        }
        return this;
    }

    public Selector getNextCarryText() {
        return getNextCarryText(50);
    }

    public Selector getNextCarryText(int i) {
        for (Selector next = getNext(); !next.isEmpty() && this.ui.isRun() && i != 0; next = next.getNext()) {
            if (!"".equals(next.getText())) {
                return next;
            }
            i--;
        }
        return this;
    }

    public Selector getNextContainsByClass(String str) {
        return getNextContainsByClass(str, 20);
    }

    public Selector getNextContainsByClass(String str, int i) {
        Selector next = getNext();
        while (!next.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(next.getId())) {
                i--;
                next = next.getNext();
            } else {
                Selector containsByClass = next.containsByClass(str);
                if (!containsByClass.isEmpty()) {
                    return containsByClass;
                }
                i--;
                next = next.getNext();
            }
        }
        return this;
    }

    public Selector getNextContainsByDesc(String str) {
        return getNextContainsByDesc(str, 20);
    }

    public Selector getNextContainsByDesc(String str, int i) {
        Selector next = getNext();
        while (!next.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(next.getId())) {
                i--;
                next = next.getNext();
            } else {
                Selector containsByDesc = next.containsByDesc(str);
                if (!containsByDesc.isEmpty()) {
                    return containsByDesc;
                }
                i--;
                next = next.getNext();
            }
        }
        return this;
    }

    public Selector getNextContainsById(String str) {
        return getNextContainsById(str, 20);
    }

    public Selector getNextContainsById(String str, int i) {
        Selector next = getNext();
        while (!next.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(next.getId())) {
                i--;
                next = next.getNext();
            } else {
                Selector containsById = next.containsById(str);
                if (!containsById.isEmpty()) {
                    return containsById;
                }
                i--;
                next = next.getNext();
            }
        }
        return this;
    }

    public Selector getNextContainsByText(String str) {
        return getNextContainsByText(str, 20);
    }

    public Selector getNextContainsByText(String str, int i) {
        Selector next = getNext();
        while (!next.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(next.getId())) {
                i--;
                next = next.getNext();
            } else {
                Selector containsByText = next.containsByText(str);
                if (!containsByText.isEmpty()) {
                    return containsByText;
                }
                i--;
                next = next.getNext();
            }
        }
        return this;
    }

    public Selector getNextEndsWithByClass(String str) {
        return getNextEndsWithByClass(str, 20);
    }

    public Selector getNextEndsWithByClass(String str, int i) {
        Selector next = getNext();
        while (!next.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(next.getId())) {
                i--;
                next = next.getNext();
            } else {
                Selector endsWithByClass = next.endsWithByClass(str);
                if (!endsWithByClass.isEmpty()) {
                    return endsWithByClass;
                }
                i--;
                next = next.getNext();
            }
        }
        return this;
    }

    public Selector getNextEndsWithById(String str) {
        return getNextEndsWithById(str, 20);
    }

    public Selector getNextEndsWithById(String str, int i) {
        Selector next = getNext();
        while (!next.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(next.getId())) {
                i--;
                next = next.getNext();
            } else {
                Selector endsWithById = next.endsWithById(str);
                if (!endsWithById.isEmpty()) {
                    return endsWithById;
                }
                i--;
                next = next.getNext();
            }
        }
        return this;
    }

    public Selector getNextEndsWithByText(String str, int i) {
        Selector next = getNext();
        while (!next.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(next.getId())) {
                i--;
                next = next.getNext();
            } else {
                Selector endsWithByText = next.endsWithByText(str);
                if (!endsWithByText.isEmpty()) {
                    return endsWithByText;
                }
                i--;
                next = next.getNext();
            }
        }
        return this;
    }

    public Selector getNextEndsWithFindByDesc(String str) {
        return getNextEndsWithFindByDesc(str, 20);
    }

    public Selector getNextEndsWithFindByDesc(String str, int i) {
        Selector next = getNext();
        while (!next.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(next.getId())) {
                i--;
                next = next.getNext();
            } else {
                Selector endsWithByDesc = next.endsWithByDesc(str);
                if (!endsWithByDesc.isEmpty()) {
                    return endsWithByDesc;
                }
                i--;
                next = next.getNext();
            }
        }
        return this;
    }

    public Selector getNextEndssWithByText(String str) {
        return getNextEndsWithByText(str, 20);
    }

    public Selector getNextFindByClass(String str) {
        return getNextFindByClass(str, 20);
    }

    public Selector getNextFindByClass(String str, int i) {
        Selector next = getNext();
        while (!next.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(next.getId())) {
                i--;
                next = next.getNext();
            } else {
                Selector findByClass = next.findByClass(str);
                if (!findByClass.isEmpty()) {
                    return findByClass;
                }
                i--;
                next = next.getNext();
            }
        }
        return this;
    }

    public Selector getNextFindByDesc(String str) {
        return getNextFindByDesc(str, 20);
    }

    public Selector getNextFindByDesc(String str, int i) {
        Selector next = getNext();
        while (!next.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(next.getId())) {
                i--;
                next = next.getNext();
            } else {
                Selector findByDesc = next.findByDesc(str);
                if (!findByDesc.isEmpty()) {
                    return findByDesc;
                }
                i--;
                next = next.getNext();
            }
        }
        return this;
    }

    public Selector getNextFindById(String str) {
        return getNextFindById(str, 20);
    }

    public Selector getNextFindById(String str, int i) {
        Selector next = getNext();
        while (!next.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(next.getId())) {
                i--;
                next = next.getNext();
            } else {
                Selector findById = next.findById(str);
                if (!findById.isEmpty()) {
                    return findById;
                }
                i--;
                next = next.getNext();
            }
        }
        return this;
    }

    public Selector getNextFindByText(String str) {
        return getNextFindByText(str, 20);
    }

    public Selector getNextFindByText(String str, int i) {
        Selector next = getNext();
        while (!next.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(next.getId())) {
                i--;
                next = next.getNext();
            } else {
                Selector findByText = next.findByText(str);
                if (!findByText.isEmpty()) {
                    return findByText;
                }
                i--;
                next = next.getNext();
            }
        }
        return this;
    }

    public Selector getNextSartsWithById(String str) {
        return getNextSartsWithById(str, 20);
    }

    public Selector getNextSartsWithById(String str, int i) {
        Selector next = getNext();
        while (!next.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(next.getId())) {
                i--;
                next = next.getNext();
            } else {
                Selector startsWithById = next.startsWithById(str);
                if (!startsWithById.isEmpty()) {
                    return startsWithById;
                }
                i--;
                next = next.getNext();
            }
        }
        return this;
    }

    public Selector getNextStartsWithByClass(String str) {
        return getNextStartsWithByClass(str, 20);
    }

    public Selector getNextStartsWithByClass(String str, int i) {
        Selector next = getNext();
        while (!next.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(next.getId())) {
                i--;
                next = next.getNext();
            } else {
                Selector startsWithByClass = next.startsWithByClass(str);
                if (!startsWithByClass.isEmpty()) {
                    return startsWithByClass;
                }
                i--;
                next = next.getNext();
            }
        }
        return this;
    }

    public Selector getNextStartsWithByText(String str) {
        return getNextStartsWithByText(str, 20);
    }

    public Selector getNextStartsWithByText(String str, int i) {
        Selector next = getNext();
        while (!next.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(next.getId())) {
                i--;
                next = next.getNext();
            } else {
                Selector startsWithByText = next.startsWithByText(str);
                if (!startsWithByText.isEmpty()) {
                    return startsWithByText;
                }
                i--;
                next = next.getNext();
            }
        }
        return this;
    }

    public Selector getNextStartsWithFindByDesc(String str) {
        return getNextStartsWithFindByDesc(str, 20);
    }

    public Selector getNextStartsWithFindByDesc(String str, int i) {
        Selector next = getNext();
        while (!next.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(next.getId())) {
                i--;
                next = next.getNext();
            } else {
                Selector startsWithByDesc = next.startsWithByDesc(str);
                if (!startsWithByDesc.isEmpty()) {
                    return startsWithByDesc;
                }
                i--;
                next = next.getNext();
            }
        }
        return this;
    }

    public Selector getParent() {
        try {
            List list = this.copyList;
            AccessibilityNodeInfo item = getItem();
            List arrayList = new ArrayList();
            String parentNodeId = getParentNodeId(item);
            if (item == null) {
                return this;
            }
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= list.size()) {
                    return new Selector(arrayList, this.copyList, this.ui);
                }
                item = (AccessibilityNodeInfo) list.get(i2);
                if (parentNodeId.equals(getSourceNodeId(item))) {
                    arrayList.add(item);
                }
                i = i2 + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }

    public Selector getParentCarryClass() {
        return getParentCarryClass(50);
    }

    public Selector getParentCarryClass(int i) {
        for (Selector parent = getParent(); !parent.isEmpty() && this.ui.isRun() && i != 0; parent = parent.getParent()) {
            if (!"".equals(parent.getClassName())) {
                return parent;
            }
            i--;
        }
        return this;
    }

    public Selector getParentCarryDesc() {
        return getParentCarryDesc(50);
    }

    public Selector getParentCarryDesc(int i) {
        for (Selector parent = getParent(); !parent.isEmpty() && this.ui.isRun() && i != 0; parent = parent.getParent()) {
            if (!"".equals(parent.getDesc())) {
                return parent;
            }
            i--;
        }
        return this;
    }

    public Selector getParentCarryId() {
        return getParentCarryId(50);
    }

    public Selector getParentCarryId(int i) {
        for (Selector parent = getParent(); !parent.isEmpty() && this.ui.isRun() && i != 0; parent = parent.getParent()) {
            if (!"".equals(parent.getId())) {
                return parent;
            }
            i--;
        }
        return this;
    }

    public Selector getParentCarryText() {
        return getParentCarryText(50);
    }

    public Selector getParentCarryText(int i) {
        for (Selector parent = getParent(); !parent.isEmpty() && this.ui.isRun() && i != 0; parent = parent.getParent()) {
            if (!"".equals(parent.getText())) {
                return parent;
            }
            i--;
        }
        return this;
    }

    public Selector getPrev() {
        List list = this.copyList;
        int index = getIndex();
        if (-1 == index || index == 0) {
            return this;
        }
        List arrayList = new ArrayList();
        arrayList.add(list.get(index - 1));
        return new Selector(arrayList, this.copyList, this.ui);
    }

    public Selector getPrevCarryClass() {
        return getPrevCarryClass(50);
    }

    public Selector getPrevCarryClass(int i) {
        for (Selector prev = getPrev(); !prev.isEmpty() && this.ui.isRun() && i != 0; prev = prev.getPrev()) {
            if (!"".equals(prev.getClassName())) {
                return prev;
            }
            i--;
        }
        return this;
    }

    public Selector getPrevCarryDesc() {
        return getPrevCarryDesc(50);
    }

    public Selector getPrevCarryDesc(int i) {
        for (Selector prev = getPrev(); !prev.isEmpty() && this.ui.isRun() && i != 0; prev = prev.getPrev()) {
            if (!"".equals(prev.getDesc())) {
                return prev;
            }
            i--;
        }
        return this;
    }

    public Selector getPrevCarryId() {
        return getPrevCarryId(50);
    }

    public Selector getPrevCarryId(int i) {
        for (Selector prev = getPrev(); !prev.isEmpty() && this.ui.isRun() && i != 0; prev = prev.getPrev()) {
            if (!"".equals(prev.getId())) {
                return prev;
            }
            i--;
        }
        return this;
    }

    public Selector getPrevCarryText() {
        return getPrevCarryText(50);
    }

    public Selector getPrevCarryText(int i) {
        for (Selector prev = getPrev(); !prev.isEmpty() && this.ui.isRun() && i != 0; prev = prev.getPrev()) {
            if (!"".equals(prev.getText())) {
                return prev;
            }
            i--;
        }
        return this;
    }

    public Selector getPrevContainsByClass(String str) {
        return getPrevContainsByClass(str, 20);
    }

    public Selector getPrevContainsByClass(String str, int i) {
        Selector prev = getPrev();
        while (!prev.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(prev.getId())) {
                i--;
                prev = prev.getPrev();
            } else {
                Selector containsByClass = prev.containsByClass(str);
                if (!containsByClass.isEmpty()) {
                    return containsByClass;
                }
                i--;
                prev = prev.getPrev();
            }
        }
        return this;
    }

    public Selector getPrevContainsByDesc(String str) {
        return getPrevContainsByDesc(str, 20);
    }

    public Selector getPrevContainsByDesc(String str, int i) {
        Selector prev = getPrev();
        while (!prev.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(prev.getId())) {
                i--;
                prev = prev.getPrev();
            } else {
                Selector containsByDesc = prev.containsByDesc(str);
                if (!containsByDesc.isEmpty()) {
                    return containsByDesc;
                }
                i--;
                prev = prev.getPrev();
            }
        }
        return this;
    }

    public Selector getPrevContainsById(String str) {
        return getPrevContainsById(str, 20);
    }

    public Selector getPrevContainsById(String str, int i) {
        Selector prev = getPrev();
        while (!prev.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(prev.getId())) {
                i--;
                prev = prev.getPrev();
            } else {
                Selector containsById = prev.containsById(str);
                if (!containsById.isEmpty()) {
                    return containsById;
                }
                i--;
                prev = prev.getPrev();
            }
        }
        return this;
    }

    public Selector getPrevContainsByText(String str) {
        return getPrevContainsByText(str, 20);
    }

    public Selector getPrevContainsByText(String str, int i) {
        Selector next = getNext();
        while (!next.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(next.getId())) {
                i--;
                next = next.getPrev();
            } else {
                Selector containsByText = next.containsByText(str);
                if (!containsByText.isEmpty()) {
                    return containsByText;
                }
                i--;
                next = next.getPrev();
            }
        }
        return this;
    }

    public Selector getPrevEndsWithByClass(String str) {
        return getPrevEndsWithByClass(str, 20);
    }

    public Selector getPrevEndsWithByClass(String str, int i) {
        Selector prev = getPrev();
        while (!prev.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(prev.getId())) {
                i--;
                prev = prev.getPrev();
            } else {
                Selector endsWithByClass = prev.endsWithByClass(str);
                if (!endsWithByClass.isEmpty()) {
                    return endsWithByClass;
                }
                i--;
                prev = prev.getPrev();
            }
        }
        return this;
    }

    public Selector getPrevEndsWithById(String str) {
        return getPrevEndsWithById(str, 20);
    }

    public Selector getPrevEndsWithById(String str, int i) {
        Selector next = getNext();
        while (!next.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(next.getId())) {
                i--;
                next = next.getPrev();
            } else {
                Selector endsWithById = next.endsWithById(str);
                if (!endsWithById.isEmpty()) {
                    return endsWithById;
                }
                i--;
                next = next.getPrev();
            }
        }
        return this;
    }

    public Selector getPrevEndsWithByText(String str) {
        return getPrevEndsWithByText(str, 20);
    }

    public Selector getPrevEndsWithByText(String str, int i) {
        Selector prev = getPrev();
        while (!prev.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(prev.getId())) {
                i--;
                prev = prev.getPrev();
            } else {
                Selector endsWithByText = prev.endsWithByText(str);
                if (!endsWithByText.isEmpty()) {
                    return endsWithByText;
                }
                i--;
                prev = prev.getPrev();
            }
        }
        return this;
    }

    public Selector getPrevEndsWithFindByDesc(String str) {
        return getPrevEndsWithFindByDesc(str, 20);
    }

    public Selector getPrevEndsWithFindByDesc(String str, int i) {
        Selector prev = getPrev();
        while (!prev.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(prev.getId())) {
                i--;
                prev = prev.getPrev();
            } else {
                Selector endsWithByDesc = prev.endsWithByDesc(str);
                if (!endsWithByDesc.isEmpty()) {
                    return endsWithByDesc;
                }
                i--;
                prev = prev.getPrev();
            }
        }
        return this;
    }

    public Selector getPrevFindByClass(String str) {
        return getPrevFindByClass(str, 20);
    }

    public Selector getPrevFindByClass(String str, int i) {
        Selector prev = getPrev();
        while (!prev.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(prev.getId())) {
                i--;
                prev = prev.getPrev();
            } else {
                Selector findByClass = prev.findByClass(str);
                if (!findByClass.isEmpty()) {
                    return findByClass;
                }
                i--;
                prev = prev.getPrev();
            }
        }
        return this;
    }

    public Selector getPrevFindByDesc(String str) {
        return getPrevFindByDesc(str, 20);
    }

    public Selector getPrevFindByDesc(String str, int i) {
        Selector prev = getPrev();
        while (!prev.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(prev.getId())) {
                i--;
                prev = prev.getPrev();
            } else {
                Selector findByDesc = prev.findByDesc(str);
                if (!findByDesc.isEmpty()) {
                    return findByDesc;
                }
                i--;
                prev = prev.getPrev();
            }
        }
        return this;
    }

    public Selector getPrevFindById(String str) {
        return getPrevFindById(str, 20);
    }

    public Selector getPrevFindById(String str, int i) {
        Selector prev = getPrev();
        while (!prev.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(prev.getId())) {
                i--;
                prev = prev.getPrev();
            } else {
                Selector findById = prev.findById(str);
                if (!findById.isEmpty()) {
                    return findById;
                }
                i--;
                prev = prev.getPrev();
            }
        }
        return this;
    }

    public Selector getPrevFindByText(String str) {
        return getPrevFindByText(str, 20);
    }

    public Selector getPrevFindByText(String str, int i) {
        Selector prev = getPrev();
        while (!prev.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(prev.getId())) {
                i--;
                prev = prev.getPrev();
            } else {
                Selector findByText = prev.findByText(str);
                if (!findByText.isEmpty()) {
                    return findByText;
                }
                i--;
                prev = prev.getPrev();
            }
        }
        return this;
    }

    public Selector getPrevSartsWithById(String str) {
        return getPrevSartsWithById(str, 20);
    }

    public Selector getPrevSartsWithById(String str, int i) {
        Selector prev = getPrev();
        while (!prev.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(prev.getId())) {
                i--;
                prev = prev.getPrev();
            } else {
                Selector startsWithById = prev.startsWithById(str);
                if (!startsWithById.isEmpty()) {
                    return startsWithById;
                }
                i--;
                prev = prev.getPrev();
            }
        }
        return this;
    }

    public Selector getPrevStartsWithByClass(String str) {
        return getPrevStartsWithByClass(str, 20);
    }

    public Selector getPrevStartsWithByClass(String str, int i) {
        Selector prev = getPrev();
        while (!prev.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(prev.getId())) {
                i--;
                prev = prev.getPrev();
            } else {
                Selector startsWithByClass = prev.startsWithByClass(str);
                if (!startsWithByClass.isEmpty()) {
                    return startsWithByClass;
                }
                i--;
                prev = prev.getPrev();
            }
        }
        return this;
    }

    public Selector getPrevStartsWithByText(String str) {
        return getPrevStartsWithByText(str, 20);
    }

    public Selector getPrevStartsWithByText(String str, int i) {
        Selector prev = getPrev();
        while (!prev.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(prev.getId())) {
                i--;
                prev = prev.getPrev();
            } else {
                Selector startsWithByText = prev.startsWithByText(str);
                if (!startsWithByText.isEmpty()) {
                    return startsWithByText;
                }
                i--;
                prev = prev.getPrev();
            }
        }
        return this;
    }

    public Selector getPrevStartsWithFindByDesc(String str) {
        return getPrevStartsWithFindByDesc(str, 20);
    }

    public Selector getPrevStartsWithFindByDesc(String str, int i) {
        Selector prev = getPrev();
        while (!prev.isEmpty() && this.ui.isRun() && i != 0) {
            if ("".equals(prev.getId())) {
                i--;
                prev = prev.getPrev();
            } else {
                Selector startsWithByDesc = prev.startsWithByDesc(str);
                if (!startsWithByDesc.isEmpty()) {
                    return startsWithByDesc;
                }
                i--;
                prev = prev.getPrev();
            }
        }
        return this;
    }

    public Selector getSiblings() {
        final AccessibilityNodeInfo item = getItem();
        return item == null ? this : getParent().getChildren().filter(new Fn() {
            public boolean onCall(Object obj) {
                return !Selector.this.getSourceNodeId((AccessibilityNodeInfo) obj).equals(Selector.this.getSourceNodeId(item));
            }
        });
    }

    public int getSize() {
        return this.list.size();
    }

    public String getText() {
        AccessibilityNodeInfo item = getItem();
        return (item == null || item.getText() == null) ? "" : item.getText().toString();
    }

    public boolean isChecked() {
        AccessibilityNodeInfo item = getItem();
        return item != null ? item.isChecked() : false;
    }

    public boolean isClickable() {
        AccessibilityNodeInfo item = getItem();
        return item != null ? item.isClickable() : false;
    }

    public boolean isEditable() {
        AccessibilityNodeInfo item = getItem();
        return item != null ? item.isEditable() : false;
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public boolean isFocused() {
        AccessibilityNodeInfo item = getItem();
        return item != null ? item.isFocused() : false;
    }

    public boolean isLongClickable() {
        AccessibilityNodeInfo item = getItem();
        return item != null ? item.isLongClickable() : false;
    }

    public boolean isScrollable() {
        AccessibilityNodeInfo item = getItem();
        return item != null ? item.isScrollable() : false;
    }

    public boolean isSelected() {
        AccessibilityNodeInfo item = getItem();
        return item != null ? item.isSelected() : false;
    }

    public Selector listClasses() {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                return (obj == null || ((AccessibilityNodeInfo) obj).getClass() == null) ? false : true;
            }
        });
    }

    public Selector listDescs() {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                return (obj == null || ((AccessibilityNodeInfo) obj).getContentDescription() == null) ? false : true;
            }
        });
    }

    public Selector listIds() {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                return (obj == null || obj == null || ((AccessibilityNodeInfo) obj).getViewIdResourceName() == null) ? false : true;
            }
        });
    }

    public Selector listTexts() {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                return (obj == null || ((AccessibilityNodeInfo) obj).getText() == null) ? false : true;
            }
        });
    }

    public boolean longClick() {
        return longClick(0);
    }

    public boolean longClick(int i) {
        AccessibilityNodeInfo item = getItem(i);
        return item != null && item.isLongClickable() && this.ui.longClick(item);
    }

    public boolean longClickNext() {
        return longClickNext(50);
    }

    public boolean longClickNext(int i) {
        for (Selector next = getNext(); !next.isEmpty() && this.ui.isRun() && i != 0; next = next.getNext()) {
            if (next.isClickable()) {
                return next.longClick();
            }
            i--;
        }
        return false;
    }

    public boolean longClickPrev() {
        return longClickPrev(50);
    }

    public boolean longClickPrev(int i) {
        for (Selector prev = getPrev(); !prev.isEmpty() && this.ui.isRun() && i != 0; prev = prev.getPrev()) {
            if (prev.isClickable()) {
                return prev.longClick();
            }
            i--;
        }
        return false;
    }

    public Selector longClickable() {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                return ((AccessibilityNodeInfo) obj).isLongClickable();
            }
        });
    }

    public Selector repeatGetChildren() {
        return repeatGetChildren(1);
    }

    public Selector repeatGetChildren(int i) {
        for (int i2 = 0; i2 < i; i2++) {
            mSelector = this.getChildren();
        }
        return mSelector;
    }

    public Selector repeatGetNext() {
        return repeatGetNext(1);
    }

    public Selector repeatGetNext(int i) {
        for (int i2 = 0; i2 < i; i2++) {
            mSelector = this.getNext();
        }
        return mSelector;
    }

    public Selector repeatGetParent() {
        return repeatGetParent(1);
    }

    public Selector repeatGetParent(int i) {
        for (int i2 = 0; i2 < i; i2++) {
            mSelector = this.getParent();
        }
        return mSelector;
    }

    public Selector repeatGetPrev() {
        return repeatGetPrev(1);
    }

    public Selector repeatGetPrev(int i) {
        for (int i2 = 0; i2 < i; i2++) {
            mSelector = this.getPrev();
        }
        return mSelector;
    }

    public boolean scrollDown() {
        return scrollDown(0);
    }

    public boolean scrollDown(int i) {
        AccessibilityNodeInfo item = getItem(i);
        return item != null ? this.ui.scrollDown(item) : false;
    }

    public boolean scrollUp() {
        return scrollUp(0);
    }

    public boolean scrollUp(int i) {
        AccessibilityNodeInfo item = getItem(i);
        return item != null ? this.ui.scrollUp(item) : false;
    }

    public Selector scrollable() {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                return ((AccessibilityNodeInfo) obj).isScrollable();
            }
        });
    }

    public Selector selected() {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                return ((AccessibilityNodeInfo) obj).isSelected();
            }
        });
    }

    public Selector startsWithByClass(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return accessibilityNodeInfo.getClass() != null && accessibilityNodeInfo.getClassName().toString().startsWith(str);
            }
        });
    }

    public Selector startsWithByDesc(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return accessibilityNodeInfo.getContentDescription() != null && accessibilityNodeInfo.getContentDescription().toString().startsWith(str);
            }
        });
    }

    public Selector startsWithById(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return accessibilityNodeInfo.getViewIdResourceName() != null && accessibilityNodeInfo.getViewIdResourceName().toString().startsWith(str);
            }
        });
    }

    public Selector startsWithByText(final String str) {
        return filter(new Fn() {
            public boolean onCall(Object obj) {
                AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
                return accessibilityNodeInfo.getText() != null && accessibilityNodeInfo.getText().toString().startsWith(str);
            }
        });
    }

    public List<Map<String, Object>> getNodeItems() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            list.add(getNodeItem((AccessibilityNodeInfo) this.list.get(i)));
        }
        return list;
    }
}
