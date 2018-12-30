package com.accessibilityservice.util;

import android.app.Notification;
import android.app.Service;
import android.os.Build;

import com.accessibilityservice.R;

/**
 * 通知工具类
 */
public class NotificationUtils {
    /**
     * 常住通知
     *
     * @param context
     */
    public static void showNotif(Service context) {
        Notification noti;
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(context.getString(R.string.app_name))
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentText(context.getString(R.string.app_name) + "运行中")
                .setContentTitle(context.getString(R.string.app_name));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            noti = builder.build();
        } else {
            noti = builder.getNotification();
        }
        noti.flags = Notification.FLAG_NO_CLEAR;
        context.startForeground(0xff, noti);
    }

}
