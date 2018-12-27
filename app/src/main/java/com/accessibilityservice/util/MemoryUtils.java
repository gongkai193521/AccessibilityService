package com.accessibilityservice.util;

import android.app.ActivityManager;

import com.accessibilityservice.MainApplication;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by gongkai on 2018/12/27.
 */

public class MemoryUtils {
    /**
     * 清理内存
     */
    public static void clearMemory() {
        ActivityManager activityManger = (ActivityManager) MainApplication.getContext().getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appList = activityManger.getRunningAppProcesses();
        if (appList != null) {
            for (int i = 0; i < appList.size(); i++) {
                ActivityManager.RunningAppProcessInfo appInfo = appList.get(i);
                String[] pkgList = appInfo.pkgList;
                if (appInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    for (int j = 0; j < pkgList.length; j++) {
                        activityManger.killBackgroundProcesses(pkgList[j]);
                    }
                }
            }
        }
    }

}
