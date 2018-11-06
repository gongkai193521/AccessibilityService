package com.accessibilityservice.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;

import com.accessibilityservice.MainApplication;

import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.accessibilityservice.MainApplication.getPkgManager;

/**
 * Created by gongkai on 2018/11/5.
 */

public class AppUtils {
    /**
     * 通过packagename启动应用
     *
     * @param context
     * @param packagename
     */
    public static void startAppByPkg(Context context, String packagename) {
        Intent intent = isInstall(packagename);
        if (intent == null) {
            Toasty.error(context, "无法启动该应用，请检查应用是否安装");
        }
        try {
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setAction(Intent.ACTION_MAIN);
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }

    /**
     * 通过packagename判断应用是否安装
     *
     * @param packagename
     * @return 跳转的应用主activity Intent
     */

    public static Intent isInstall(String packagename) {
        Intent intent = null;
        try {
            intent = getPkgManager().getLaunchIntentForPackage(packagename);
        } catch (Exception e) {
            Toasty.error(MainApplication.getContext(), "没有检测到安装该APP");
        }
        return intent;
    }

    public static void installApp(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse(path), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 判断手机是否安装某个应用
     *
     * @param appPackageName 应用包名
     * @return true：安装，false：未安装
     */
    public static boolean isApplicationAvilible(String appPackageName) {
        List<PackageInfo> pinfo = MainApplication.getPkgManager().getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (appPackageName.equals(pn)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void openDetail(Context context, String str) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", str, null));
        context.startActivity(intent);
    }
}
