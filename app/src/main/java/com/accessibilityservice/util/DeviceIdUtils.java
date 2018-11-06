package com.accessibilityservice.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.UUID;

public class DeviceIdUtils {
    public static String getDeviceId(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("a");
        try {
            @SuppressLint("WrongConstant") TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
            @SuppressLint({"HardwareIds", "MissingPermission"}) String deviceId = telephonyManager.getDeviceId();
            if (TextUtils.isEmpty(deviceId)) {
                @SuppressLint("MissingPermission") String simSerialNumber = telephonyManager.getSimSerialNumber();
                if (TextUtils.isEmpty(simSerialNumber)) {
                    simSerialNumber = getUUID(context);
                    if (!TextUtils.isEmpty(simSerialNumber)) {
                        stringBuilder.append("id");
                        stringBuilder.append(simSerialNumber);
                        return stringBuilder.toString();
                    }
                    return stringBuilder.toString();
                }
                stringBuilder.append("sn");
                stringBuilder.append(simSerialNumber);
                return stringBuilder.toString();
            }
            stringBuilder.append("imei");
            stringBuilder.append(deviceId);
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            stringBuilder.append("id").append(getUUID(context));
        }
        return stringBuilder.toString();
    }

    public static String getUUID(Context context) {
        String uuid = null;
        SharedPreferences sharedPreferences = context.getSharedPreferences("uuid", 0);
        if (sharedPreferences != null) {
            uuid = sharedPreferences.getString("uuid", "");
        }
        if (!TextUtils.isEmpty(uuid)) {
            return uuid;
        }
        uuid = UUID.randomUUID().toString();
        sharedPreferences.edit().putString("uuid", uuid).commit();
        return uuid;
    }
}
