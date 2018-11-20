package com.accessibilityservice.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.accessibilityservice.MainApplication;
import com.accessibilityservice.activity.MainActivity;
import com.accessibilityservice.model.ActivityInfo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            Log.i("----", "无法启动该应用，请检查应用是否安装");
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
            Toasty.error(MainApplication.getContext(), "没有检测到安装该APP").show();
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

    public static ActivityInfo getTopActivity() {
        Matcher matcher = Pattern.compile("\\s*mSurface=Surface\\(name=([^\\s\\/]+)\\/([^\\s]+)\\b").matcher((Shell.exec("dumpsys window w | grep \\/ | grep name=")).trim());
        ActivityInfo activityInfo = new ActivityInfo();
        if (matcher.find()) {
            activityInfo.setPkgName(matcher.group(1).trim());
            activityInfo.setClsName(matcher.group(2).trim());
        }
        Log.i("----", activityInfo.getPkgName() + "/" + activityInfo.getClsName());
        return activityInfo;
    }

    public static String getTopPkg() {
        return getTopActivity().getPkgName();
    }

    public static String getTopCls() {
        return getTopActivity().getClsName();
    }

    public static boolean checkApkExist(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);

            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

    }

    public static void launchAppDetail(Context context, String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg))
                return;
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //如果设置了market包名 打开指定app市场
            if (!TextUtils.isEmpty(marketPkg))
                intent.setPackage(marketPkg);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setLauncherEnabled(Context context, boolean isEnable) {
        PackageManager var3 = MainApplication.getPkgManager();
        ComponentName var4 = new ComponentName(context, MainActivity.class);
        int var = isEnable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        try {
            var3.setComponentEnabledSetting(var4, var, PackageManager.DONT_KILL_APP);
        } catch (Exception var6) {
            var6.printStackTrace();
        }
    }

}
