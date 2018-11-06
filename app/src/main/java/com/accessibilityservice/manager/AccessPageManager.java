package com.accessibilityservice.manager;

import com.accessibilityservice.MainApplication;

/**
 * Created by gackor on 16/8/1.
 */
public class AccessPageManager {
    private static AccessPageManager mInstance = null;

    public static AccessPageManager getInstance() {
        if (mInstance == null) {
            synchronized (AccessPageManager.class) {
                if (mInstance == null) {
                    mInstance = new AccessPageManager();
                }
            }
        }
        return mInstance;
    }

    private String currentPkg = MainApplication.getContext().getPackageName();
    private String currentCls = "";

    public String getCurrentPkgName() {
        return currentPkg;
    }

    public void setCurrentPkgName(String pkgName) {
        currentPkg = pkgName;
    }

    public String getCurentClsName() {
        return currentCls;
    }

    public void setCurentClsName(String clsName) {
        currentCls = clsName;
    }

    public boolean isCurrentClsName(Class cls) {
        return cls.getName().equals(currentCls);
    }
}
